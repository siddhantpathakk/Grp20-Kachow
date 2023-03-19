from pickle import TRUE
import time
import socket
import os


class AlgoPC(object):

	def __init__(self):
		self.isConnected = False
		self.client = None
		self.addr = None
		HOST = '192.168.20.1' 
		self.PORT = 3004
		self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		print('Socket created')
        # managing error exception
		try:
			self.server.bind((HOST, self.PORT))
			self.server.listen(1)
		except socket.error:
			print('Socket Bind failed \n\n')

	def getisConnected(self):
		return self.isConnected

	def connect(self):
		print(f"\nWaiting for connection with ALgo PC")
		while self.isConnected == False:
			try:
				if self.client is None:

					print('Socket awaiting messages')
					self.client, self.addr = self.server.accept()
					print(
						f'[SUCCESSFUL CONNECTION] Algo PC Connected at {self.client};{self.addr}\n\n')
					self.isConnected = True

			except Exception as e:
				print(f"[ERROR] Unable to establish connection with Algo PC\n\n")
				self.client.close()
				self.client = None
				print("Retrying to connect with Algo PC ...")
				time.sleep(1)
				self.connect()

	def disconnect(self):
		try:
			print("Algo PC: Shutting down Server ...")
			self.server.close()
			self.server = None
			print("Algo PC: Shutting down Client ...")
			self.client.close()
			self.client = None
			self.isConnected = False
		except Exception as e:
			print("f[ERROR]: Unable to disconnect from Algo PC: {str(e)}")
			os.system('sudo fuser -k %d/tcp' % (self.PORT))

	def read(self):
		try:
			while True:
				msg = self.client.recv(1024).strip().decode('utf-8')
				if len(msg) == 0:
					break
				print(f"[FROM ALGOPC] {msg}")
				return msg

		except Exception as e:
			print(f"[ERROR] Algo PC read error: {str(e)}")
			try:
				self.server.getpeername()
			except:
				self.client.close()
				self.isConnected = False
				self.client = None
				print("Retrying to connect with Algo PC ...")
				self.connect()
				print("Trying to read message from Algo PC again...")
				self.read()
		return msg

	def write(self, message):
		try:
			message = message.encode("utf-8")
			if self.isConnected:
				self.client.send(message)
				print(f"[SENT TO Algo PC]: {message}")
			else:
				print("[Error]  Connection with Algo PC is not established")

		except Exception as e:
			print(f"[ERROR] Algo PC write error: {str(e)}")
			try:
				self.server.getpeername()
			except:
				self.client.close()
				self.isConnected = False
				self.client = None
				print("Retrying to connect with Algo PC  ...")
				self.connect()
				print("Trying to send the message to Algo PC again...")
				self.write(self, message)
