from weather import *

def test_url():
    assert url_builder('Ottawa,Ca')[:88] == "http://api.openweathermap.org/data/2.5/weather?q=Ottawa,Ca&mode=json&units=metric&APPID="

def test_time():
    assert time_converter("1442342523") == "02:42 PM"
    assert time_converter("1489352400") == "05:00 PM"
