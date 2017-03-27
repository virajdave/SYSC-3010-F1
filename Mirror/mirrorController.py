from mirror import *
from dataPassingObject import *
from weather import *
from busInfo import *
from mirrorNetwork import *
from queue import *
import _thread, time



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
	while True :
		weatherData = data_organizer(data_fetch(url_builder('Ottawa,Ca')))
		guiRecvQueue.put_nowait(message('weather', weatherData))
		time.sleep(900)

def tellGUIToUpdateBusInfo():
	while True:
		busInfo = parseBusInfo(getBusInfo(urlBuilderBus(3031, 104)))
		guiRecvQueue.put_nowait(message('bus', busInfo))
		time.sleep(30)
	

def watchRecvMessages()	:
	while True:
			if not recvQueue.empty():
				while not recvQueue.empty():
					messageRecv = recvQueue.get()
					if (messageRecv.info[0] == 'w'):
						weatherInfo = data_organizer(messageRecv.info[1:])
						guiRecvQueue.put_nowait(message('weather', weatherInfo))
					if (messageRecv.info[0] == 'b'):
						busInfo = parseBusInfo(messageRecv.info[1:])
						guiRecvQueue.put_nowait(message('bus', busInfo))
					if (messageRecv.info[0] == 'c'):
						guiRecvQueue.put_nowait(message('colour', messageRecv.info[1:]))
			time.sleep(0.001)
			
			
#Starts up the network, controller and gui threads
def runController():
	top = Tk() 	#used as the root for the tk window
	#Start up threads 
	gui = mirrorGUI(top, guiRecvQueue)
	_thread.start_new_thread(gui.runnerLoop, (guiRecvQueue,))
	_thread.start_new_thread(tellGUIToUpdateTime, ())
	#_thread.start_new_thread(tellGUIToUpdateWeather, ())
	#_thread.start_new_thread(tellGUIToUpdateBusInfo, ())
	
	_thread.start_new_thread(mirrorNetRecv, (recvQueue,8080,))
	_thread.start_new_thread(watchRecvMessages, ())
	time.sleep(1)
	_thread.start_new_thread(sendFakeData, ())
	
	
	#Display the gui window
	gui.showGUI()
	
	
	
runController()