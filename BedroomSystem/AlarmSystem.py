import sys
import string
from time import sleep
import datetime;
import signal, os
import RPi.GPIO as GPIO



class AlarmSystem:
    # Tell GPIO library to use GPIO references
    GPIO.setmode(GPIO.BCM)  

    # Configure GPIO8 as an outout
    GPIO.setup(8, GPIO.OUT)

    # Turn Buzzer off
    GPIO.output(8, False)
    # List of LED GPIO numbers
    LedSeq = [4,17,22,10,9,11]
    #lights on to be used as status boolean
    lightsOn = False
    alarmOn = False
    # Set up the GPIO pins as outputs and set False (off)
    print "Setup LED pins as outputs"
    for x in range(6):
        GPIO.setup(LedSeq[x], GPIO.OUT)
        GPIO.output(LedSeq[x], False)
    # Setup for the button
    GPIO.setup(7, GPIO.IN)
    #pass hour/minute alarm should trigger
    def manageAlarm (self):

        print ("Alarm Sounding")
        #turns on all lights then trigger buzzer
        #only occurs if button in pressed (person on bed)
        if (GPIO.input(7) == 1):
            self.lights (True)
            while (GPIO.input(7) == 1):
                GPIO.output(8, True)
                print("buzz")
            GPIO.output(8, False)
        self.alarmOn = False
    def lights (self, on):
        if (on != self.lightsOn):
            for x in range(6):
                GPIO.output(self.LedSeq[x], on)
            self.lightsOn = on

        else:
            print ("Lights already set correctly")
#test code           
#a = AlarmSystem()
#now = datetime.datetime.now()
#a.manageAlarm (now.hour, now.minute+1)
#sleep(5)
#print("leaving Sleep")
#a.lights(False)
def linux_set_time(milliSeconds):
    formatedTime = datetime.datetime.fromtimestamp(int(milliSeconds)/1000)
    time_tuple = (formatedTime.year,
               formatedTime.month,
               formatedTime.day,
               formatedTime.hour,
               formatedTime.minute,
               formatedTime.second,
               formatedTime.microsecond)
    systemCall = 'sudo date -s "'
    systemCall += str(formatedTime.day)
    systemCall += ' ' + formatedTime.strftime('%B')
    systemCall += ' ' + str(formatedTime.year)
    systemCall += ' ' + str(formatedTime.hour) + ':'
    systemCall += str(formatedTime.minute) + ':'
    systemCall += str(formatedTime.second) + '"'
    os.system(systemCall)
