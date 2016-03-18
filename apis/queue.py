# coding=utf-8
from flask import request, Blueprint
from models import db, User, Queue
import json
import tarantool
import settings
import responses
import time
from gcm import *
from taran import tarantool_manager
from taran.helper import NoResult

queue_api = Blueprint('queue', __name__)


standings = tarantool_manager.get_space('standings')


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
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)
    queue = {
        'user_id': user['id'],
        'name': name,
        'created': time.time()
    }
    q = tarantool_manager.insert('queues', queue)
    response = {
        'code': 200,
        'body': {
            'qid': q['id']
        }
    }
    return json.dumps(response)

@queue_api.route("/update/", methods=['POST'])
def update():
    try:
        token = request.form['token']
        qid = int(request.form['qid'])
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    name = request.form.get('name')
    description = request.form.get('description')
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)
    try:
        q = tarantool_manager.select_assoc('queues', (qid, user['id']), index='qid_user')
    except NoResult:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    q = q[0]
    to_update = {}
    if name is not None:
        to_update['name'] = name
    if description is not None:
        to_update['description'] = description

    if len(to_update) > 0:
        tarantool_manager.simple_update('queues', q['id'], to_update)

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

    try:
        q = tarantool_manager.select_assoc('queues', (qid))
    except NoResult:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    q = q[0]
    stands = standings.select(qid, index='qid')
    users = [u[1] for u in stands]

    response = {
        'code': 200,
        'body': {
            'qid': q['id'],
            'name': q['name'],
            'description': q['description'],
            'date_opened': int(q['created']),
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
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    # check if user had been already in queue
    try:
        standings.insert((int(qid), int(user['id']), None, None, int(time.time()), None, 0))
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
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    try:
        q = tarantool_manager.select_assoc('queues', (qid, user['id']), index='qid_user')
    except NoResult:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    # get info for mysql stats
    user = standings.select(qid, index='secondary', limit=1, iterator=0)
    if not user:
        return json.dumps(responses.EMPTY_QUEUE)
    # push notification
    gcm = GCM(settings.GCM_SERVER_ID)
    data = {'call': 'true', 'param2': 'value2'}
    try:
        out_user = tarantool_manager.select_assoc('users', (user[0][1]))
    except NoResult:
        return json.dumps(responses.UNDEFINED_USER)
    out_user = out_user[0]
    reg_id = out_user['gcmid']
    if reg_id:
        #gcm.plaintext_request(registration_id=reg_id, data=data)
        pass
    
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
        query = request.args.get('query')
        queues = tarantool_manager.select_by_like('queues', 'name', 'name', query)
    else:
        try:
            queues = tarantool_manager.select_assoc('queues', ())
        except NoResult:
            response = {
                'code': 200,
                'body': {
                    'queues': []
                }
            }
            return json.dumps(response)

    if queues[0]:
        q = [{'qid': queue['id'], 'name': queue['name'], 'description': queue['description']} for queue in queues]
    else:
        q = []
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
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    queues = tarantool_manager.select_assoc('queues', (user['id']), index='userid')
    q = [{'qid': queue['id'], 'name': queue['name'], 'description': queue['description']} for queue in queues]

    response = {
        'code': 200,
        'body': {
            'queues': q
        }
    }
    return json.dumps(response)


@queue_api.route("/in-queue/", methods=['POST'])
def in_queue():
    try:
        token = request.form['token']
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    queues = standings.select(user['id'], index='user_id', iterator=0)
    q = [queue[0] for queue in queues]

    info = []
    for q_id in q:
        try:
            queue = tarantool_manager.select_assoc('queues', (q_id))
        except NoResult:
            return json.dumps(responses.ACCESS_DENIED)
        info.append({'qid': queue[0]['id'], 'name': queue[0]['name'], 'description': queue[0]['description']})


    response = {
        'code': 200,
        'body': {
            'queues': info
        }
    }
    return json.dumps(response)