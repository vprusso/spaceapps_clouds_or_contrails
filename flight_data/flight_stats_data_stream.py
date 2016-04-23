"""
flight_stats_data_stream.py:
    Use FlightStats API to obtain all flights near a specified latitude and
    longitude over a specified radius.
"""
import requests
import pprint
import json
import sys

from clouds_or_contrails import api_credentials

def pretty_print_json(dictionary):
    print (json.dumps(dictionary, sort_keys = True, indent = 4, separators = (',', ': ')))

def request_flight_states(url):
    dictionary = requests.get(url).json()
    return dictionary

def get_flight_call_signs(dictionary):
    flight_pos = dictionary['flightPositions']
    call_signs = []
    for i in range(len(flight_pos)):
        call_signs.append(flight_pos[i]['callsign'])
    return call_signs

def get_flight_Ids(dictionary):
    flight_pos = dictionary['flightPositions']
    flight_Ids = []
    for i in range(len(flight_pos)):
        flight_Ids.append(flight_pos[i]['flightId'])
    return flight_Ids

max_flights = "999"
app_id = api_credentials.FLIGHT_STATS_ID
app_key = api_credentials.FLIGHT_STATS_API_KEY

if len(sys.argv) < 4:
    sys.stderr.write("ARG PARSE: Required arguments: LAT, LON, RAD...")
else:
    lat = sys.argv[1]
    lon = sys.argv[2]
    mile_radius = sys.argv[3]

url = 'https://api.flightstats.com/flex/flightstatus/rest/v2/json/flightsNear/'+lat+'/'+lon+'/'+mile_radius+'?appId='+app_id+'&appKey='+app_key+'&'+max_flights
dictionary = request_flight_states(url)
pretty_print_json(dictionary)