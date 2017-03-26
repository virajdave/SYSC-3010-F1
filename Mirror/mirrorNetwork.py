import socket, sys, time
from dataPassingObject import *

#For testing
from weather import *


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
	
	
def sendFakeWeather ():
	host = '127.0.0.1'

	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	port = 8080
	server_address = (host, port)

	while 1:
		#weather = open('weather.txt', 'r')
		data = data_fetch(url_builder('Ottawa,Ca'))
		#data = weather.read()
		s.sendto(data.encode('utf-8'), server_address)
		time.sleep(900)

	s.shutdown(1)
