from celery import Celery
import socket
import json
from taran import tarantool_manager

app = Celery('tasks', broker='amqp://guest@localhost//')
app.config_from_object('celeryconfig')


@app.task
def notify(payload):
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    s.connect("/home/tech/websocket/websocket_daemon.sock")
    s.send(json.dumps(payload))
    s.close()
    # requests.post('http://localhost:8888/post/', data=payload)


@app.task
def update_coefs():
    print 'updating'
    coefs = {
        'class': '',
        'wait_avg': 19
    }
    tarantool_manager.insert('coefs', coefs)