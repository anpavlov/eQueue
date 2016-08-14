import random

import pandas
from sklearn.metrics import mean_squared_error


class Predictor(object):
    MAX_SLICE = 50
    LEARN_STEPS = 1000

    def __init__(self):
        self.data = list()
        self.param = 0
        self.quality = 0

    def fit(self, data):
        param_arr = list()

        for sl_param in range(self.MAX_SLICE):

            true_arr = list()
            pred_arr = list()

            for i in range(self.LEARN_STEPS):
                arr, index, true_time = prepare_predict_data(data)
                users_before = index - len(arr)
                median = pandas.Series(arr[-sl_param:]).median()
                pred_time = median * users_before

                true_arr.append(true_time)
                pred_arr.append(pred_time)

            param_arr.append(mean_squared_error(true_arr, pred_arr))

        self.param = pandas.Series(param_arr).idxmin()
        self.quality = pandas.Series(param_arr).min()

    def get_quality(self):
        return self.quality

    def predict(self, arr, index):
        users_before = index - len(arr)
        median = pandas.Series(arr[-self.param:]).median()
        return median * users_before


def prepare_predict_data(arr):
    temp = random.randint(0, len(arr) - 3)
    index = random.randint(temp + 2, len(arr) - 1)
    time = sum(arr[temp+1:index])
    return arr[:temp + 1], index, time


def test_predictor(data, pr):
    error_arr = list()

    distance_arr = list()

    for i in range(100):
        arr, index, true_time = prepare_predict_data(data)
        pred_time = pr.predict(arr, index)

        distance = index - len(arr)
        distance_arr.append(distance)

        error_arr.append(abs(true_time-pred_time))

    return pandas.Series(error_arr), distance_arr