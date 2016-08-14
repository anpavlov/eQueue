import random

import imp
try:
    matplotlib_info = imp.find_module('matplotlib')
    matplotlib = imp.load_module('matplotlib', *matplotlib_info)
    imp.find_module('pyplot', matplotlib.__path__)
    import matplotlib.pyplot as plt
    plot = True
except ImportError:
    plot = False

if not plot:
    print "Module matplotlib.pyplot is not installed. Plots will not show!!!"

from predictor import Predictor, test_predictor
from queuegen import NormalDynamicQueue, RangeQueue, DynamicQueue

random.seed(1024)

N = 500  # size  of data
SPLITTER = int(N * 0.8)  # 80% for learning, 20% for testing predictor

predictor = Predictor()

# 1 - monotone queue
print 'Monotone queue with dynamic mean'
q = NormalDynamicQueue(N)
data = q.get_data(vmin=5, vmax=8, tmin=5, tmax=20)  # [t1, t2, t3, .. tn]

predictor.fit(data[:SPLITTER])  # learning predictor with learning subset
print '    Parametr: %d' % predictor.param  # param from learning
print '    Quality: %.2f' % predictor.get_quality()  # mean_squared_error

# OK, now we can test predictor
# test_data - absolute error between true and predict time
# distance - count of people before predict user
test_data, distance = test_predictor(data[SPLITTER:], predictor)
print '    Error: %.2f min' % test_data.median()
print

if plot:
    plt.subplot(311)
    plt.plot(data)  # time line queue
    plt.subplot(312)
    plt.hist(test_data, 100)  # histogram of errors
    plt.subplot(313)
    plt.plot(distance, test_data, 'o')  # dependence error time from distance
    plt.show()


# 2 - range queue
print 'Range queue with const mean'
q = RangeQueue(N)
data = q.get_data(vmin=2, vmax=12, count=3)
predictor.fit(data[:SPLITTER])
print '    Parametr: %d' % predictor.param
print '    Quality: %.2f' % predictor.get_quality()

test_data, distance = test_predictor(data[SPLITTER:], predictor)
print '    Error: %.2f min' % test_data.median()
print

if plot:
    plt.subplot(311)
    plt.plot(data)
    plt.subplot(312)
    plt.hist(test_data, 100)
    plt.subplot(313)
    plt.plot(distance, test_data, 'o')
    plt.show()


# 3 - dynamic queue
print 'Dinamic queue'
q = DynamicQueue(N)
data = q.get_data(vmin=3, vmax=8)
predictor.fit(data[:SPLITTER])
print '    Parametr: %d' % predictor.param
print '    Quality: %.2f' % predictor.get_quality()

test_data, distance = test_predictor(data[SPLITTER:], predictor)
print '    Error: %.2f min' % test_data.median()
print

if plot:
    plt.subplot(311)
    plt.plot(data)
    plt.subplot(312)
    plt.hist(data, 100)
    plt.subplot(313)
    plt.plot(distance, test_data, 'o')
    plt.show()
