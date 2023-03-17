import os
import socket
import time
import cv2
import imagezmq
import serial
from picamera import PiCamera
from picamera.array import PiRGBArray
from Android import Android
from multiprocessing import Process, Queue  # Manage multi-thread programming

img_directory = "/home/pi/mdp-g20/fixrpi/picamera_images/"
image_processing_server_url = "tcp://192.168.20.25:5555"
image_count = 0  # keep track of the images detected


def move_stm(instr):
	"""
	Move STM32 using a list of instructions

	Args:
		instr_list: list of instructions to be sent to STM32 (list)
	"""

	stm.write(instr.encode())
	while True:
		raw_dat = stm.read(1)
		dat = raw_dat.strip().decode()
		if dat == "R":
			return


def take_pic():
	try:
		rawCapture = PiRGBArray(camera)
		camera.capture(rawCapture, format="bgr")
		image = rawCapture.array
		print("[TAKE PIC] Image successfully taken.")

	except Exception as e:
		print(f"[TAKE PIC][ERROR] Unable to take pic: {str(e)}")

	return image


def process_image(image):

	image_sender = imagezmq.ImageSender(connect_to=image_processing_server_url)
	global image_count, image_id_lst
	try:
		rpi_name = socket.gethostname()  # send RPi hostname with each image
		reply = image_sender.send_image(rpi_name, image)
		reply = reply.decode('utf-8')

		if reply == "n":  # no image detected
			class_id = f"n"

		elif reply == "00" or reply == '0':  # bulls eye
			class_id = "bullseye"

		elif reply == "38":
			class_id = "right"

		elif reply == "39":
			class_id = "left"

		img_file_name = f'i{image_count}_{class_id}.jpg'
		cv2.imwrite(os.path.join(img_directory, img_file_name), image)
		image_sender.close()
		return class_id

	except Exception as e:
		print(f"[ERROR] Image processing failed: {str(e)}")


def readMsg(queue, interface):
	''' Read and put message into the queue'''
	while True:
		try:
			msg = interface.read()
			if msg is None:
				continue
			queue.put(msg)
		except Exception as e:
			print(f"[READ ERROR]: {str(e)}")


if __name__ == "__main__":
	stm = serial.Serial()
	stm.baudrate = 115200
	stm.port = "/dev/ttyUSB0"
	print(stm.open())
	print("[MAIN][SUCCESSFUL CONNECTION] STM ready.\n")
	interfaces = []
	interfaces.append(Android())
	ANDROID = 0
	queue = Queue()
	android = Process(target=readMsg, args=(queue, interfaces[ANDROID]))
	for interface in interfaces:
		interface.connect()  

	android.start()  
	print('[MAIN][SUCCESSFUL] Android connected')
	camera = PiCamera()
	print("[MAIN][SUCCESSFUL CONNECTION] PiCamera ready.\n")

	try:
		while True:
			msg = queue.get()
			if 'START' in msg:  
				break

	except Exception as e:
		print(f"[MAIN][ERROR] {str(e)}")
		for i in interfaces:
			i.disconnect()

	finally:
		for i in interfaces:
			i.disconnect()
		android.terminate()

	init = ['US225']  # move until 25cm before 1st obstacle

	case_l = [
		# scan 1 = left
		'FL057', 'FR057',
		# move to 2nd obs
		'US030',  # move until 30cm before 2nd obstacle and save d to buff 0
		'FR020',
	]

	case_r = [
		# scan 1 = right
		'FR057', 'FL057',
		# move to 2nd obs
		'US030',  # move until 30cm before 2nd obstacle and save d to buff 0
		'FL020',
	]

	case_ll = [
		# scan 2 = left
		'BR020', 'FL067', 'FR067', 'FW025', 'FR090', 'FW060', 'FR090',
		'RT070',  # d + 70
		'FR090', 'FL090',
		# back to base
        'US120', # move until 20cm before parking wall
		'US120'  # move until 20cm before parking wall
	]

	case_lr = [
		# scan 2 = right
		'FR070', 'FW025', 'FL090', 'FW025', 'FL090', 'FW060', 'FL090',
		'RT075',  # d + 75
		'FL090', 'FR090',
		# back to base
        'US120', # move until 20cm before parking wall
		'US120'  # move until 20cm before parking wall
	]

	case_rr = [
		# scan 2 = left
		'BL020', 'FR067', 'FL067', 'FW025', 'FL090', 'FW060', 'FL090',
		'RT070',  # d + 70
		'FL090', 'FR090',
		# back to base
        'US120', # move until 20cm before parking wall
		'US120'  # move until 20cm before parking wall
	]

	case_rl = [
		# scan 2 = right
		'FL070', 'FW025', 'FR090', 'FW025', 'FR090', 'FW060', 'FR090',
		'RT075',  # d + 75
		'FR090', 'FL090',
		# back to base
        'US120', # move until 20cm before parking wall
		'US120'  # move until 20cm before parking wall
	]

	instr_list = []

	img1 = "left"
	img2 = "left"

	move_stm(init[0])
	image = take_pic()
	img1 = process_image(image)
	print(f'got img1:{img1}')
	if "right" in img1:
		# move_stm(case_r)
		for instr in case_r:
			print(f'instruction:{instr}')
			move_stm(instr=instr)
		# time.sleep(10)
		image2 = take_pic()
		img2 = process_image(image2)
		print(f'got img2:{img2}')

	elif "left" in img1:
		# move_stm(case_l)
		# time.sleep(10)
		for instr in case_l:
			print(f'instruction:{instr}')
			move_stm(instr=instr)
		image2 = take_pic()
		img2 = process_image(image2)
		print(f'got img2:{img2}')
	# Checking if the first image is left and the second image is left.
	if "left" in img1 and "left" in img2:
		for instr in case_ll:
			move_stm(instr)

	# Checking if the first image is left and the second image is right.
	if "left" in img1 and "right" in img2:
		for instr in case_lr:
			move_stm(instr)
	# Checking if the first image is right and the second image is left.
	if "right" in img1 and "left" in img2:
		for instr in case_rl:
			move_stm(instr)
	# Checking if the first image is right and the second image is right.
	if "right" in img1 and "right" in img2:
		for instr in case_rr:
			move_stm(instr)
