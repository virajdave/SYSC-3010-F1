#==============================================================================
#title           		:mirror_test.py
#description    		:tests features of the mirror
#author          		:Dillon Verhaeghe
#date            		:20170403
#version         		:0.0
#python_version :3 
#==============================================================================


import socket, time ,_thread
from mirrorController import *
from queue import *
from dataPassingObject import *


def test_updateWeather():
	testQueue = Queue()
	setId('10')
	run()
	_thread.start_new_thread(tellGUIToUpdateWeather, (testQueue,))
	time.sleep(1)
	stop()
	returned = testQueue.get_nowait()
	assert returned.messageType == 'data', "Weather update did not have type data"
	assert returned.info == '10/weather', "Weather update did not have info 10/weather"
	
def test_updateBusInfo():
	testQueue = Queue()
	setId('10')
	run()
	_thread.start_new_thread(tellGUIToUpdateBusInfo, (testQueue,))
	time.sleep(1)
	stop()
	returned = testQueue.get_nowait()
	assert returned.messageType == 'data', "Bus update did not have type data"
	assert returned.info == '10/bus', "Bus update did not have info 10/bus"
	


    

