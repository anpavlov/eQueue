BAD_REQUEST = {'code': 400, 'body': {'error': 'Неверные параметры для запроса'}}
INVALID_TOKEN = {'code': 403, 'body': {'error': 'Ваша сессия устарела'}}
QUEUE_NOT_FOUND = {'code': 404, 'body': {'error': 'Очередь не найдена'}}
ALREADY_IN_QUEUE = {'code': 400, 'body': {'error': 'Вы уже стоите в этой очереди'}}
EMPTY_QUEUE = {'code': 400, 'body': {'error': 'В очереди нет ни одного человека'}}
EMAIL_BUSY = {'code': 403, 'body': {'error': 'E-mail уже зарегистрирован'}}
ACCESS_DENIED = {'code': 403, 'body': {'error': 'Неверная пара email/password'}}
UNDEFINED_USER = {'code': 200, 'body': {'error': 'Неопределенный пользователь'}}
