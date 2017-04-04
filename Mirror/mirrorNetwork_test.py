#==============================================================================
#title           		:mirrorNetwork_test.py
#description    	:tests features of the mirror network
#author          		:Dillon Verhaeghe
#date            		:20170403
#version         		:0.0
#python_version :3 
#==============================================================================
from mirrorNetwork import *
import socket, time
from dataPassingObject import *
import _thread, os
from queue import *

def test_recv_id():
    testQueue = Queue()
    data = '01/10'
    networkRun()
    recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    recvSock.bind(('127.0.0.1', 0))
    recvPort = recvSock.getsockname()[1]
    self_address = ('127.0.0.1', recvPort)
    sendSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    _thread.start_new_thread(mirrorNetRecv, (recvPort, recvSock,testQueue,))
    time.sleep(1)
    sendSock.sendto(data.encode('utf-8'), self_address)
    time.sleep(1)
    networkStop()
    time.sleep(2)
    recvSock.close()
    sendSock.close()
    returned = testQueue.get_nowait()
    assert (returned.messageType == 'id'), 'Recv network did not set message type to id'
    assert (returned.info == '10'), 'Recv network did not return id correctly'
    
def test_recv_beat():
    testQueue = Queue()
    data = '00'
    networkRun()
    recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    recvSock.bind(('127.0.0.1', 0))
    recvPort = recvSock.getsockname()[1]
    self_address = ('127.0.0.1', recvPort)
    sendSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    _thread.start_new_thread(mirrorNetRecv, (recvPort, recvSock,testQueue,))
    time.sleep(1)
    sendSock.sendto(data.encode('utf-8'), self_address)
    time.sleep(1)
    networkStop()
    time.sleep(2)
    recvSock.close()
    sendSock.close()
    returned = testQueue.get_nowait()
    assert (returned.messageType == 'beat'), 'Recv network did not set message type to beat'
    assert (returned.info == ''), 'Recv network did not have nothing in info when recv beat'
    
def test_recv_data():
    testQueue = Queue()
    data = '02/somedataHere'
    networkRun()
    recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    recvSock.bind(('127.0.0.1', 0))
    recvPort = recvSock.getsockname()[1]
    self_address = ('127.0.0.1', recvPort)
    sendSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    _thread.start_new_thread(mirrorNetRecv, (recvPort, recvSock,testQueue,))
    time.sleep(1)
    sendSock.sendto(data.encode('utf-8'), self_address)
    time.sleep(1)
    networkStop()
    time.sleep(2)
    recvSock.close()
    sendSock.close()
    returned = testQueue.get_nowait()
    assert (returned.messageType == 'data'), 'Recv network did not set message type to data'
    assert (returned.info == 'somedataHere'), 'Recv network did not have the random data in the info'
    
    
    # This test is removed since the automated git software testing cant read the seperate file to check for the error
    
# def test_recv_error():
    # testQueue = Queue()
    # data = 'blablabla'
    # networkRun()
    # recvSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    # recvSock.bind(('127.0.0.1', 0))
    # recvPort = recvSock.getsockname()[1]
    # self_address = ('127.0.0.1', recvPort)
    # sendSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    # _thread.start_new_thread(mirrorNetRecv, (recvPort, recvSock,testQueue,))
    # time.sleep(1)
    # sendSock.sendto(data.encode('utf-8'), self_address)
    # time.sleep(1)
    # networkStop()
    # time.sleep(2)
    # recvSock.close()
    # sendSock.close()
    # data = 'Error Message: ' + data + '\n'
    # log = open('logs/RecvError.log', 'r')
    # errorMessage = log.readlines()[-1]
    # assert (errorMessage == data), 'Recv network did not log the error correctly'
    

