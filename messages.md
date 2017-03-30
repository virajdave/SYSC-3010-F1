# Devices -> Server
## BEAT
Sent when the device is turned on and also in response to a server BEAT.
```
20 / Device ID / Device Type
```
## ACK
Used to acknowledge a message from the server.
```
21 / Device ID / (Potential data)
```
## DATA
Needed to send any data to the device driver on the server.
```
22 / Device ID / Data
```
When received by the server, an ACK is sent back to the device.
# Server -> Devices
## BEAT
Sent every x minutes to make sure devices are connected.
```
00
```
## ACK
Used to respond/acknowledge a message from a device.
```
01 / (Potential data)
```
While adding a new device, the data is the device's new ID.
## DATA
Needed to send any data from the device driver on the server.
```
02 / Data
```
# App <-> Server
## NETINF
Asks for the high level device network information.
```
10
```
Response:
```
00 / Device ID : Device Type : Dead / ...each device
```
## ACK
Developing...
## DEVINF
Asks for the information on a specific device.
```
13 / Device ID
```
Response:
```
03 / Device info
```
## DATA
```
12 / Device ID / Data
```
When received by the server, an ACK is sent back to the app.
