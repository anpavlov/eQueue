import random


def predict(position):
    if position == -1:
        return 3
    if position == 0:
        return 1
    return position * 3