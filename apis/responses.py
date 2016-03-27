BAD_REQUEST = {'code': 400, 'body': {'error': 'invalid request params'}}
INVALID_TOKEN = {'code': 403, 'body': {'error': 'invalid token'}}
QUEUE_NOT_FOUND = {'code': 404, 'body': {'error': 'queue was not found'}}
ALREADY_IN_QUEUE = {'code': 400, 'body': {'error': 'you have been already in this queue'}}
EMPTY_QUEUE = {'code': 400, 'body': {'error': 'empty queue'}}
EMAIL_BUSY = {'code': 403, 'body': {'error': 'email is busy'}}
ACCESS_DENIED = {'code': 403, 'body': {'error': 'wrong pair email/password'}}
UNDEFINED_USER = {'code': 200, 'body': {'error': 'undefined user'}}