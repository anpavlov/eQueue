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
from map import class_resolver, categories

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
            'qid': q['id'],
            'name': name,
            'description': description,
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

    if in_front != -1:
        stand_timestamp = tarantool_manager.get_stand_timestamp(q['id'], user['id'])
    else:
        stand_timestamp = 0

    if in_front == -1:
        total_in_queue = tarantool_manager.get_total_count_in_queue(q['id'])
    else:
        total_in_queue = 0

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
            'address': class_resolver.get_address_by_coords(q['coords']),
            'wait_time': predict.predict(in_front, stand_timestamp, total_in_queue),
            'in_front': in_front,
            'number': in_front + len(passed) + 1
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
            'address': class_resolver.get_address_by_coords(q['coords']),
            'wait_time': 26,  # TODO: remove magic))
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
    payload = {'qid': qid, 'in-front': 5, 'users_quantity': 21, 'wait_time': 31}
    # notify.delay(payload)
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
    data = {'notification': 'true', 'title': q[0]['name'], 'qid': qid}
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
        'time_out': int(time.time()),
        'class': q[0]['class']
    }
    tarantool_manager.insert('stats', stat_data)

    response = {
        'code': 200,
        'body': {
            'user': user[0][1]
        }
    }
    payload = {'qid': qid, 'in-front': 5, 'users_quantity': 21, 'wait_time': 31}
    # notify.delay(payload)
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

    q = []
    if queues[0]:
        for queue in queues:
            stands = standings.select(queue['id'], index='qid')
            users = [u[1] for u in stands]

            q.append(
                {
                    'qid': queue['id'],
                    'name': queue['name'],
                    'description': queue['description'],
                    'coords': str(queue['coords'][0]) + ',' + str(queue['coords'][1]),
                    'users_quantity': len(users),
                    'address': class_resolver.get_address_by_coords(queue['coords'])
                }
            )
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
    # q = [queue[0] for queue in queues]

    info = []
    for queue_s in queues:
        q_id = queue_s[0]
        try:
            queue = tarantool_manager.select_assoc('queues', (q_id))
        except NoResult:
            return json.dumps(responses.ACCESS_DENIED)
        stands = standings.select(queue[0]['id'], index='qid')
        users = [u[1] for u in stands]
        in_front = tarantool_manager.get_user_position(q_id, user['id'])
        if in_front > 0:
            in_front -= 1

        try:
            passed = tarantool_manager.select_assoc('stats', (q_id), index='qid')
        except NoResult:
            passed = []

        info.append({
            'qid': queue[0]['id'],
            'name': queue[0]['name'],
            'description': queue[0]['description'],
            'users_quantity': len(users),
            'address': class_resolver.get_address_by_coords(queue[0]['coords']),
            'wait_time': predict.predict(in_front, queue_s[4]),
            'in_front': in_front,
            'number': in_front + len(passed) + 1
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
        payload = {'qid': qid, 'in-front': 5, 'users_quantity': 21, 'wait_time': 31}
        # notify.delay(payload)
        return json.dumps(response)
    else:
        #  means user was not in the queue
        response = {
            'code': 400,
            'body': {
                'error': 'not in this queue'
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
    try:
        q = tarantool_manager.select_assoc('queues', (qid, user['id']), index='qid_user')
    except NoResult:
        return json.dumps(responses.QUEUE_NOT_FOUND)

    # clear standings for this queue
    # TODO: stats
    # TODO: socket to users
    stands = standings.select(qid, index='qid')
    for s in stands:
        standings.delete((s[0], s[1]))

    tarantool_manager.delete('queues', (q[0]['id']))

    response = {
        'code': 200,
        'body': {
            'ok': 'ok'
        }
    }
    return json.dumps(response)


@queue_api.route("/tags/", methods=['GET'])
def tags():
    response = {
        'code': 200,
        'body': {
            'tags': categories.categories
        }
    }
    return json.dumps(response)


@queue_api.route("/pretty/", methods=['GET'])
def pretty():
    try:
        res = tarantool_manager.select_assoc('queues', ())
    except NoResult:
        res = {}
    return json.dumps(res)
