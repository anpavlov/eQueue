# coding=utf-8
from flask import request, Blueprint
from models import db, User, Session
from datetime import datetime, timedelta
import json

user_api = Blueprint('user', __name__)
SESSION_TIME = 24*60*60

@user_api.route("/create/", methods=['POST'])
def create():
    # user
    user_id = User.query.order_by(User.id.desc()).first().id + 1;
    user = User('anonym'+str(user_id))
    db.session.add(user)
    # session
    session = Session(user)
    db.session.add(session)
    db.session.commit()

    response = {'code': 200, 'body': {'token': session.token} }
    return json.dumps(response)


@user_api.route("/update/", methods=['POST'])
def update():
    token = None    

    try:
        token = request.form['token']
    except KeyError:
        response = { 'code': 400, 'body': {'error': 'invalid request params'} }
        return json.dumps(response)
      
    session = Session.query.filter_by(token=token).first()
    if not session:
        response = { 'code': 403, 'body': {'error': 'bad token'} }
        return json.dumps(response)
    
    user = session.user;
    # username
    try:
        username = request.form['username']
        user.username = username
    except KeyError:
        pass
    # email
    try:
        username = request.form['email']
        user.email = username
    except KeyError:
        pass

    db.session.add(user)
    session.act_date = datetime.utcnow()
    db.session.add(session)
    db.session.commit()

    response = { 'code': 200,
       'body': {
           'username': user.username,
           'email': user.email,
       }
    }
    return json.dumps(response)


@user_api.route("/details/", methods=['GET'])
def details():

    token = request.args.get('token', '')
    if not token:
        response = { 'code': 400, 'body': {'error': 'invalid request params'} }
        return json.dumps(response)
      
    session = Session.query.filter_by(token=token).first()
    if not session or int((datetime.utcnow()-session.act_date).total_seconds()) > SESSION_TIME:
        response = { 'code': 403, 'body': {'error': 'bad token'} }
        return json.dumps(response)
    
    user = session.user;

    response = { 'code': 200,
       'body': {
           'username': user.username,
           'email': user.email,
       }
    }
    return json.dumps(response)
