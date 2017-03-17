import sys
import string
from time import sleep
import datetime;

class AlarmSystem:
    def manageAlarm (self, h, m):
        now = datetime.datetime.now()
        seconds = (h - now.hour) *360 + (m - now.minute)*60
        print ("Sleeping for " + str(seconds) )
        sleep(seconds)
a = AlarmSystem()
now = datetime.datetime.now()
a.manageAlarm (now.hour, now.minute+1)