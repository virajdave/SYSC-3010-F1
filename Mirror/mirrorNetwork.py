import socket, sys, time
from dataPassingObject import *


def mirrorNetRecv(queue, textport):
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	port = int(textport)
	server_address = ('localhost', port)
	s.bind(server_address)

	while True:
		buf, address = s.recvfrom(port)
		if not len(buf):
			break
		stringdata = buf.decode('utf-8')
		queue.put_nowait(message('recv', stringdata))
		
	s.shutdown(1)
	
	
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
