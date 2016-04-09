# eQueue

dependencies:
`pip install flask flask-migrate flask-sqlalchemy flask-script tarantool flask-mysql python-gcm`


#API Documentation

##User
* [create](./doc/user/create.md)
* [details](./doc/user/details.md)
* [update](./doc/user/update.md)

##Queue


#Deploy
Execute deploy.sh

#tarantool new solution
Copy equeue.lua file as config to /etc/tarantool/instances.available/
Then make symlink in /etc/tarantool/instances.enabled/ to this file

#tarantool start (temporary solution)
```bash
box.cfg{listen=3301}
s = box.schema.space.create('standings')
s:create_index('primary', {type = 'tree', parts = {1, 'NUM', 2, 'NUM'}})
s:create_index('secondary', {type='tree', parts = {1, 'NUM', 5, 'NUM'}})
users = box.schema.space.create('users')
users:create_index('primary', {type = 'tree', parts = {1, 'NUM'}})
users:create_index('email', {type = 'tree', parts = {2, 'STR'}, unique = true})
session = box.schema.space.create('session')
session:create_index('primary', {type = 'tree', parts = {1, 'NUM'}})
box.schema.user.grant('guest', 'read,write', 'space', 'standings')
```