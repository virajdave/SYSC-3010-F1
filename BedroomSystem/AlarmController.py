from AlarmSystem import *
import thread
import time
import datetime
import string
from threading import Timer
import RPi.GPIO as GPIO
import socket, sys

class AlarmController:
    alarm = AlarmSystem()
    id = -1
    t = Timer

     #starts new alarm/ disables any pre-existing alarms
    def newAlarm (self, data):
        if (self.alarm.alarmOn):
            print ('disabling old alarm')
            self.t.cancel()
            self.alarm.alarmOn = False
            
        if ( validTime(data) == False):
            print('alarm not valid')
            return
        dataH = data[4:]
        #print ('data = '+ data)
        dataH = dataH[:2]
        #print ('data h = ' + dataH)
        dataM = data[7:]
        
        h = int (dataH)
        m = int (dataM)
        now = datetime.datetime.now()        
        seconds = (h - now.hour) *3600 + (m - now.minute)*60 - now.second
        print ("Sleeping for " + str(seconds) )
        self.t = Timer(seconds, self.alarm.manageAlarm, )
        self.t.start()
        self.alarm.alarmOn = True
            #sleep(5)
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
            print(buf)
            stringData = buf.decode('utf-8')
            
            print('got: '+ stringData)
            if (stringData[:2] == '01'):
                print("id = "+ stringData[3:])
                if (self.id == -1):
                    self.id = stringData[3:]
            elif (stringData[:2] == '00'):
                print("heartbeat response")
                message = '20/' + str(self.id)+ '/4'
                s.sendto(message.encode('utf-8'), server_address)
            elif (stringData[:2] == '02'):
                    print("data received")
                    sd = stringData[3:]
                    print('data = ' + sd)
                    if (sd[:1] == 't'):
                        linux_set_time(sd[1:])
                    elif:(sd[:1] == 'lo'):
                        self.alarm.lights(True)
                    elif:(sd[:1] == 'lf'):
                        self.alarm.lights(True)
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
            #for testing to be removed
            h = datetime.datetime.now().hour
            m = datetime.datetime.now().minute + 1
            stringDataT = '02/a'+ str(h) +","+ str(m)
            if (self.alarm.alarmOn == False):
                print('Data T= '+stringDataT)
                self.newAlarm(stringDataT)
            
        # for now assuming alarm message with contents
            self.alarm.lights(True)
            sleep(1)
            self.alarm.lights(False)
            #end of testing
            

def validTime(data):
    
    h = datetime.datetime.now().hour
    m = datetime.datetime.now().minute
    #print ("data " + data[3:])
    dataH = data[4:]
    #print ('l'+data)
    dataH = dataH[:2]
    
    #print ('data h = ' + dataH)
    dataM = data[7:]
    #print ('dataM' + dataM)
    if (int(dataH) >= h -12 ):
        if (int(dataM) >= m):
            #print('alarm Valid')
            return True
    return False
ac = AlarmController()
ac.runSystem()

#takes as input number of ms since 1970 getting info from server
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


