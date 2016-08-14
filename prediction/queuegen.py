import random
import numpy


class Queue:

    def __init__(self, size):
        self.size = size
        self.mean = []
        self.data = []

    def get_const_mean(self, vmin, vmax):
        val = random.randint(vmin, vmax)
        self.mean = [val for i in range(self.size)]

    def get_dynamic_mean(self, vmin, vmax, tmin, tmax):

        self.mean.append(random.randint(vmin, vmax))
        for i in range(self.size):
            if len(self.mean) > i:
                continue
            scale = random.randint(0, vmax - vmin)
            val = scale * (2*(random.random()-0.5))**3
            start_time = random.randint(int(tmin + (tmax-tmin)*(val-vmin)/(vmax-vmin)), tmax)
            if val >= 0:
                if self.mean[-1] + val < vmax:
                    avg = self.mean[-1] + val
                else:
                    avg = vmax
            if val < 0:
                if self.mean[-1] + val > vmin:
                    avg = self.mean[-1] + val
                else:
                    avg = vmin
            for k in range(start_time):
                if len(self.mean) < self.size:
                    self.mean.append(avg)

    def get_normal(self):
        for i in range(self.size):
            random_val = self.mean[i] + self.mean[i] * random.random()*random.uniform(-1, 1)**15
            self.data.append(random_val)

        return self.data


class NormalConstQueue(Queue):
    def get_data(self, vmin, vmax):
        self.get_const_mean(vmin, vmax)
        return self.get_normal()


class NormalDynamicQueue(Queue):
    def get_data(self, vmin, vmax, tmin, tmax):
        self.get_dynamic_mean( vmin, vmax, tmin, tmax)
        return self.get_normal()


class RangeQueue(Queue):
    def get_data(self, vmin, vmax, count):
        data = []
        for i in range(count):
            q = NormalConstQueue(self.size)
            d = q.get_data(vmin, vmax)
            data.append(d)

        result = []
        for i in range(self.size):
            result.append(data[int(count*random.random()**3)][i])

        return result


class DynamicQueue(Queue):
    def get_data(self, vmin, vmax):
        data = []
        for i in range(self.size):
            data.append(vmin + (vmax - vmin) * abs(numpy.random.standard_normal()))

        return data