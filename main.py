# coding=utf-8
import settings
from flask import Flask
from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand
from models import db
import tarantool
from taran import helper
import spaces

from apis.user import user_api
from apis.queue import queue_api

app = Flask(__name__)
app.config.from_object(settings)

db.init_app(app)
manager = Manager(app)


app.register_blueprint(user_api, url_prefix='/api/user')
app.register_blueprint(queue_api, url_prefix='/api/queue')


@app.route("/")
def hello():
    return "Hello World!"


if __name__ == "__main__":
    manager.run()
