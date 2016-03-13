# coding=utf-8
from flask import request, Blueprint
from models import db, User, Queue
from sqlalchemy.orm.exc import NoResultFound
from datetime import datetime
import json
import tarantool
import settings
import responses
import time
from gcm import *

queue_api = Blueprint('queue', __name__)

# tarantool connection
tarantool_conn = tarantool.connect(settings.TARANTOOL_HOST, settings.TARANTOOL_PORT)
standings = tarantool_conn.space('standings')


@queue_api.route("/create/", methods=['POST'])
def create():
    try:
        token = request.form['token']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)
    try:
        name = request.form['name']
    except KeyError:
        name = 'Queue'
    try:
        user = User.get_user_by_token(token)
    except NoResultFound:
        return json.dumps(responses.INVALID_TOKEN)
    q = Queue(user, name)
    db.session.add(q)
    db.session.commit()
    response = {
        'code': 200,
        'body': {
            'qid': q.id
        }
    }
    return json.dumps(response)

@queue_api.route("/update/", methods=['POST'])
def update():
    try:
        token = request.form['token']
        qid = request.form['qid']
    except KeyError:
        return json.dumps(responses.BAD_REQUEST)
    name = request.form.get('name')
    description = request.form.get('description')
    try:
        user = User.get_user_by_token(token)
    except NoResultFound:
        return json.dumps(responses.INVALID_TOKEN)
    try:
        q = Queue.query.filter_by(id=qid, user=user).one()
    except NoResultFound:
        return json.dumps(responses.QUEUE_NOT_FOUND)
    if name is not None:
        q.name = name
    if description is not None:
        q.description = description
    db.session.commit()
    response = {
        'code': 200
    }
    return json.dumps(response)


@queue_api.route("/info/", methods=['GET'])
def info():
    try:
        qid = int(request.args.get('qid'))
    except (ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)

    q = Queue.query.get(qid)
    if q is None:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    stands = standings.select(qid)
    users = [u[1] for u in stands]

    if q.created is None:
        q.created = datetime(1970, 1, 1)
    response = {
        'code': 200,
        'body': {
            'qid': q.id,
            'name': q.name,
            'description': q.description,
            'date_opened': int((q.created - datetime(1970, 1, 1)).total_seconds()),
            'users': users
        }
    }
    return json.dumps(response)


@queue_api.route("/join/", methods=['POST'])
def join():
    try:
        token = request.form['token']
        qid = int(request.form['qid'])
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = User.get_user_by_token(token)
    except NoResultFound:
        return json.dumps(responses.INVALID_TOKEN)
    if user is None:
        return json.dumps(responses.INVALID_TOKEN)
    # check if user had been already in queue
    try:
        standings.insert((int(qid), int(user.id), None, None, int(time.time()), None, 0))
    except tarantool.DatabaseError:
        return json.dumps(responses.ALREADY_IN_QUEUE)
    response = {
        'code': 200,
        'body': {
            'ok': 'ok'
        }
    }
    return json.dumps(response)


@queue_api.route("/call/", methods=['POST'])
def call():
    try:
        token = request.form['token']
        qid = int(request.form['qid'])
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = User.get_user_by_token(token)
    except NoResultFound:
        return json.dumps(responses.INVALID_TOKEN)
    if user is None:
        return json.dumps(responses.INVALID_TOKEN)

    try:
        q = Queue.query.filter_by(id=qid, user=user).one()
    except NoResultFound:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    # get info for mysql stats
    user = standings.select(qid, index='secondary', limit=1, iterator=0)
    if not user:
        return json.dumps(responses.EMPTY_QUEUE)
    # push notification
    gcm = GCM(settings.GCM_SERVER_ID)
    data = {'call': 'true', 'param2': 'value2'}
    out_user = User.query.get(user[0][1])
    reg_id = out_user.gcmid
    if reg_id:
        gcm.plaintext_request(registration_id=reg_id, data=data)
    
    standings.delete((qid, user[0][1]))

    response = {
        'code': 200,
        'body': {
            'user': user[0][1]
        }
    }
    return json.dumps(response)

@queue_api.route("/find/", methods=['GET'])
def find():
    query = request.args.get('query')
    if query:
        queues = Queue.query.filter(Queue.name.like("%" + u' '.join(query).encode('utf-8') + "%")).all()
    else:
        queues = Queue.query.all()
        
    q = [{'qid': queue.id, 'name': queue.name, 'description': queue.description} for queue in queues]

    response = {
        'code': 200,
        'body': {
            'queues': q
        }
    }
    return json.dumps(response)


@queue_api.route("/my/", methods=['POST'])
def my():
    try:
        token = request.form['token']
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = User.get_user_by_token(token)
    except NoResultFound:
        return json.dumps(responses.INVALID_TOKEN)
    if user is None:
        return json.dumps(responses.INVALID_TOKEN)

    queues = Queue.query.filter(Queue.user_id == user.id).all()
    q = [{'qid': queue.id, 'name': queue.name, 'description': queue.description} for queue in queues]

    response = {
        'code': 200,
        'body': {
            'queues': q
        }
    }
    return json.dumps(response)
