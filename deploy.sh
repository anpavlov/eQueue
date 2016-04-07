cd ~/eQueue/
git pull >& /dev/null
cd ..
kill $(ps aux | grep 'tarantool' | awk '{print $2}') >& /dev/null
nohup ~/tarantool/tarantool/src/tarantool ~/eQueue/equeue.lua > Output.out 2> Error.err < /dev/null &
kill $(ps aux | grep 'main.py runserver' | awk '{print $2}') >& /dev/null
nohup python eQueue/main.py runserver -p 30212 > serv.out 2> Error.err < /dev/null &
echo 0