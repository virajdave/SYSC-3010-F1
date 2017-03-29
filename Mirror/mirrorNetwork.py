import socket, sys, time
from dataPassingObject import *
import _thread


def networkInit(recvQueue, sendQueue):
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	localAddress = ('localhost', 0)
	s.bind(localAddress)
	_thread.start_new_thread(mirrorNetRecv, (s,recvQueue,))
	_thread.start_new_thread(mirrorNetSend, (s,recvQueue,))
	

def mirrorNetRecv(s, queue):
	while True:
		buf, address = s.recvfrom(port)
		if not len(buf):
			break
		stringdata = buf.decode('utf-8')
		if(stringdata[:2] == '01'):
			queue.put_nowait(message('id', stringdata[3:]))
		elif(stringdata[:2] == '00'):
			queue.put_nowait(message('beat', ''))
		elif(stringdata[:2] == '02'):
			queue.put_nowait(message('data', stringdata[3:]))
		else:
			recvError = open('/logs/RecvError.log', 'a')
			recvError.write(stringdata)
			recvError.close()
	s.shutdown(1)
	

def mirrorNetSend(s, queue):
	serverIp = '10.0.0.1'
	servPort = 3010
	server_address = (serverIp, servPort)
	heartBeat(s,'-1')
	while True:
		if not queue.empty():
				while not queue.empty():
					messageToSend = queue.get()
					if(messageToSend.type == 'beat'):
						heartBeat(s,messageToSend.info)
					elif(messageToSend.type == 'data'):
						data = '22/' + messageToSend.info
						s.sendto(data.encode('utf-8'), server_address)
					elif(messageToSend.type == 'ack'):
						sendAck(s,messageToSend.info)
					else:
						sendError = open('/logs/sendError.log', 'a')
						sendError.write(stringdata)
						sendError.close()
	s.shutdown(1)


def heartBeat(s,id):
	data = '20/' + id + '/2'
	s.sendto(data.encode('utf-8'), server_address)

def sendAck(s,id):
	data = '21/' + id + '/2'
	s.sendto(data.encode('utf-8'), server_address)

def sendFakeData ():
	host = '127.0.0.1'

	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	port = 8080
	server_address = (host, port)
	timeBetweenSends = 2
	while 1:
		data ='c' + '#FF0000'
		s.sendto(data.encode('utf-8'), server_address)	
		time.sleep(timeBetweenSends)
		
		weather = open('weatherTest.txt', 'r')
		data = weather.read()
		weather.close()
		data ='w' + data
		s.sendto(data.encode('utf-8'), server_address)
		time.sleep(timeBetweenSends)
		
		data ='c' + '#00FF00'
		s.sendto(data.encode('utf-8'), server_address)	
		time.sleep(timeBetweenSends)
		
		bus = open('busTest.txt', 'r')
		data = bus.read()
		bus.close()
		data ='b' + data
		s.sendto(data.encode('utf-8'), server_address)	
		time.sleep(timeBetweenSends)
		
		data ='c' + '#2E99A9'
		s.sendto(data.encode('utf-8'), server_address)	
		time.sleep(timeBetweenSends)

	s.shutdown(1)
