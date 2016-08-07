import random
import time

USER_AVG = 19


def predict(position, stand_time, pos=0):
    if position == -1:
        wait_time = pos * USER_AVG
    else:
        wait_time = position * USER_AVG

    # subtract elapsed time
    if stand_time != 0:
        wait_time -= float(int(time.time()) - stand_time) / 60.

    if wait_time < 0:
        return 1
    return int(wait_time)
