from celery import Celery
import socket
import json

app = Celery('tasks', broker='amqp://guest@localhost//')


@app.task
def notify(payload):
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    s.connect("/home/tech/websocket/websocket_daemon.sock")
    s.send(json.dumps(payload))
    s.close()
    # requests.post('http://localhost:8888/post/', data=payload)
