
import socket, sys, time, _thread

from AlarmSystem import*
#port = 100
#currentAlarm
heartBeat = [0,0]
hReply = [2,0]
ACK = [0, 1]
aReply = [2,1]
data = [0, 2]
dReply = [2,2]
#
#server_address = ('localhost', port)

class AlarmController:
    alarmOn = False
    print("hello")
    #s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    Alarm = AlarmSystem()
    #s.bind(server_address)
    #s.settimeout(1)
    print("Alarm Controller activated sending inital message")
    #data = hReply
    #s.sendto(data, server_address)
    #s.recvfrom(port)
    
    #while True:
    
        
        
    def setAlarm (self, h, m):
        #if (self.alarmOn == False):
        #    print ("Alarm Running termiating")
        self.Alarm.manageAlarm (h,m)
        #currentAlarm = _thread.start_new_thread(self.Alarm.manageAlarm,( m,h))
        d = aReply
        #s.sendto(data, server_address)0
        alarmOn = True
        
    def getLightInfo (self):
        return self.Alarm.lightsOn
        
    def setLight (change):
        Alarm.lights(Alarm, change)
print ("hello") 
ac = AlarmController()
ac.setAlarm ( 10, 41)