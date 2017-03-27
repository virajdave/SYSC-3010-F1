from weather import *

def test_url():
    assert url_builder('Ottawa,Ca')[:88] == "http://api.openweathermap.org/data/2.5/weather?q=Ottawa,Ca&mode=json&units=metric&APPID="
