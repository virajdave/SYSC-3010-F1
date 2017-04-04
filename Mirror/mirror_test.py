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


def test_guiUpdateTime():
	testQueue = Queue()
	setId('10')
	run()
	_thread.start_new_thread(tellGUIToUpdateTime, (testQueue,))
	time.sleep(0.01)
	stop()
	returned = testQueue.get_nowait()
	assert (returned.messageType == 'time'), "time update did not have type time"

def test_updateWeather():
	testQueue = Queue()
	setId('10')
	run()
	_thread.start_new_thread(tellGUIToUpdateWeather, (testQueue,))
	time.sleep(1)
	stop()
	returned = testQueue.get_nowait()
	assert (returned.messageType == 'data'), "Weather update did not have type data"
	assert (returned.info == '10/weather'), "Weather update did not have info 10/weather"
	
def test_updateBusInfo():
	testQueue = Queue()
	setId('10')
	run()
	_thread.start_new_thread(tellGUIToUpdateBusInfo, (testQueue,))
	time.sleep(1)
	stop()
	returned = testQueue.get_nowait()
	assert (returned.messageType == 'data'), "Bus update did not have type data"
	assert (returned.info == '10/bus'), "Bus update did not have info 10/bus"
	
	
def test_timeSync():
	testQueue = Queue()
	setId('10')
	run()
	_thread.start_new_thread(timeSync, (testQueue,))
	time.sleep(1)
	stop()
	returned = testQueue.get_nowait()
	assert (returned.messageType == 'data'), "Sync  did not have type data"
	assert (returned.info == '10/time'), "Sync  did not have info 10/time"

def test_RecvMessages_weather():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    run()
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    weatherDataFile = open('testData/weatherTest.txt', 'r')
    weatherData = weatherDataFile.read()
    testRecv.put_nowait(message('data', 'w' + weatherData))
    time.sleep(1)
    stop()
    returned = testgui.get_nowait()
    expected = data_organizer(weatherData)
    assert (returned.messageType == 'weather'), 'Recved message did not give message of type weather back'
    assert (set(returned.info.keys()) == set(expected.keys())), 'Recved messaged did not give the correct weather data back'
    
def test_RecvMessages_bus():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    run()
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    busDataFile = open('testData/busTest.txt', 'r')
    busData = busDataFile.read()
    testRecv.put_nowait(message('data', 'b' + busData))
    time.sleep(1)
    stop()
    returned = testgui.get_nowait()
    expected = parseBusInfo(busData)
    assert (returned.messageType == 'bus'), 'Recved message did not give message of type bus back'
    assert (set(returned.info.keys()) == set(expected.keys())), 'Recved messaged did not give the correct weather data back'

def test_RecvMessages_colour():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    run()
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    colour = '#FFFFFF'
    testRecv.put_nowait(message('data', 'c' + colour))
    time.sleep(1)
    stop()
    returned = testgui.get_nowait()
    assert (returned.messageType == 'colour'), 'Recved message did not give message of type colour back'
    assert (returned.info == colour), 'Recved messaged did not give the correct colour data back'

def test_RecvMessages_direction():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    run()
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    direct = '1'
    testRecv.put_nowait(message('data', 'd' + direct))
    time.sleep(1)
    stop()
    returned = testgui.get_nowait()
    assert (returned.messageType == 'direction'), 'Recved message did not give message of type direction back'
    assert (returned.info == direct), 'Recved messaged did not give the correct direction data back'    
