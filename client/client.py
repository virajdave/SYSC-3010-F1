import _thread, socket, json

PORT = 25050
OPSET = 48 # NEEDED TO CONVERT FROM STRING CHAR WHILE TESTING

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.bind(('', PORT))

def loop():
    '''
    Do the loop!
    '''

    server = None
    devices = set()

    while True:

        # Wait from received packet.
        buf, address = s.recvfrom(2048)
        if not len(buf):
            break

        bwho = buf[0] - OPSET
        btype = buf[1] - OPSET
        print("From", address, "got OP", bwho, btype)

        if bwho == 0:
            # SERVER
            if btype == 0:
                # DISCOVERY: Add server + pass through to devices
                server = address

                code = b'12' # CLIENT + INFO
                response = json.dumps(list(devices)).encode()
                s.sendto(code + response, server)

                for device in devices:
                    s.sendto(code, device)

        elif bwho == 2:
            # DEVICE
            if btype == 0:
                # DISCOVERY: Send back server addr and add to device list
                devices.add(address)
                response = b'12' #CLIENT + INFO
                response += json.dumps(server).encode()
                s.sendto(response, address)
        #else:
            # What to do?

_thread.start_new_thread(loop, ())
while True:
    continue
s.shutdown(1)
