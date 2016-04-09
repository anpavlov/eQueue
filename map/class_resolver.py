import requests
import settings
from map import categories


def get_class_by_coords(coords):
    r = requests.get('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={},{}&radius=50&key={}'.
                     format(coords[0], coords[1], settings.GOOGLE_PLACE_ID))
    res = r.json()
    try:
        for result in res['results']:
            for category in result['types']:
                if category in categories.categories:
                    return category
    except KeyError:
        return -1

    return 'undefined'