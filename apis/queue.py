# coding=utf-8
from flask import request, Blueprint
from models import db, User, Session, Queue
import json
import tarantool
import settings

queue_api = Blueprint('queue', __name__)

# tarantool connection
tarantool_conn = tarantool.connect(settings.TARANTOOL_HOST, settings.TARANTOOL_PORT)
standings = tarantool_conn.space('standings')


@queue_api.route("/create/", methods=['POST'])
def create():
    try:
        token = request.form['token']
    except KeyError:
        response = {'code': 400, 'body': {'error': 'invalid request params'}}
        return json.dumps(response)
    try:
        name = request.form['name']
    except KeyError:
        name = ''
    user_id = Session.query.filter_by(token=token).one().user_id
    user = User.query.filter_by(id=user_id).one()
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