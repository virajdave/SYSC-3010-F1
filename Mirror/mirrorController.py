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

# Continuly runs to make sure the gui updates time and date independent of everything else
def tellGUIToUpdateTime(queue):
    while True :
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
        sleep = 1
        print(id)
        while True :
                if (id != '-1'):
                        sleep = 900
                        queue.put_nowait(message('data', id + '/weather'))
                time.sleep(sleep)

# Every 30 seconds it sends out to get updated bus info 
def tellGUIToUpdateBusInfo(queue):
        global id
        sleep = 1
        while True:
                if (id != '-1'):
                        sleep = 30
                        queue.put_nowait(message('data', id + '/bus'))
                time.sleep(sleep)


def timeSync(queue):
        global id
        sleep = 1
        while True:
            if (id != '-1'):
                sleep = 600
                queue.put_nowait(message('data', id + '/time'))
            time.sleep(sleep)
            
# Watches the recv queue for messages then ditributes them   
def watchRecvMessages(recvedQueue, sendingQueue, guiQueue):
        global id
        while True:
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
                                elif(messageRecv.messageType == 'id'):
                                        if (id == '-1'):
                                                id = messageRecv.info
                                elif(messageRecv.messageType == 'beat'):
                                        sendingQueue.put_nowait(message('beat', id))
                if (id == '-1'):
                    sendingQueue.put_nowait(message('beat', id))
                    time.sleep(10)
                                                 
                time.sleep(0.001)
            
            
#Starts up the network, controller and gui threads
def runController(server, port):
    global gui
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
    _thread.start_new_thread(timeSync, (sendQueue,))
    
    #_thread.start_new_thread(mirrorNetRecv, (recvQueue,8080,))
    _thread.start_new_thread(watchRecvMessages, (recvQueue,sendQueue,guiRecvQueue,))
    _thread.start_new_thread(networkInit, (server,port,recvQueue,sendQueue,))
    #time.sleep(1)
    #_thread.start_new_thread(sendFakeData, ())
    
    
    #Display the gui window
    gui.showGUI()


