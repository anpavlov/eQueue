import tarantool


class Manager():
    def __init__(self, conn, schema):
        self.conn = conn
        self.schema = schema

    def get_space(self, space_name):
        return self.conn.space(space_name)

    def insert(self, space_name, **kwargs):
        return self.conn.call("insert_to_space", 'test')