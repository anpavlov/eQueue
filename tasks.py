from celery import Celery
import requests

app = Celery('tasks', broker='amqp://guest@localhost//')


@app.task
def notify(payload):
    requests.post('http://localhost:8888/post/', data=payload)