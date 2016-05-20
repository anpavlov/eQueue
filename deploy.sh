#!/usr/bin/env bash
cd ~/eQueue/
git pull >& /dev/null
cd ..
kill $(ps aux | grep 'tarantool' | awk '{print $2}') >& /dev/null
nohup ~/tarantool/tarantool/src/tarantool ~/eQueue/equeue.lua > tarantool.out 2> tarantool.out < /dev/null &
sleep 1
kill $(ps aux | grep 'main.py runserver' | awk '{print $2}') >& /dev/null
nohup python eQueue/main.py runserver -p 30212 > flask.out 2> flask.out < /dev/null &
kill $(ps aux | grep 'tasks' | awk '{print $2}') >& /dev/null
cd eQueue
nohup celery -A tasks worker -c 5 --loglevel=info > ../celery.out 2> ../celery.out < /dev/null &
echo 'done'
