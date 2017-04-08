#==============================================================================
#title           		:mirrorController_test.py
#description    	:tests features of the mirror
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
    weatherData = '{"coord":{"lon":-75.7,"lat":45.41},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02d"}],"base":"stations","main":{"temp":2.51,"pressure":1030,"humidity":51,"temp_min":2,"temp_max":3},"visibility":24140,"wind":{"speed":1.5,"deg":230},"clouds":{"all":20},"dt":1490482800,"sys":{"type":1,"id":3694,"message":0.0039,"country":"CA","sunrise":1490439242,"sunset":1490484206},"id":6094817,"name":"Ottawa","cod":200}'
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
    busData = '{"GetNextTripsForStopResult":{"StopNo":"3031","StopLabel":"SMYTH","Error":"","Route":{"RouteDirection":[{"RouteNo":104,"RouteLabel":"Place d\'Orl\u00e9ans","Direction":"Eastbound","Error":"","RequestProcessingTime":"20170326194147","Trips":{"Trip":[{"TripDestination":"Place d\'Orl\u00e9ans","TripStartTime":"19:38","AdjustedScheduleTime":"9","AdjustmentAge":"0.86","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"45.384907","Longitude":"-75.694578","GPSSpeed":"20.3"},{"TripDestination":"Place d\'Orl\u00e9ans","TripStartTime":"20:08","AdjustedScheduleTime":"37","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""},{"TripDestination":"Place d\'Orl\u00e9ans","TripStartTime":"20:38","AdjustedScheduleTime":"67","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""}]}},{"RouteNo":104,"RouteLabel":"Carleton","Direction":"Westbound","Error":"","RequestProcessingTime":"20170326194147","Trips":{"Trip":[{"TripDestination":"Carleton","TripStartTime":"19:31","AdjustedScheduleTime":"16","AdjustmentAge":"0.71","LastTripOfSchedule":false,"BusType":"6E - 60","Latitude":"45.451344","Longitude":"-75.584719","GPSSpeed":"13.7"},{"TripDestination":"Carleton","TripStartTime":"20:01","AdjustedScheduleTime":"44","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""},{"TripDestination":"Carleton","TripStartTime":"20:31","AdjustedScheduleTime":"74","AdjustmentAge":"-1","LastTripOfSchedule":false,"BusType":"6EB - 60","Latitude":"","Longitude":"","GPSSpeed":""}]}}]}}}'
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
 
def test_RecvMessages_beat():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    id = '10'
    setId(id)
    run()
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    testRecv.put_nowait(message('beat', ''))
    time.sleep(1)
    stop()
    returned = testSend.get_nowait()
    assert (returned.messageType == 'beat'), 'Recved message did not give message of type beat back'
    assert (returned.info == id), 'Recved messaged did not give the correct id (10) data back'  

def test_RecvMessages_id():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    setId('-1')
    id = '99'
    run()
    testRecv.put_nowait(message('id', id))
    testRecv.put_nowait(message('beat', ''))
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    time.sleep(1)
    stop()
    returned = testSend.get_nowait()
    assert (returned.messageType == 'beat'), 'Recved message did not give message of type beat when setting id '
    assert (returned.info == id), 'Recved messaged did not give the correct id (99) data back'      
    
def test_RecvMessages_serverConnect():
    testRecv = Queue()
    testSend = Queue()
    testgui = Queue()
    setId('-1')
    run()
    _thread.start_new_thread(watchRecvMessages, (testRecv,testSend,testgui,))
    time.sleep(1)
    stop()
    returned = testSend.get_nowait()
    assert (returned.messageType == 'beat'), 'Recved message did not give message of type beat when autoConnect to server '
    assert (returned.info == '-1'), 'Recved messaged did not give the correct id (-1) data back'  