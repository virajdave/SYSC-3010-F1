import _thread, socket, sys, time

try:
    port = int(sys.argv[1])
except IndexError:
    print("Missing port argument! ex. 8080")
    quit()
except ValueError:
    print("Port argument '%s' is not an integer! ex. 8080" % sys.argv[1])
    quit()

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

def sender(port):
    users = set()

    while True:
        data = sys.stdin.readline().strip()

        if not len(data):
            break
        elif data[:4] == "add:":
            user = data[4:]
            try:
                s.sendto("adding...".encode('utf-8'), (user, port))
                users.add(user)
                print("Added", user)
            except OSError:
                print("Could not send a message to", user)
        elif data[:7] == "remove:":
            user = data[7:]
            try:
                users.remove(user)
                print("Removed", user)
            except KeyError:
                print(user, "is not added")
        elif data[:5] == "list:":
            if len(users):
                for user in users:
                    print("\t", user)
            else:
                print("\tempty!")
        else:
            for user in users:
                s.sendto(data.encode('utf-8'), (user, port))
    return

def receiver(port):
    s.bind(('', port))

    while True:
        buf, address = s.recvfrom(2048)
        out = buf.decode('utf-8')

        if not len(out):
            break
        elif out == "adding...":
            print(address[0], "added you!")
        else:
            print("%s : %s" % (address[0], out))
    return

print("Type and press enter to send a message! An empty line will exit.")
print("Commands -> 'add:{ip}' adds a user, 'remove:{ip}' removes a user, 'list:' lists all users.\n")
_thread.start_new_thread(receiver, (8080,))
sender(port)

s.shutdown(1)
