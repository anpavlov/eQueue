# eQueue

dependencies:
`pip install flask flask-migrate flask-sqlalchemy flask-script tarantool flask-mysql`


#API Documentation

##User
* [create](./doc/user/create.md)
* [details](./doc/user/details.md)
* [update](./doc/user/update.md)

#tarantool start
```bash
$ tarantool
tarantool> box.cfg{listen=3301}
tarantool> s = box.schema.space.create('standings')
tarantool> s:create_index('primary', {type = 'hash', parts = {1, 'NUM'}})
```