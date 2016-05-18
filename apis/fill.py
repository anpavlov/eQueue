# coding=utf-8
from flask import request, Blueprint
import json
from taran import tarantool_manager
import numpy as np
import random
from faker import Factory
import time
import responses


fake = Factory.create()

fill_api = Blueprint('fill', __name__)


@fill_api.route("/start/", methods=['POST'])
def start():
    try:
        n = int(request.form['n'])
    except (KeyError, ValueError):
        return json.dumps(responses.BAD_REQUEST)

    for _ in xrange(n):
        create_queue()

    return json.dumps({
        'ok': 'ok'
    })


def create_queue():
    input_lambda = 10  # avg rate - each 10 mins
    output_mu = 9  # avg rate - each 9 mins
    count_people = random.randint(6, 50)
    input_times = list(np.random.poisson(input_lambda, count_people))
    output_times = list(np.random.exponential(output_mu, count_people))
    # create queue
    created_time = time.time() - random.randint(10, 100000)
    queue = {
        'name': ' '.join(fake.words()),
        'description': ' '.join(fake.words()),
        'created': created_time,
        'coords': [0, 0]
    }
    q = tarantool_manager.insert('queues', queue)

    # modeling inputs
    for i in range(count_people - 1):
        input_times[i + 1] += input_times[i]

    prev_out = created_time
    for i in range(count_people):
        time_in = created_time + input_times[i] * 60
        if prev_out > time_in:
            time_out = prev_out + output_times[i] * 60
        else:
            time_out = time_in + output_times[i] * 60
        prev_out = time_out

        stat_data = {
            'qid': q['id'],
            'wait_time': time_out - time_in,
            'time_in': time_in,
            'time_out': time_out,
            'class': q['class']
        }

        tarantool_manager.insert('stats', stat_data)