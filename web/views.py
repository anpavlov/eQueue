from django.shortcuts import render
from django.http import HttpResponse
from django.contrib.sessions.backends.db import SessionStore
import json
from web.api import DB


db = DB()

def home(req):

    resp_data = ''

    # user_signup()
    # resp_data = json.dumps(db.user_signup())

    # queue_create(token)
    # token = 'aaymhgwiled5nm3r6fvwt1672ii3soj9'
    # resp_data = json.dumps(db.queue_create(token))

    # queue_update(token, alter_id)
    # token = 'aaymhgwiled5nm3r6fvwt1672ii3soj9'
    # alter_id = 'vn458'
    # resp_data = json.dumps(db.queue_update(token, alter_id, 'java_rk1', 'queue for rk1 for groups'))


    return HttpResponse(resp_data)
