# coding=utf-8
from flask import request, Blueprint
import json
import tarantool
import settings
import responses
import time
from gcm import *
from taran import tarantool_manager
from taran.helper import NoResult
from gcm.gcm import GCMNotRegisteredException
from prediction import predict
from map import class_resolver

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
        name = 'Без названия'
    try:
        description = request.form['description']
    except KeyError:
        description = ''
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)
    queue = {
        'user_id': user['id'],
        'name': name,
        'description': description,
        'created': time.time(),
        'coords': [0, 0]
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
        qid = abs(int(request.form['qid']))
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)

    try:
        coords = request.form.get('coords')
        coords = coords.split(",")
        coords = [float(coords[0]), float(coords[1])]
    except (KeyError, ValueError, TypeError, AttributeError, IndexError):
        coords = None

    if coords:
        category = class_resolver.get_class_by_coords(coords)

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
    if coords:
        to_update['coords'] = coords
        if category != -1:
            to_update['class'] = category

    if len(to_update) > 0:
        tarantool_manager.simple_update('queues', q['id'], to_update)

    response = {
        'code': 200
    }
    return json.dumps(response)


@queue_api.route("/info/", methods=['GET'])
def info():
    try:
        qid = abs(int(request.args.get('qid')))
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
            'users_quantity': len(users),
        }
    }

    if q['coords'] != [0, 0]:
        response['body']['coords'] = str(q['coords'][0]) + ',' + str(q['coords'][1])

    return json.dumps(response)


@queue_api.route("/info-user/", methods=['POST'])
def info_user():
    try:
        token = request.form['token']
        qid = abs(int(request.form['qid']))
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    try:
        q = tarantool_manager.select_assoc('queues', (qid))
    except NoResult:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    q = q[0]
    stands = standings.select(qid, index='qid')
    users = [u[1] for u in stands]

    in_front = tarantool_manager.get_user_position(q['id'], user['id'])
    if in_front > 0:
        in_front -= 1

    response = {
        'code': 200,
        'body': {
            'qid': q['id'],
            'name': q['name'],
            'description': q['description'],
            'date_opened': int(q['created']),
            'users_quantity': len(users),
            'address': 'lorem ipsum',  # TODO: get address string
            'wait_time': predict.predict(),
            'in_front': in_front
        }
    }

    if q['coords'] != [0, 0]:
        response['body']['coords'] = str(q['coords'][0]) + ',' + str(q['coords'][1])

    return json.dumps(response)


@queue_api.route("/info-admin/", methods=['POST'])
def info_admin():
    try:
        token = request.form['token']
        qid = abs(int(request.form['qid']))
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    try:
        q = tarantool_manager.select_assoc('queues', (qid))
    except NoResult:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    q = q[0]

    if q['user_id'] != user['id']:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    stands = standings.select(qid, index='qid')
    users = [u[1] for u in stands]

    try:
        passed = tarantool_manager.select_assoc('stats', (qid), index='qid')
    except NoResult:
        passed = []

    response = {
        'code': 200,
        'body': {
            'qid': q['id'],
            'name': q['name'],
            'description': q['description'],
            'date_opened': int(q['created']),
            'users_quantity': len(users),
            'address': 'lorem ipsum',  # TODO: get address string
            'wait_time': predict.predict(),
            'passed': len(passed)
        }
    }

    if q['coords'] != [0, 0]:
        response['body']['coords'] = str(q['coords'][0]) + ',' + str(q['coords'][1])

    return json.dumps(response)

@queue_api.route("/join/", methods=['POST'])
def join():
    try:
        token = request.form['token']
        qid = abs(int(request.form['qid']))
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
        qid = abs(int(request.form['qid']))
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
    data = {'notification': 'true', 'title': q[0]['name']}
    try:
        out_user = tarantool_manager.select_assoc('users', (user[0][1]))
    except NoResult:
        return json.dumps(responses.UNDEFINED_USER)
    out_user = out_user[0]
    reg_id = out_user['gcmid']
    if reg_id != '' and reg_id is not None:
        try:
            gcm.plaintext_request(registration_id=reg_id, data=data)
        except GCMNotRegisteredException:
            pass
    
    standings.delete((qid, user[0][1]))

    stat_data = {
        'qid': qid,
        'uid': out_user['id'],
        'wait_time': int(time.time()) - int(user[0][4]),
        'time_in': int(user[0][4]),
        'time_out': int(time.time())
    }
    tarantool_manager.insert('stats', stat_data)

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


@queue_api.route("/find_near/", methods=['GET'])
def find_near():
    try:
        coords = request.args.get('coords')
        coords = coords.split(",")
        coords = [float(coords[0]), float(coords[1])]
    except (KeyError, ValueError, TypeError, AttributeError, IndexError):
        coords = None

    if coords:
        border = 0.02
        coords = [coords[0] - border, coords[1] - border, coords[0] + border, coords[1] + border]
        queues = tarantool_manager.select_by_coords('queues', 'coords', coords)
    else:
        response = {
            'code': 200,
            'body': {
                'queues': []
            }
        }
        return json.dumps(response)

    if queues[0]:
        q = [{'qid': queue['id'], 'name': queue['name'], 'description': queue['description'], 'coords': queue['coords']} for queue in queues]
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

    try:
        queues = tarantool_manager.select_assoc('queues', (user['id']), index='userid')
    except NoResult:
        queues = []
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
        stands = standings.select(queue[0]['id'], index='qid')
        users = [u[1] for u in stands]
        info.append({
            'qid': queue[0]['id'],
            'name': queue[0]['name'],
            'description': queue[0]['description'],
            'users_quantity': len(users)
        })


    response = {
        'code': 200,
        'body': {
            'queues': info
        }
    }
    return json.dumps(response)


@queue_api.route("/is-yours/", methods=['POST'])
def is_yours():
    try:
        token = request.form['token']
        qid = abs(int(request.form['qid']))
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    res = standings.select((qid, user['id']), index='qid_u')
    if not res:
        response = {
            'code': 200,
            'body': {
                'status': 0
            }
        }
        return json.dumps(response)
    response = {
        'code': 200,
        'body': {
            'status': 1
        }
    }
    return json.dumps(response)


@queue_api.route("/leave/", methods=['POST'])
def leave():
    try:
        token = request.form['token']
        qid = abs(int(request.form['qid']))
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)

    res = standings.delete((qid, user['id']))

    #  TODO: broadcast

    if res:
        response = {
            'code': 200,
            'body': {
                'status': 1
            }
        }
        return json.dumps(response)
    else:
        #  means user was not in the queue
        response = {
            'code': 200,
            'body': {
                'status': 0
            }
        }
        return json.dumps(response)


@queue_api.route("/exist/", methods=['GET'])
def exist():
    try:
        qid = abs(int(request.args.get('qid')))
    except (ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        tarantool_manager.select_assoc('queues', (qid))
    except NoResult:
        response = {
            'code': 200,
            'body': {
                'status': 0
            }
        }
        return json.dumps(response)
    response = {
        'code': 200,
        'body': {
            'status': 1
        }
    }
    return json.dumps(response)


@queue_api.route("/delete/", methods=['POST'])
def delete():
    try:
        token = request.form['token']
        qid = abs(int(request.form['qid']))
    except (KeyError, ValueError, TypeError):
        return json.dumps(responses.BAD_REQUEST)
    try:
        user = tarantool_manager.get_user_by_token(token)
    except NoResult:
        return json.dumps(responses.INVALID_TOKEN)



@queue_api.route("/pretty/", methods=['GET'])
def pretty():
    res = tarantool_manager.select_assoc('queues', ())
    return json.dumps(res)
