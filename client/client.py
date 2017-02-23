import _thread, socket, json

PORT = 25050
OPSET = 48 # NEEDED TO CONVERT FROM STRING CHAR WHILE TESTING

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.bind(('', PORT))

def loop():
    '''
    Do the loop!
    '''

    server_addr = None

    while True:

        # Wait from received packet.
        buf, address = s.recvfrom(2048)
        out = buf[1:].decode('utf-8')

        if not len(out):
            break

        bwho = buf[0] - OPSET
        btype = buf[1] - OPSET
        print("Connected to %s" % address[0])
        print("Got ", btype)
        #s.sendto(str('hi').encode('utf-8'), address)

        if bwho == 0:
            # Server
            server_addr = address

            response = b'10'
            #response.append("got ya".encode())
            s.sendto(response, server_addr)

        elif bwho == 1:
            # Device
            if btype == 0:
                # Send back OK
                response = b'10'
                s.sendto(response, address)
            elif btype == 1:
                # Send back server address
                response = b'11'
                response += json.dumps(server_addr).encode()
                s.sendto(response, address)
        #else:
            # What to do?

_thread.start_new_thread(loop, ())
while True:
    continue
s.shutdown(1)
