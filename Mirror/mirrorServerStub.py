#==============================================================================
#title           	    :mirrorServerStub.py
#description     	:Preforms server operations to test mirror
#author          	    :Dillon Verhaeghe
#date            	    :20170326
#version         	    :0
#python_version :3 
#==============================================================================
import socket, sys, time
from queue import *
import _thread
from mirrorController import *

stubStop = 0
mirrorAddress = 0
def mirrorServer():
    global stubStop
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    port = 6000
    server_address = ('localhost', port)
    s.bind(server_address)
    queue = Queue()
    _thread.start_new_thread(stubRecver, (s,queue,))
    stubSender(s,queue,)


def stubSender(s, queue):
    global stubStop
    global mirrorAddress
    id = 20
    timeRef = 1491518078388
    lastTime = int(round(time.time() * 1000))
    while True:
        if stubStop:
            return
        if not queue.empty():
            while not queue.empty():
                got = queue.get()
                if got[:2] == '20':
                    send = '01/' + str(id) 
                    s.sendto(send.encode('utf-8'), mirrorAddress)
                if got[:2] == '22':
                    if got[6:] == 'weather':
                        send  = '02/w{\"coord\":{\"lon\":-75.7,\"lat\":45.41},\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"base\":\"stations\",\"main\":{\"temp\":2.51,\"pressure\":1030,\"humidity\":51,\"temp_min\":2,\"temp_max\":3},\"visibility\":24140,\"wind\":{\"speed\":1.5,\"deg\":230},\"clouds\":{\"all\":20},\"dt\":1490482800,\"sys\":{\"type\":1,\"id\":3694,\"message\":0.0039,\"country\":\"CA\",\"sunrise\":1490439242,\"sunset\":1490484206},\"id\":6094817,\"name\":\"Ottawa\",\"cod\":200}'
                        s.sendto(send.encode('utf-8'), mirrorAddress)
                    if got[6:] == 'thermo':
                        send  = '02/h10'
                        s.sendto(send.encode('utf-8'), mirrorAddress)
                    if got[6:] == 'bus':
                        send  = '02/b{"GetNextTripsForStopResult":{"StopNo":"3031","StopLabel":"SMYTH","Error":"","Route":{"RouteDirection":[{"RouteNo":104,"RouteLabel":"Place d\'Orl\u00e9ans","Direction":"Eastbound","Error":"","RequestProcessingTime":"20170326194147","Trips":{"Trip":[{"TripDestination":"Place d\'Orl\u00e9ans","TripStartTime":"19:38","AdjustedScheduleTime":"9","AdjustmentAge":"0.86","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"45.384907","Longitude":"-75.694578","GPSSpeed":"20.3"},{"TripDestination":"Place d\'Orl\u00e9ans","TripStartTime":"20:08","AdjustedScheduleTime":"37","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""},{"TripDestination":"Place d\'Orl\u00e9ans","TripStartTime":"20:38","AdjustedScheduleTime":"67","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""}]}},{"RouteNo":104,"RouteLabel":"Carleton","Direction":"Westbound","Error":"","RequestProcessingTime":"20170326194147","Trips":{"Trip":[{"TripDestination":"Carleton","TripStartTime":"19:31","AdjustedScheduleTime":"16","AdjustmentAge":"0.71","LastTripOfSchedule":false,"BusType":"6E - 60","Latitude":"45.451344","Longitude":"-75.584719","GPSSpeed":"13.7"},{"TripDestination":"Carleton","TripStartTime":"20:01","AdjustedScheduleTime":"44","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""},{"TripDestination":"Carleton","TripStartTime":"20:31","AdjustedScheduleTime":"74","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""}]}}]}}}'
                        s.sendto(send.encode('utf-8'), mirrorAddress)
                    if got[6:] ==  'time':
                        timeRef += 10000
                        send = '02/t' + str(timeRef)
                currentTime = int(round(time.time() * 1000))
                if currentTime - lastTime > 3000:
                    beat = '00'
                    s.sendto(beat.encode('utf-8'), mirrorAddress)
                    lastTime = int(round(time.time() * 1000))
				
def stubRecver(s, sendQueue):
    global stubStop
    global mirrorAddress
    while True:
        if stubStop:
            return
        
        buf, address = s.recvfrom(1000)
        mirrorAddress = address
        sendQueue.put_nowait(buf.decode('utf-8'))
    
def runStub():
    global stubStop
    stubStop = 0
    
def stopStub() :
    global stubStop
    stubStop = 1
	
runStub()
_thread.start_new_thread(mirrorServer, ())
runController('127.0.0.1',6000)
