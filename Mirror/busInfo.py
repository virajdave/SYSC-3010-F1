#==============================================================================
#title          			:busInfo.py
#description    	:Helper functions for dealing with bus info
#author          		:Dillon Verhaeghe
#date           		:20170326
#version         		:0.4
#usage           		:python3 mirrorController.py
#notes           		:Parses and can get the bus information from the OCTranspo APi
#python_version :3 
#==============================================================================

import urllib.request
import json
import re

def urlBuilderBus(stop, route):
     key = "appID=7f6091d8&apiKey=4be816c142bfb4100421b9cbdef4fb9a"
     stop = "&stopNo=" + str(stop)
     route = '&routeNo=' + str(route)
     url = 'https://api.octranspo1.com/v1.2/GetNextTripsForStop?'
     fullURL = url + key + route + stop + '&format=json'
     return fullURL

def getBusInfo (Fullurl):
     url = urllib.request.urlopen(Fullurl, timeout=10, verify=False)
     output = url.read().decode('utf-8')
     #print(output)
     #raw_api_dict = json.loads(output)
     url.close()
     return raw_api_dict

def parseBusInfo(info):
     info = json.loads(info)
     data = dict(
          station = info.get('GetNextTripsForStopResult').get('StopLabel')
     )
     routeList = info.get('GetNextTripsForStopResult').get('Route').get('RouteDirection')
     directionNum = 0
     if type(routeList) is list:
          for route in routeList:
               directionNum += 1
               trips = route['Trips']
               data['direction' + str(directionNum)] = dict(
                    dest = str(route['RouteNo']) + ': ' + route['RouteLabel']
               )
               if type(trips.get('Trip')) is list:
                    for x in range(0,len(trips.get('Trip')) ):
                         data['direction' + str(directionNum)]['busTime' + str(x)] = trips.get('Trip')[x].get('AdjustedScheduleTime') + ' mins'
               elif  len(trips) > 0:
                         data['direction' + str(directionNum)]['busTime0'] = trips.get('Trip').get('AdjustedScheduleTime') + ' mins'
                         print(data['direction' + str(directionNum)]['busTime0'])
     else:
          directionNum += 1
          trips = routeList['Trips']
          data['direction' + str(directionNum)] = dict(
               dest = str(routeList['RouteNo']) + ': ' + routeList['RouteLabel']
          )
          if type(trips.get('Trip')) is list:
               for x in range(0,len(trips.get('Trip')) ):
                    data['direction' + str(directionNum)]['busTime' + str(x)] = trips.get('Trip')[x].get('AdjustedScheduleTime') + ' mins'
          elif  len(trips) > 0:
                    data['direction' + str(directionNum)]['busTime0'] = trips.get('Trip').get('AdjustedScheduleTime') + ' mins'
                    print(data['direction' + str(directionNum)]['busTime0'])
     return data

     
