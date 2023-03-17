import serial
import time


class STM32(object):
    
	def __init__(self, port='/dev/ttyUSB0'):
		self.port = port
		self.baudrate = 115200
		self.isConnected = False

	def connect(self):
		while not self.isConnected:
			try:
				print("\nEstablishing connection with STM32 Board ...")

				self.ser = serial.Serial(self.port, self.baudrate, timeout=20)
				if (self.ser.is_open):
					print("[SUCCESSFUL CONNECTION]: Successfully established connection with STM32 Board.\n\n")
					self.isConnected = True

			except Exception as e:
				print(f"[ERROR] Unable to establish connection with STM32 Board: {str(e)}")
				print("Retrying to connect with STM32 Board ...")
				time.sleep(1)

	def disconnect(self):
		try:
			if (self.ser):  # Check there is a serial instance created
				print("STM32: Disconnecting from STM32 Board ...")
				self.ser.close()
				self.isConnected = False
		except Exception as e:
			print(f"[ERROR]: Unable to disconnect from STM32 Board: {str(e)}")


	def getisConnected(self):
		return self.isConnected

	def read(self):
		try:
			data = self.ser.read().decode("UTF-8")
			temp = self.ser.read()
			if data == '':  # No data read
				return "No reply"
			print(f"[FROM STM32] {data}")
			return data
		except Exception as e:
			print(f"[ERROR] STM32 Board read error: {str(e)}")

	def write(self, message):
		try:
			# Ensure connection is established before sending a message
			if self.isConnected:
				self.ser.write(message.encode('UTF-8'))
				print(f"[SENT TO STM32]: {message}")
			else:
				print("[Error]  Connection with STM32 board is not established")
		except Exception as e:
			print("[Error] Unable to send message from STM32: %s" % str(e))
