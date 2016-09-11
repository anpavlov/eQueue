from socketIO_client import SocketIO, LoggingNamespace
from celery import Celery
import socket
import json

from taran import tarantool_manager

app = Celery('tasks', broker='amqp://guest@localhost//')
app.config_from_object('celeryconfig')

class SocServer(object):
    socket = None

    def get_socket(self):
        if not SocServer.socket:
            SocServer.socket = SocketIO('localhost', 7000, LoggingNamespace)
        return SocServer.socket

@app.task
def notify(payload):
    print 'Send refresh for queue: %d' % payload['qid']
    SocServer().get_socket().emit('apiserver', payload['qid'])

# @app.task
# def update_coefs():
#     print 'updating'
#     coefs = {
#         'class': '',
#         'wait_avg': 19
#     }
#     tarantool_manager.insert('coefs', coefs)
