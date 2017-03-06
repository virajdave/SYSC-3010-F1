from socket import *
import sys

s = socket(AF_INET, SOCK_DGRAM)

s.bind(('', 8080))

while True:

    print ("Waiting to receive.")

    buf, address = s.recvfrom(2048)
    print ("Connected to %s" % address[0])
    s.sendto(str('hi').encode('utf-8'), address)

s.shutdown(1)
