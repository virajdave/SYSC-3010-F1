import socket
#from mirrorController import *
##def mockServer ():
##    totalTests = 0
##    totalpass = 0
##    totalfail = 0
##    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
##    mirrorNetwork = ''
##    while (mirrorNetwork == ''):
##        buf, address = s.recvfrom(9874)
##        if not len(buf):
##            break
##        mirrorNetwork = address
##
##    deviceIdmess = '01/10'
##    s.sendto(deviceIdmess.encode('utf-8'),mirrorNetwork)
##    buf = ''
##    while (buf == ''):
##        buf, address = s.recvfrom(9874)
##
##    buf = buf.decode('utf-8')
##    recvId = buf[3:5]
##    totalTests += 1
##    if (recvId = '10'):
##        print("Passed setting id")
##        totalpass += 1
##    else:
##        print("Failed setting id Got: " + recvId + "Expected: 10") 
##        totalfail += 1


    


