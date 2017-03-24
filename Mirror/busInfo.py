import urllib.request
import json

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
     print(output)
     #raw_api_dict = json.loads(output)
     url.close()
     return output

def parseBusInfo(info):
     print(info)
     #print(info.get('GetRouteSummaryForStopResult').get('Routes'))


parseBusInfo(getBusInfo(urlBuilderBus(3031, 104)))
