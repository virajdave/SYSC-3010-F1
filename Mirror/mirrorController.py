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
import _thread, time

#device id
id = '-1'

stopFlag = 0

# Continuly runs to make sure the gui updates time and date independent of everything else
def tellGUIToUpdateTime(queue):
    global stopFlag
    while True :
        if stopFlag == 1:
            return
        hour = str(int(strftime("%I")))
        minute = strftime("%M")
        curTime = hour + ':' + minute
        queue.put_nowait(message('time', curTime))
        
        date = strftime("%A, %B %d %Y")
        queue.put_nowait(message('date', date))
        time.sleep(0.5)

# Every 15 mins fetches new weather data and gives the info to the gui to display
def tellGUIToUpdateWeather(queue):
        global id
        global stopFlag
        sleep = 1
        while True :
            if stopFlag == 1:
                return
            if (id != '-1'):
                sleep = 900
                queue.put_nowait(message('data', id + '/weather'))
            time.sleep(sleep)

# Every 15 mins fetches new weather data and gives the info to the gui to display
def tellGUIToUpdateThermo(queue):
        global id
        global stopFlag
        sleep = 1
        while True :
            if stopFlag == 1:
                return
            if (id != '-1'):
                sleep = 10
                queue.put_nowait(message('data', id + '/thermo'))
            time.sleep(sleep)			
			
# Every 30 seconds it sends out to get updated bus info 
def tellGUIToUpdateBusInfo(queue):
        global id
        global stopFlag
        sleep = 1
        while True:
                if stopFlag == 1:
                        return
                if (id != '-1'):
                        sleep = 30
                        queue.put_nowait(message('data', id + '/bus'))
                time.sleep(sleep)


def timeSync(queue):
        global id
        global stopFlag
        sleep = 1
        while True:
            if stopFlag == 1:
                return
            if (id != '-1'):
                sleep = 600
                queue.put_nowait(message('data', id + '/time'))
            time.sleep(sleep)
            
# Watches the recv queue for messages then ditributes them   
def watchRecvMessages(recvedQueue, sendingQueue, guiQueue):
        global stopFlag
        global id
        lastBeatTime = int(round(time.time() * 1000))
        while True:
                if stopFlag == 1:
                    return
                if not recvedQueue.empty():
                        while not recvedQueue.empty():
                                messageRecv = recvedQueue.get()
                                if (messageRecv.messageType == 'data'):
                                        if (messageRecv.info[0] == 'w'):
                                                weatherInfo = data_organizer(messageRecv.info[1:])
                                                guiQueue.put_nowait(message('weather', weatherInfo))
                                        if (messageRecv.info[0] == 'b'):
                                                busInfo = parseBusInfo(messageRecv.info[1:])
                                                guiQueue.put_nowait(message('bus', busInfo))
                                        if (messageRecv.info[0] == 'c'):
                                                guiQueue.put_nowait(message('colour', messageRecv.info[1:]))
                                        if (messageRecv.info[0] == 'd'):
                                                guiQueue.put_nowait(message('direction', messageRecv.info[1:]))
                                        if (messageRecv.info[0] == 't'):
                                                linux_set_time(messageRecv.info[1:])
                                        if (messageRecv.info[0] == 'h'):
                                                guiQueue.put_nowait(message('thermo', messageRecv.info[1:]))
                                elif(messageRecv.messageType == 'id'):
                                        lastBeatTime = int(round(time.time() * 1000))
                                        if (id == '-1'):
                                                setId(messageRecv.info)
                                elif(messageRecv.messageType == 'beat'):
                                        sendingQueue.put_nowait(message('beat', id))

                currentTime = int(round(time.time() * 1000))
                if (currentTime - lastBeatTime > 300000):
                    #If the server hasnt sent a beat in 5 mins server disconnected
                    id = '-1'
                    
                if (id == '-1'):
                    sendingQueue.put_nowait(message('beat', id))
                    time.sleep(10)
                                                 
                time.sleep(0.001)
            
            
#Starts up the network, controller and gui threads
def runController(server, port):
    global gui
    global stopFlag
    top = Tk()     #used as the root for the tk window
	
	# Queue setup
    guiRecvQueue = Queue()
    sendQueue = Queue()
    recvQueue = Queue()
	
    #Start up threads 
    gui = mirrorGUI(top, guiRecvQueue)
    _thread.start_new_thread(gui.runnerLoop, (guiRecvQueue,))
    _thread.start_new_thread(tellGUIToUpdateTime, (guiRecvQueue,))
    _thread.start_new_thread(tellGUIToUpdateWeather, (sendQueue,))
    _thread.start_new_thread(tellGUIToUpdateBusInfo, (sendQueue,))
    _thread.start_new_thread(tellGUIToUpdateThermo, (sendQueue,))
    _thread.start_new_thread(timeSync, (sendQueue,))
    
    # Start up networking side
    networkRun()
    _thread.start_new_thread(watchRecvMessages, (recvQueue,sendQueue,guiRecvQueue,))
    _thread.start_new_thread(networkInit, (server,port,recvQueue,sendQueue,))
    
    
    
    #Display the gui window
    gui.showGUI()

# changes the device id
def setId(num):
    global id
    id = num

# Sets flag for the controller to run
def run():
    global stopFlag
    stopFlag = 0

# sets flag to stop the controller	
def stop():
    global stopFlag
    stopFlag = 1
