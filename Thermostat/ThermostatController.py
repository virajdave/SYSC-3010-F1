from Thermostat import *
import thread
import time
import datetime
import string
from threading import Timer
import RPi.GPIO as GPIO
import socket, sys

class ThermostatController:
    themo = thermostatControl()
    id = -1
    t = Timer
   # serverStatus = False

    def setNewTemp(self, data):
        print ('temp to be set to ' + data)
        #data = 
        themo.setTemp()
        
    def runSystem(self):
        #creating datagram , sending heartbeat (setup)
        s = socket.socket(socket.AF_INET,  socket.SOCK_DGRAM)
        #port and address of server
        host = '10.0.0.1'
        port = 3010
        server_address = (host, port)

        #creating and sending inital packet to server (Discovery)
        data = '20/' + str(self.id) + '/3'
        print ('sending: ' + data)
        s.sendto(data.encode('utf-8'), server_address)
        
        while True:
            #clearing data input from server (in case of socket timeout)
            stringData = ''
            #   Wait for intructions from server
            buf, address = s.recvfrom(3010)
            stringData = buf.decode('utf-8')
            #if socket timed out
            if (stringData == ''):
                print('got: nothing'+ stringData)
                #prompts thermostat to check its current status
                self.themo.manageTemp()
                continue
            print('got: '+ stringData)
            #'01' means ACK from server
            if (stringData[:2] == '01'):
                #if device hasn't yet gotten an id from server set it
                if(self.id == -1):
                    print("ACK Received")
                    self.id = stringData[3:]
            #'00' is heartbeat from server
            elif (stringData[:2] == '00'):
                print ("heart beat received sending reply")
                message = '20/' + str(self.id)+ '/4'
                print (message)
                s.sendto(message.encode('utf-8'), server_address)
            # '02' is data from server
            elif (stringData[:2] == '02'):
                    print("data received")
                    self.setNewTemp(stringData[3:])
                    
            data = self.themo.ts.getTemp()
            #if last received message isn't an ACK update the current
            #temperature on the server
            if (stringData[:2] != '01'):
                data = ('22/' + str(self.id) + '/' + str(data))
                print ('sending to server: ' + data)
                s.sendto(data.encode('utf-8'), server_address)
            #prompts thermostat to check its current status
            self.themo.manageTemp()

Tc = ThermostatController ()
Tc.runSystem()



                    
