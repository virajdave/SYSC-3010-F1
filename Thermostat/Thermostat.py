
import os
import glob
import time
import RPi.GPIO as GPIO

#setup getting program the file the senor writes into
os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')
base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'
#heater setup
GPIO.setmode(GPIO.BCM)
GPIO.setup(17, GPIO.OUT)
GPIO.output(17, False)

#driver for temperature sensor (it writes data to a file automatically)
class temperatureSensor:
    #default value
    temp = -100;
    
    def getTemp(self):
        lines = self.read_temp_raw()
        #'YES' is refering to if the current data in the file is valid
        while lines[0].strip()[-3:] != 'YES':
            time.sleep(0.2)
            lines=read_temp_raw()
            #in file the current temp in C is after t=
        equals_pos = lines[1].find("t=")
        if equals_pos != -1:
           temp_string = lines[1][equals_pos +2:]
           #in file temp is a string of 5 numbers (no decimal point)
           temp_c = float(temp_string)/1000.0
           #update current temp in temperature sensor before returning value
           #(keeps all data synced)
           self.temp = temp_c
           return temp_c
    #gets most recent data from file
    def read_temp_raw(self):
        f = open(device_file,'r')
        lines = f.readlines()
        f.close()
        return lines
    
class heater:
    on = False
    def onOff (self):
        if (self.on == True):
            self.on = False
            print ("Heater is now off")
            GPIO.output(17, False)
        elif(self.on == False):
            self.on = True
            GPIO.output(17, True)
            print ("Heater is now on")
            

            
class thermostatControl:
    ts = temperatureSensor()
    h = heater()
    temp = 25
    currTemp = ts.getTemp()
    #regulates thermostat by checking current temp vs target
    #adjusts heater accordingly 
    def manageTemp(self):
        self.currTemp = ts.getTemp()
        print ("currTemp = %f" % self.currTemp) 
        print ("wantedTemp = %d" % self.temp)
        n = 0
        if (self.currTemp < self.temp and self.h.on == False) :
            print ("\nTemp low activating heater\n")
            self.h.onOff()         
        elif (self.currTemp-0.5 >= self.temp and self.h.on) :
            print ("\nTemp high deactivating heater\n")
            self.h.onOff()
            
    def setNewTemp(self, t):
        
        self.temp = float (t)
        
        

ts = temperatureSensor()


