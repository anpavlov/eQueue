import tarantool


class Manager():
    def __init__(self, conn, schema):
        self.conn = conn
        self.schema = schema

    def get_space(self, space_name):
        return self.conn.space(space_name)

    def insert(self, space_name, **kwargs):
        return self.conn.call("auto_inc_insert", space_name, 'cdkecm', 'ueuergg')

    def select_assoc(self, space_name, where_tuple, index, asc=True):
        iter = 0 if asc else 1
        res = self.get_space(space_name).select(where_tuple, index=index, iterator=iter)
        # make it assoc
        assoc = []
        for k, row in enumerate(res):
            x = {}
            for i, val in enumerate(row):
                x[self.schema[space_name]['fields'][i][0]] = val
            assoc.append(x)
        return assoc