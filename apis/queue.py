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
    user_id = Session.query.filter_by(token=token).first().user_id
    user = User.query.filter_by(id=user_id).first()
    response = {
        'code': 200,
        'body': {
            'user_id': user.id
        }
    }
    return json.dumps(response)