import sys
import string
from time import sleep
import datetime;
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

    lightsOn = False

    # Set up the GPIO pins as outputs and set False
    print "Setup LED pins as outputs"
    for x in range(6):
        GPIO.setup(LedSeq[x], GPIO.OUT)
        GPIO.output(LedSeq[x], False)
    # Setup for the button
    GPIO.setup(7, GPIO.IN)
    def manageAlarm (self, h, m):
        now = datetime.datetime.now()
        print ("Current time h=", now.hour, "m = ", now.minute)
        print ("Alarm time h=", h, "m = ", m)
        seconds = (h - now.hour) *360 + (m - now.minute)*60
        print ("Sleeping for " + str(seconds) )
        sleep(seconds)
        print ("Alarm Sounding")
        if (GPIO.input(7) == 1):
            self.lights (True)
            while (GPIO.input(7) == 1):
                GPIO.output(8, True)

            GPIO.output(8, False)
            
    def lights (self, on):
        if (on != self.lightsOn):
            for x in range(6):
                GPIO.output(self.LedSeq[x], on)
            self.lightsOn = on

        else:
            print ("Lights already set correctly")
           
a = AlarmSystem()
now = datetime.datetime.now()
a.manageAlarm (now.hour, now.minute+1)
sleep(10)
a.lights(False)
