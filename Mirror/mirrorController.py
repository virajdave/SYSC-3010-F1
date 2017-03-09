from mirror import *
from dataPassingObject import *
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

#Starts up the network, controller and gui threads
def runController():
	top = Tk() 	#used as the root for the tk window
	#Start up threads 
	gui = mirrorGUI(top, guiRecvQueue)
	_thread.start_new_thread(gui.runnerLoop, (guiRecvQueue,))
	_thread.start_new_thread(tellGUIToUpdateTime, ())
	
	#Display the gui window
	gui.showGUI()
runController()