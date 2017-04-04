#==============================================================================
#title           	:mirrorNetwork.py
#description     	:Preforms the networking for the mirror
#author          	:Dillon Verhaeghe
#date            	:20170326
#version         	:0.4
#usage           	:python3 mirrorRunner.py 10.0.0.1 3010
#notes           	:Should only be invoked from the mirrorController script
#python_version:3 
#==============================================================================


import socket, sys, time
from dataPassingObject import *
import _thread


def networkInit(server, port, recvQueue, sendQueue):
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        localAddress = ('', 0)
        _thread.start_new_thread(mirrorNetRecv, (port, s,recvQueue,))
        _thread.start_new_thread(mirrorNetSend, (server,port,s,sendQueue,))
    

def mirrorNetRecv(port, s, queue):
        portInt = int(port)
        while True:
                buf, address = s.recvfrom(portInt)
                stringdata = buf.decode('utf-8')
                print(stringdata)
                if(stringdata[:2] == '01'):
                        queue.put_nowait(message('id', stringdata[3:]))
                elif(stringdata[:2] == '00'):
                        queue.put_nowait(message('beat', ''))
                elif(stringdata[:2] == '02'):
                        queue.put_nowait(message('data', stringdata[3:]))
                else:
                        recvError = open('logs/RecvError.log', 'a')
                        recvError.write(stringdata)
                        recvError.close()
        s.shutdown(1)
    

def mirrorNetSend(serverIp, servPort, s, queue):
        #serverIp = '10.0.0.1'
        #servPort = 3010
        server_address = (serverIp, int(servPort))
        heartBeat(serverIp,servPort,s,'-1')
        while True:
                if not queue.empty():
                        while not queue.empty():
                                messageToSend = queue.get()
                                if(messageToSend.messageType == 'beat'):
                                        print(messageToSend.info)
                                        heartBeat(serverIp,servPort,s,messageToSend.info)
                                elif(messageToSend.messageType == 'data'):
                                        data = '22/' + messageToSend.info
                                        print('Sending: ' + data)
                                        s.sendto(data.encode('utf-8'), server_address)
                                elif(messageToSend.messageType == 'ack'):
                                        sendAck(serverIp,servPort,s,messageToSend.info)
                                else:
                                        sendError = open('logs/sendError.log', 'a')
                                        sendError.write(messageToSend.messageType + ':' + messageToSend.info + '\n')
                                        sendError.close()
        s.shutdown(1)


def heartBeat(host,port,s,id):
        #host = '10.0.0.1'
        #port = 3010
        server_address = (host,int(port))
        data = '20/' + id + '/2'
        print('Sending: ' + data)
        s.sendto(data.encode('utf-8'), server_address)

def sendAck(host, port, s,id):
        #host = '10.0.0.1'
        #port = 3010
        data = '21/' + id + '/2'
        server_address = (host,int(port))
        print('Sending: ' + data)
        s.sendto(data.encode('utf-8'), server_address)

