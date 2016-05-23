import random


def predict(position):
    if position == -1:
        return 18
    if position == 0:
        return 8
    return position * 19