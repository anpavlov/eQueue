# coding=utf-8
from flask import request, Blueprint

user_api = Blueprint('user', __name__)


@user_api.route("/")
def create():
    return "hello"
