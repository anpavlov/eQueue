# coding=utf-8
import settings
from utils import mysql
from flask import Flask

from apis.user import user

app = Flask(__name__)
app.config.from_object(settings)

mysql.init_app(app)

app.register_blueprint(user, url_prefix='/api')


@app.route("/")
def hello():
    return "Hello World!"


if __name__ == "__main__":
    app.run()
