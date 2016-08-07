import uuid
import time


class NoResult(Exception):
    pass


class Manager:
    def __init__(self, conn, schema):
        self.conn = conn
        self.schema = schema

    def get_space(self, space_name):
        return self.conn.space(space_name)

    def insert(self, space_name, info):
        # form tuple from assoc values
        data = self._get_default_entity(space_name)
        for key, val in info.iteritems():
            data[self._get_position_by_key(space_name, key)] = val
        res = self.conn.call("auto_inc_insert", space_name, *data)
        return self._make_assoc(space_name, res)[0]

    def select_assoc(self, space_name, where_tuple, index='primary', asc=True):
        iter = 0 if asc else 1
        res = self.get_space(space_name).select(where_tuple, index=index, iterator=iter)
        if not res:
            raise NoResult
        return self._make_assoc(space_name, res)

    def select_by_like(self, space_name, index_name, field, value):
        col_number = self._get_position_by_key(space_name, field) + 2
        res = self.conn.call("search_by_like", space_name, index_name, col_number, value)
        return self._make_assoc(space_name, res)

    def select_by_coords(self, space_name, index_name, value):
        res = self.conn.call("search_by_coords", space_name, index_name, value)
        return self._make_assoc(space_name, res)

    def simple_update(self, space_name, key, values):
        ops = []
        for k, value in values.iteritems():
            ops.append(('=', self._get_position_by_key(space_name, k) + 1, value))
        res = self.get_space(space_name).update(key, ops)
        return self._make_assoc(space_name, res)

    def delete(self, space_name, key):
        self.get_space(space_name).delete(key)

    def get_user_by_token(self, token):
        session = self.select_assoc('sessions', (token), index='token')
        if not session:
            raise NoResult()
        try:
            user = self.select_assoc('users', (session[0]['user_id']))
        except KeyError:
            raise NoResult()
        if not user[0]:
            raise NoResult()
        return user[0]

    def create_session(self, user):
        session = {
            'user_id': user['id'],
            'token': str(uuid.uuid4()),
            'act_date': int(time.time())
        }
        self.insert('sessions', session)
        return session['token']

    def get_user_position(self, qid, uid):
        res = self.conn.call("user_number", qid, uid)
        if not res:
            return 0
        return int(res[0][0])

    def get_stand_timestamp(self, qid, uid):
        res = self.conn.call("user_timestamp", qid, uid)
        if not res:
            return 0
        try:
            return int(res[0][0])
        except ValueError:
            return 0

    def get_total_count_in_queue(self, qid):
        res = self.conn.call("total_count", qid)
        if not res:
            return 0
        try:
            return int(res[0][0])
        except ValueError:
            return 0

    def _get_position_by_key(self, space_name, key):
        for i, val in enumerate(self.schema[space_name]['fields']):
            if val[0] == key:
                return i - 1

    def _get_default_entity(self, space_name):
        data = []
        for val in self.schema[space_name]['fields']:
            if val[0] != 'id':
                data.append(val[2])
        return data

    def _make_assoc(self, space_name, response):
        assoc = []
        for k, row in enumerate(response):
            x = {}
            for i, val in enumerate(row):
                x[self.schema[space_name]['fields'][i][0]] = val
            assoc.append(x)
        return assoc
