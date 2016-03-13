# coding=utf-8
from flask import request, Blueprint
from sqlalchemy.orm.exc import NoResultFound
from models import db, User, Session
from datetime import datetime, timedelta
import werkzeug
import json
import responses
from taran import tarantool_manager
from taran.helper import NoResult

user_api = Blueprint('user', __name__)
SESSION_TIME = 24*60*60

@user_api.route("/create/", methods=['POST'])
def create():

    try:
        token = request.form['token']
    except KeyError:
        token = None

    try:
        gcmid = request.form['gcmid']
    except KeyError:
        gcmid = None

    try:
        email = request.form['email']
        res = tarantool_manager.select_assoc('users', (email), index='email')
        if res:
            return json.dumps(responses.EMAIL_BUSY)
        password = request.form['password']
        password = werkzeug.security.generate_password_hash(password, method='pbkdf2:sha256:2400', salt_length=8)
    except KeyError:
        email = None

    user = {}

    if token:
        try:
            user = tarantool_manager.get_user_by_token(token)
            if email and not user['email']:
                user['email'] = email
                user['password'] = password
            else:
                return json.dumps(responses.BAD_REQUEST)
        except NoResult:
            return json.dumps(responses.INVALID_TOKEN)
    else:
        if email:
            user['email'] = email
            user['password'] = password

    try:
        username = request.form['username']
        user['username'] = username
    except KeyError:
        pass

    user['gcmid'] = gcmid

    new_user = tarantool_manager.insert('users', user)
    if not token:
        token = tarantool_manager.create_session(new_user)

    response = {
        'code': 200,
        'body': {
            'token': str(token),
            'uid': new_user['id'],
            'email': new_user['email'],
            'username': new_user['username'],
        }
    }
    return json.dumps(response)


@user_api.route("/updategcm/", methods=['POST'])
def update_gcmid():
    try:
        gcmid = request.form['gcmid']
        token = request.form['token']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)
    user = tarantool_manager.get_user_by_token(token)
    tarantool_manager.simple_update('users', user['id'], {'gcmid': gcmid})

    response = {
        'code': 200,
        'body': {
            'ok': 'ok'
        }
    }
    return json.dumps(response)



@user_api.route("/login/", methods=['POST'])
def login():

    try:
        email = request.form['email']
        password = request.form['password']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)

    user = tarantool_manager.select_assoc('users', (email), index='email')
    if not user:
        return json.dumps(responses.ACCESS_DENIED)
    user = user[0]

    if werkzeug.security.check_password_hash(user['password'], password):
        token = tarantool_manager.create_session(user)
    else:
        return json.dumps(responses.ACCESS_DENIED)


    response = {
        'code': 200,
        'body': {
            'token': str(token),
            'username': user['username'],
            'email': user['email'],
            'uid': user['id'],
        }
    }
    return json.dumps(response)


@user_api.route("/logout/", methods=['POST'])
def logout():

    try:
        token = request.form['token']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)
    
    session = Session.query.filter_by(token=token).first()
    if session:
        db.session.delete(session)
        db.session.commit()
    else:
        return json.dumps(responses.INVALID_TOKEN)

    response = {
        'code': 200,
        'body': {}
    }
    return json.dumps(response)


@user_api.route("/update/", methods=['POST'])
def update():

    try:
        token = request.form['token']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)

    try:
        user = User.get_user_by_token(token)
    except NoResultFound:
        return json.dumps(responses.INVALID_TOKEN)

    # username
    try:
        username = request.form['username']
        user.username = username
    except KeyError:
        pass
    # email
    try:
        email = request.form['email']
        if user.email != email and User.query.filter_by(email=email).first():
            return json.dumps(responses.EMAIL_BUSY)
        user.email = email
    except KeyError:
        pass

    session = Session.query.filter_by(token=token).first()
    session.act_date = datetime.utcnow()
    db.session.add(user)
    db.session.add(session)
    db.session.commit()

    response = {
        'code': 200,
        'body': {
            'email': user.email,
            'uid': user.id,
            'username': user.username,
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
            'uid': user.id,
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
        # try:
        #     user_id = User.query.order_by(User.id.desc()).first().id + 1
        # except AttributeError:
        #     user_id = 1
        user = User()
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
