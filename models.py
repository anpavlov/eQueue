from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
import uuid

db = SQLAlchemy()


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(120), unique=True)
    password = db.Column(db.String(92))
    username = db.Column(db.String(80))
    vkuid = db.Column(db.Integer)
    gcmid = db.Column(db.String(200))

    def __repr__(self):
        return '<User %r>' % self.username

    @staticmethod
    def get_user_by_token(token):
        session = Session.query.filter_by(token=token).one()
        return User.query.get(session.user_id)


class Session(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    token = db.Column(db.String(36))
    act_date = db.Column(db.DateTime)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship('User',
        backref=db.backref('users', lazy='dynamic'))

    def __init__(self, user):
        self.user = user
        self.act_date = datetime.utcnow()
        self.token = uuid.uuid4()

    def __repr__(self):
        return '<Post %r>' % self.title


class Queue(db.Model):
    id = db.Column(db.Integer, primary_key=True)

    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship('User', backref=db.backref('q_users', lazy='dynamic'))
    name = db.Column(db.String(255))
    description = db.Column(db.Text)
    current = db.Column(db.Integer)
    created = db.Column(db.DateTime, default=datetime.utcnow())
    closed = db.Column(db.DateTime)

    def __init__(self, user, name):
        self.user = user
        self.name = name

    def __repr__(self):
        return '<Queue %r>' % self.name
