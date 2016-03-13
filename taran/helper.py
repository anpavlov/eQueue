import tarantool


class Manager():
    def __init__(self, conn, schema):
        self.conn = conn
        self.schema = schema

    def get_space(self, space_name):
        return self.conn.space(space_name)