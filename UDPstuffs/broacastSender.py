from socket import *
import sys, time, datetime, _thread

def getIP():
    s = socket(AF_INET, SOCK_DGRAM)
    s.connect(('8.8.8.8', 0))
    name = s.getsockname()[0]
    s.close()
    return name

def getSubnet():
    return getIP().rsplit('.',1)[0] + '.255'

def broadcast(s, ip):
    while True:
        s.setsockopt(SOL_SOCKET, SO_BROADCAST, 1)
        s.sendto(str('hi').encode('utf-8'), (ip, 8020))
        s.setsockopt(SOL_SOCKET, SO_BROADCAST, 0)
        time.sleep(10)

ip = getIP()
subnet = getSubnet()

s = socket(AF_INET, SOCK_DGRAM)
s.bind(('', 0))
port = s.getsockname()[1]
print("Using port " + str(port))


print("BROADCASTING")
_thread.start_new_thread(broadcast, (s, subnet))

'''
time.sleep(3)

print("PING ALL")
ipa = subnet[:-3]
for x in range(0, 255):
    ip = ipa + str(x)
    s.sendto(str('w').encode('utf-8'), (ip, 8080))


time.sleep(3)
'''
print ("Waiting to hear back")
start = datetime.datetime.now()

while True:

    buf, address = s.recvfrom(2048)
    end = datetime.datetime.now()
    if address[0] == ip:
        print('.', end='')
    else:
        print("\nReceived msg from %s in %ds" % (address[0], (end - start).seconds))

s.shutdown(1)



