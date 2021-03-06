import random
import time

USER_AVG = 19  # koef

START_AVG = 2  # start using avg


def predict(position, stand_time, avg=0, total_in_queue=0, passed=0):
    global USER_AVG
    if passed >= START_AVG:
        USER_AVG = avg / 60.

    if position == -1:
        wait_time = total_in_queue * USER_AVG
    else:
        wait_time = position * USER_AVG

    # subtract elapsed time
    if stand_time != 0:
        wait_time -= float(int(time.time()) - stand_time) / 60.

    if wait_time < 0:
        return 1
    return int(wait_time)
