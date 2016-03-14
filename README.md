# eQueue

dependencies:
`pip install flask flask-migrate flask-sqlalchemy flask-script tarantool flask-mysql python-gcm`


#API Documentation

##User
* [create](./doc/user/create.md)
* [details](./doc/user/details.md)
* [update](./doc/user/update.md)

##Queue


#tarantool start (temporary solution)
```bash
$ tarantool
tarantool> box.cfg{listen=3301}
tarantool> s = box.schema.space.create('standings')
tarantool> s:create_index('primary', {type = 'tree', parts = {1, 'NUM', 2, 'NUM'}})
tarantool> s:create_index('secondary', {type='tree', parts = {1, 'NUM', 5, 'NUM'}})
tarantool> s:create_index('user_id', {type = 'tree', parts = {2, 'NUM'}, if_not_exists = true})
tarantool> box.schema.user.grant('guest', 'read,write', 'space', 'standings')
```