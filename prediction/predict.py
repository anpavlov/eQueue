import random
import time

USER_AVG = 19


def predict(position, stand_time):
    if position == -1:
        wait_time = 18  # TODO: change it
    else:
        wait_time = position * USER_AVG + random.randint(-1, 1)

    # subtract elapsed time
    wait_time -= float(int(time.time()) - stand_time) / 60.

    if wait_time < 0:
        return 1
    return int(wait_time)
