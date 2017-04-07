#==============================================================================
#title           		:weather.py
#description    		:Collects and parses weather information
#author          		:Dillon Verhaeghe
#date            		:20170326
#version         		:0.4
#usage           		:python3 mirrorRunner.py 10.0.0.1 3010
#notes           		:Pases and can also collect info from the open weather API
#python_version :3 
#==============================================================================

import datetime
import json
import urllib.request


def time_converter(time):
    converted_time = datetime.datetime.fromtimestamp(
        int(time)
    ).strftime('%I:%M %p')
    return converted_time

# takes the JSON from the weather api and parses it out
def data_organizer(raw_api_dict):
    raw_api_dict = json.loads(raw_api_dict)
    data = dict(
        city=raw_api_dict.get('name'),
        country=raw_api_dict.get('sys').get('country'),
        temp=raw_api_dict.get('main').get('temp'),
        temp_max=raw_api_dict.get('main').get('temp_max'),
        temp_min=raw_api_dict.get('main').get('temp_min'),
        humidity=raw_api_dict.get('main').get('humidity'),
        pressure=raw_api_dict.get('main').get('pressure'),
        sky=raw_api_dict['weather'][0]['main'],
        sunrise=time_converter(raw_api_dict.get('sys').get('sunrise')),
        sunset=time_converter(raw_api_dict.get('sys').get('sunset')),
        wind=raw_api_dict.get('wind').get('speed'),
        wind_deg=raw_api_dict.get('deg'),
        dt=time_converter(raw_api_dict.get('dt')),
        cloudiness=raw_api_dict.get('clouds').get('all')
    )
    return data
