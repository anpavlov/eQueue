# coding=utf-8
from flaskext.mysql import MySQL
from werkzeug.security import generate_password_hash, check_password_hash
from settings import SESSION_COOKIE_STRING, SESSION_COOKIE_LENGTH
import random
import string
# from flask import request

mysql = MySQL()


def get_logged_user_id(req):
    cookie = req.cookies.get(SESSION_COOKIE_STRING)
    if cookie == '':
        return None
    conn = mysql.connect()
    cursor = conn.cursor()
    cursor.execute("SELECT user_id FROM Session WHERE cookie = %s", (cookie,))
    row = cursor.fetchone()
    if row is None:
        return None
    else:
        return row[0]


def try_login_admin(name, password):
    conn = mysql.connect()
    cursor = conn.cursor()
    cursor.execute("SELECT id, password, is_admin FROM User WHERE name = %s", (name,))
    row = cursor.fetchone()
    if row is None:
        return None
    if row[2] != 1:
        return None
    if check_password_hash(row[1], password):
        return row[0]
    else:
        return None


def set_new_session(user_id, resp):
    conn = mysql.connect()
    cursor = conn.cursor()
    # sid = ''
    while True:
        sid = ''.join(random.SystemRandom().choice(string.digits + string.ascii_letters)
                      for _ in xrange(SESSION_COOKIE_LENGTH))
        cursor.execute("SELECT 1 FROM Session WHERE cookie = %s", (sid,))
        row = cursor.fetchone()
        if row is None:
            break
    cursor.execute("INSERT INTO Session (user_id, cookie) VALUES (%s, %s)", (user_id, sid))
    conn.commit()
    resp.set_cookie(SESSION_COOKIE_STRING, value=sid)