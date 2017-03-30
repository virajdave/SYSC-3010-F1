import _thread, socket, sys, time

try:
    ip = sys.argv[1]
except IndexError:
    print("Missing arguments! ex. localhost 8080")
    quit()

try:
    port = int(sys.argv[2])
except IndexError:
    print("Missing port argument! ex. 8080")
    quit()
except ValueError:
    print("Port argument '%s' is not an integer! ex. 8080" % sys.argv[2])
    quit()

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def sender(port):

    while True:
        data = sys.stdin.readline().strip()

        if not len(data):
            break
        elif data[:5] == "port:":
            try:
                port = int(data[5:])
            except ValueError:
                print("Invalid port '%s'" % data[5:])
        else:
            s.sendto(data.encode('utf-8'), (ip, port))
    return

def receiver(port):
    s.bind(('', port))
    print("Listening on port", s.getsockname()[1])

    while True:
        buf, address = s.recvfrom(2048)
        out = buf.decode('utf-8')

        if not len(out):
            break
        elif out == "adding...":
            print(address, "added you!")
        else:
            print("%s : %s" % (address, out))
    return

print("Type and press enter to send a message! An empty line will exit.")
_thread.start_new_thread(receiver, (0,))
sender(port)

s.shutdown(1)
