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
     url = urllib.request.urlopen(Fullurl, timeout=10)
     output = url.read().decode('utf-8')
     raw_api_dict = json.loads(output)
     url.close()
     return raw_api_dict

def parseBusInfo(info):
	data = dict(
		station = info.get('GetNextTripsForStopResult').get('StopLabel')
    )
	routeList = info.get('GetNextTripsForStopResult').get('Route').get('RouteDirection')
	directionNum = 0
	for route in routeList:
		directionNum += 1
		trips = route['Trips']
		data['direction' + str(directionNum)] = dict(
				dest = str(route['RouteNo']) + ': ' + route['RouteLabel']
		)
		if len(trips) > 0:
			for x in range(0,len(trips)):
				data['direction' + str(directionNum)]['busTime' + str(x)] = trips.get('Trip')[x].get('AdjustedScheduleTime') + ' mins'
		
	return data