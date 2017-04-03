#==============================================================================
#title           		:systemTime.py
#description    		:Changes the system time on linux machines
#author          		:Dillon Verhaeghe
#date            		:20170326
#version         		:0.4
#usage           		:python3 mirrorRunner.py 10.0.0.1 3010
#notes           		
#python_version 		:3 
#==============================================================================


import sys
import datetime
import os

#time_tuple = (     2012, # Year
#                   9, # Month
#                   6, # Day
#                   0, # Hour
#                   38, # Minute
#                   0, # Second
#                   0, # Millisecond
#              )

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

