#==============================================================================
#title           		:mirrorController.py
#description     	        :Starts up and sets up everything for the mirror
#author          		:Dillon Verhaeghe
#date            		:20170326
#version         		:0.4
#usage           		:python3 mirrorRunner.py 10.0.0.1 3010
#python_version :3 
#==============================================================================


from mirror import *
from dataPassingObject import *
from weather import *
from busInfo import *
from mirrorNetwork import *
from systemTime import *
from queue import *
from serverMock import *
import _thread, time

#device id
id = '-1'

# Queue setup
guiRecvQueue = Queue()
sendQueue = Queue()
recvQueue = Queue()

# Continuly runs to make sure the gui updates time and date independent of everything else
def tellGUIToUpdateTime():
    while True :
        hour = str(int(strftime("%I")))
        minute = strftime("%M")
        curTime = hour + ':' + minute
        guiRecvQueue.put_nowait(message('time', curTime))
        
        date = strftime("%A, %B %d %Y")
        guiRecvQueue.put_nowait(message('date', date))
        time.sleep(0.5)

# Every 15 mins fetches new weather data and gives the info to the gui to display
def tellGUIToUpdateWeather():
        global id
        sleep = 1
        while True :
                if (id != '-1'):
                        sleep = 900
                        sendQueue.put_nowait(message('data', id + '/weather'))
                time.sleep(sleep)

# Every 30 seconds it sends out to get updated bus info 
def tellGUIToUpdateBusInfo():
        global id
        sleep = 1
        while True:
                if (id != '-1'):
                        sleep = 30
                        sendQueue.put_nowait(message('data', id + '/bus'))
                time.sleep(sleep)


def timeSync():
        global id
        sleep = 1
        while True:
            if (id != '-1'):
                sleep = 600
                sendQueue.put_nowait(message('data', id + '/time'))
            time.sleep(sleep)
            
# Watches the recv queue for messages then ditributes them   
def watchRecvMessages():
        global id
        while True:
                if not recvQueue.empty():
                        while not recvQueue.empty():
                                messageRecv = recvQueue.get()
                                if (messageRecv.messageType == 'data'):
                                        if (messageRecv.info[0] == 'w'):
                                                weatherInfo = data_organizer(messageRecv.info[1:])
                                                guiRecvQueue.put_nowait(message('weather', weatherInfo))
                                        if (messageRecv.info[0] == 'b'):
                                                busInfo = parseBusInfo(messageRecv.info[1:])
                                                guiRecvQueue.put_nowait(message('bus', busInfo))
                                        if (messageRecv.info[0] == 'c'):
                                                guiRecvQueue.put_nowait(message('colour', messageRecv.info[1:]))
                                        if (messageRecv.info[0] == 'd'):
                                                guiRecvQueue.put_nowait(message('direction', messageRecv.info[1:]))
                                        if (messageRecv.info[0] == 't'):
                                                linux_set_time(messageRecv.info[1:])
                                elif(messageRecv.messageType == 'id'):
                                        if (id == '-1'):
                                                id = messageRecv.info
                                elif(messageRecv.messageType == 'beat'):
                                        sendQueue.put_nowait(message('beat', id))
                if (id == '-1'):
                    sendQueue.put_nowait(message('beat', id))
                    time.sleep(10)
                                                 
                time.sleep(0.001)
            
            
#Starts up the network, controller and gui threads
def runController(server, port):
    global gui
    top = Tk()     #used as the root for the tk window
    #Start up threads 
    gui = mirrorGUI(top, guiRecvQueue)
    _thread.start_new_thread(gui.runnerLoop, (guiRecvQueue,))
    _thread.start_new_thread(tellGUIToUpdateTime, ())
    _thread.start_new_thread(tellGUIToUpdateWeather, ())
    _thread.start_new_thread(tellGUIToUpdateBusInfo, ())
    _thread.start_new_thread(timeSync, ())
    
    #_thread.start_new_thread(mirrorNetRecv, (recvQueue,8080,))
    _thread.start_new_thread(watchRecvMessages, ())
    _thread.start_new_thread(networkInit, (server,port,recvQueue,sendQueue,))
    #time.sleep(1)
    #_thread.start_new_thread(sendFakeData, ())
    
    
    #Display the gui window
    gui.showGUI()


