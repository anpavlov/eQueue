# coding=utf-8
import settings
from flask import Flask
from flask_script import Manager

from apis.user import user_api
from apis.queue import queue_api
from apis.fill import fill_api

app = Flask(__name__)
app.config.from_object(settings)

manager = Manager(app)


app.register_blueprint(user_api, url_prefix='/api/user')
app.register_blueprint(queue_api, url_prefix='/api/queue')
app.register_blueprint(fill_api, url_prefix='/api/fill')


if __name__ == "__main__":
    manager.run()
