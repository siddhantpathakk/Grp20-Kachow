import numpy as np
import json
import socket
import json

from Algorithm.main import RunMain



class Client:


    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.socket = socket.socket()

    def connect(self):
        print("Connection")
        print(f"Attempting connection to ALGO at {self.host}:{self.port}")
        self.socket.connect((self.host, self.port))
        print("Connected to ALGO!")

    def send(self, d):
        data = d.encode()
        self.socket.send(data)

    def receive(self):
        msg = self.socket.recv(1024)
        data = msg.decode()
        return (data)

    def is_json(self, msg):
        try:
            data = json.loads(msg)
            d = data["obstacle1"]
            return data
        except Exception:
            print( "Exception occured")
            return False

    def close(self):
        print("Closing client socket.")
        self.socket.close()


def runAlgorithm():
    try:
        client = Client("192.168.20.1", 3004)
        client.connect()
        print("Algorithm PC successfully connected to Raspberry Pi...")

    except Exception as e:
        print("[ALG-CONNECTION ERROR]", str(e))

    filename1 = 'mapFromAndroid.json'
    filename2 = 'commands2stm.json'

    while True:
        try:
            print("\nReceive Obstacles Data\n")
            print("Waiting to receive obstacle data from ANDROID...")

            obstacle_data = client.receive() 
            data2 = obstacle_data 

            print("Received all obstacles data from ANDROID.")
            print("Obstacles data: {data2}")

            with open(filename1, "w") as f:  
                json.dump(data2, f, indent=4)

            commands, obsOrder = RunMain()  
            print("\nFull list of STM commands till last obstacle:")
            print(f'{commands}') 
            print(f'The order of visiting obstacles is:\n', obsOrder)
            all_cmd_str = ','.join(str(e) for e in commands)
            all_obs_str = ','.join(str(e) for e in obsOrder)
            all_str = all_cmd_str + "$" + all_obs_str
            client.send(all_str)


        except KeyboardInterrupt:
            client.close()
            break

        except Exception as e:
            print('[MAIN CONNECT FUNCTION ERROR]', str(e))
            client.close()
            break


# Run the system
if __name__ == '__main__':
    runAlgorithm()
