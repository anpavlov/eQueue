# coding=utf-8
import settings
from flask import Flask
from flask_script import Manager
from flask_migrate import Migrate, MigrateCommand
from models import db

from apis.user import user_api

app = Flask(__name__)
app.config.from_object(settings)

db.init_app(app)
migrate = Migrate(app, db)
manager = Manager(app)
manager.add_command('db', MigrateCommand)

app.register_blueprint(user_api, url_prefix='/api')


@app.route("/")
def hello():
    return "Hello World!"


if __name__ == "__main__":
    manager.run()
