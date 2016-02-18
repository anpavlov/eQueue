# coding=utf-8
from utils import mysql
from flask import request, Blueprint, render_template, redirect, url_for, make_response
import json

user = Blueprint('user', __name__)


@user.route("/")
def admin_page():
    return modules()


@user.route("/modules")
def modules():
    user_id = get_logged_user_id(request)
    if user_id is None:
        return redirect('/login')
    modules_count, new_modules_count, all_modules = get_all_modules()
    ctx = {'modules_count': modules_count, 'all_modules': all_modules, 'new_modules_count': new_modules_count}
    return render_template("modules.html", **ctx)