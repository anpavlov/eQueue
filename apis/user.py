# coding=utf-8
from flask import request, Blueprint
from sqlalchemy.orm.exc import NoResultFound
from models import db, User, Session
from datetime import datetime, timedelta
import json
import responses

user_api = Blueprint('user', __name__)
SESSION_TIME = 24*60*60

@user_api.route("/create/", methods=['POST'])
def create():
    # user
    try:
        user_id = User.query.order_by(User.id.desc()).first().id + 1
    except AttributeError:
        user_id = 1
    user = User('anonym'+str(user_id))
    db.session.add(user)
    # session
    session = Session(user)
    db.session.add(session)
    db.session.commit()

    response = {
        'code': 200,
        'body': {
            'token': session.token,
            'uid': user_id
        }
    }
    return json.dumps(response)


@user_api.route("/update/", methods=['POST'])
def update():
    token = None    

    try:
        token = request.form['token']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)

    session = Session.query.filter_by(token=token).first()
    if not session:
        return json.dumps(responses.INVALID_TOKEN)
    
    user = session.user
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

    response = {
        'code': 200,
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
        return json.dumps(responses.BAD_REQUEST)

    session = Session.query.filter_by(token=token).first()
    if not session or int((datetime.utcnow()-session.act_date).total_seconds()) > SESSION_TIME:
        return json.dumps(responses.INVALID_TOKEN)
    
    user = session.user

    response = {
        'code': 200,
        'body': {
            'username': user.username,
            'email': user.email,
        }
    }
    return json.dumps(response)


@user_api.route("/vkauth/", methods=['POST'])
def vkauth():
    try:
        vkuid = int(request.form['vkuid'])
    except (KeyError, TypeError, ValueError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = User.query.filter_by(vkuid=vkuid).one()
    except NoResultFound:
        try:
            user_id = User.query.order_by(User.id.desc()).first().id + 1
        except AttributeError:
            user_id = 1
        user = User('anonym'+str(user_id))
        user.vkuid = vkuid
        db.session.add(user)
    # session
    session = Session(user)
    db.session.add(session)
    db.session.commit()

    response = {
        'code': 200,
        'body': {
            'token': session.token,
            'uid': user.id
        }
    }
    return json.dumps(response)