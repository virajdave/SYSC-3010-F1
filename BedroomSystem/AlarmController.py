from AlarmSystem import *
import thread
import time
import datetime
import string
from threading import Timer
import RPi.GPIO as GPIO
import socket, sys

#takes as input number of ms since 1970 getting info from server
#sets the current system time to the value from server
def linux_set_time(milliSeconds):
    formatedTime = datetime.datetime.fromtimestamp(int(milliSeconds)/1000)
    time_tuple = (formatedTime.year, formatedTime.month,formatedTime.day,formatedTime.hour,formatedTime.minute,formatedTime.second,formatedTime.microsecond)
    systemCall = 'sudo date -s "'
    systemCall += str(formatedTime.day)
    systemCall += ' ' + formatedTime.strftime('%B')
    systemCall += ' ' + str(formatedTime.year)
    systemCall += ' ' + str(formatedTime.hour) + ':'
    systemCall += str(formatedTime.minute) + ':'
    systemCall += str(formatedTime.second) + '"'
    os.system(systemCall)

class AlarmController:
    alarm = AlarmSystem()
	#invalid default id
    id = -1
    t = Timer

     #starts new alarm/ disables any pre-existing alarms
	 #checks if new alarm time is valid if not the alarm will not be set
    def newAlarm (self, data):
        if (self.alarm.alarmOn):
            print ('disabling old alarm')
            self.t.cancel()
            self.alarm.alarmOn = False
            
        if ( validTime(data) == False):
            print('alarm not valid')
            return
			#pulling hour, minute info out of message string from server
        dataH = data[9:]
        dataH = dataH[:2]
        dataM = data[12:]
        
        h = int (dataH)
        m = int (dataM)
        now = datetime.datetime.now()        
        seconds = (h - now.hour) *3600 + (m - now.minute)*60 - now.second
        print ("Sleeping for " + str(seconds) )
        self.t = Timer(seconds, self.alarm.manageAlarm, )
        self.t.start()
        self.alarm.alarmOn = True
		
    def runSystem(self):
        #creating datagram , sending heartbeat (setup)
        s = socket.socket(socket.AF_INET,  socket.SOCK_DGRAM)
        host = '10.0.0.1'
        port = 3010
        server_address = (host, port)
        data = '20/' + str(self.id) + '/4'
        print ('sending: ' + data)
        s.sendto(data.encode('utf-8'), server_address)
        sync = 100

        #
        
        #
        while True:

        #   Wait for intructions from server
            buf, address = s.recvfrom(3010)
            stringData = buf.decode('utf-8')
            #decode message to instruction
			#01 server ack comes with device id
            if (stringData[:2] == '01'):
                
                if (self.id == -1):
                    self.id = stringData[3:]
                    print("id = "+ str(self.id))
                continue
				#heartbeat respond with heartbeat message
            elif (stringData[:2] == '00'):
                print("heartbeat response")
                message = '20/' + str(self.id)+ '/4'
                s.sendto(message.encode('utf-8'), server_address)
				#data from server decode if lights, time sync, or alarm
            elif (stringData[:2] == '02'):
                    print("data received")
                    sd = stringData[3:]
                    print (sd)
                    print('data = ' + sd)
                    if (sd[:1] == 't'):
                        linux_set_time (sd[1:])
                    elif(sd == 'l/1'):
                        self.alarm.lights(True)
                    elif(sd == 'l/0'):
                        self.alarm.lights(False)
                    else:
                        self.newAlarm(stringData)
            #sync exists to setup request for server to get its current time
            if (sync < 100):
                sync +=1
            else:
                print ('sending time sync')
                sync = 0
                message = '22/' + self.id + '/time'
                s.sendto(message.encode('utf-8'), server_address)            
            #updates the server on the current status of the lights
            if (self.alarm.lightsOn == True): 
                message = '22/' + self.id + '/LO'
                s.sendto(message.encode('utf-8'), server_address)
            if (self.alarm.lightsOn == False): 
                message = '22/' + self.id + '/LF'
                s.sendto(message.encode('utf-8'), server_address)
            
#compares alarm time vs current time alarms cannot be set in the past
def validTime(data):
    
    h = datetime.datetime.now().hour
    m = datetime.datetime.now().minute
    dataH = data[9:]
    dataH = dataH[:2]

    dataM = data[12:]

    if (int(dataH) >= h ):
        if (int(dataM) >= m):

            return True
    return False
#start up
ac = AlarmController()
ac.runSystem()



