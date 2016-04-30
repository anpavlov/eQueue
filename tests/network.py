import requests

api_url = 'http://localhost:5000/api/'


def wrapper(url, data):
    resp = requests.post(api_url + url, data)
    return resp.json()


def create_user(data):
    return wrapper('user/create/', data)


def update_gcm(data):
    return wrapper('user/updategcm/', data)


def login(data):
    return wrapper('user/login/', data)


def logout(data):
    return wrapper('user/logout/', data)