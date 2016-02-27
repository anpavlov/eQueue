# coding=utf-8
from flask import request, Blueprint
from models import db, User, Queue
from sqlalchemy.orm.exc import NoResultFound
import json
import tarantool
import settings
import responses

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
        name = ''
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