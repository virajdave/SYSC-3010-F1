from mirror import *
from dataPassingObject import *
from queue import *
import _thread, time



# Queue setup
guiRecvQueue = Queue()
sendQueue = Queue()
recvQueue = Queue()

def tellGUIToUpdateTime():
	while True :
		hour = str(int(strftime("%I")))
		minute = strftime("%M")
		curTime = hour + ':' + minute
		guiRecvQueue.put_nowait(message('time', curTime))
		
		date = strftime("%B, %A %d %Y")
		guiRecvQueue.put_nowait(message('date', date))
		time.sleep(0.5)

def runController():
	_thread.start_new_thread(runnerLoop, (guiRecvQueue,))
	_thread.start_new_thread(tellGUIToUpdateTime, ())
	createmirrorGUI(guiRecvQueue)
	
runController()