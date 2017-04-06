#==============================================================================
#title           	    :mirrorServerStub.py
#description     	:Preforms server operations to test mirror
#author          	    :Dillon Verhaeghe
#date            	    :20170326
#version         	    :0
#python_version :3 
#==============================================================================
import socket, sys, time
import Queue

stubStop = 0
mirrorAddress = 0
def mirrorServer():
    global stubStop
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    port = 6000
    queue = Queue()
    _thread.start_new_thread(stubRecver, (s,queue,))
    _thread.start_new_thread(stubSender, (s,queue,))


def stubSender(s, queue):
    global stubStop
    global mirrorAddress
    id = 20
    while True:
        if stubStop:
            return
        if not queue.empty():
            while not queue.empty():
                got = queue.get()
                if got[:2] == '20':
                    send = '01/' + id 
                    s.sendto(send.encode('utf-8'), mirrorAddress)
                if got[:2] == '22':
                    if got[6:] == 'weather':
                        send  = '02/w{"coord":{"lon":-75.7,"lat":45.41},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02d"}],"base":"stations","main":{"temp":2.51,"pressure":1030,"humidity":51,"temp_min":2,"temp_max":3},"visibility":24140,"wind":{"speed":1.5,"deg":230},"clouds":{"all":20},"dt":1490482800,"sys":{"type":1,"id":3694,"message":0.0039,"country":"CA","sunrise":1490439242,"sunset":1490484206},"id":6094817,"name":"Ottawa","cod":200}'
                        s.sendto(send.encode('utf-8'), mirrorAddress)
    
def stubRecver(s, sendQueue):
    global stubStop
    global mirrorAddress
    while True:
        if stubStop:
            return 
        buf, address = s.recvfrom(1000)
        mirrorAddress = address
        sendQueue.put_nowait(buf)
    
def runStub():
    global stubStop
    stubStop = 0
    
def stopStub() :
    global stubStop
    stubStop = 1