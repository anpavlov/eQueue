# types
TARANTOOL_STR = 1
TARANTOOL_NUM = 2
TARANTOOL_NUM64 = 3
TARANTOOL_RAW = 4

# structure: (NAME, TYPE, DEFAULT)

schema = {
    'users': {
        'fields': [
            ('id', TARANTOOL_NUM64, None),  # primary
            ('email', TARANTOOL_STR, ''),  # unique
            ('password', TARANTOOL_STR, ''),
            ('username', TARANTOOL_STR, ''),
            ('vkuid', TARANTOOL_NUM, 0),
            ('gcmid', TARANTOOL_STR, '')
        ]
    },
    'sessions': {
        'fields': [
            ('id', TARANTOOL_NUM64, None),  # primary
            ('token', TARANTOOL_STR, ''),  # unique
            ('act_date', TARANTOOL_NUM, 0),
            ('user_id', TARANTOOL_NUM, 0)
        ]
    },
    'queues': {
        'fields': [
            ('id', TARANTOOL_NUM64, None),
            ('user_id', TARANTOOL_NUM64, 0),
            ('name', TARANTOOL_STR, ''),
            ('description', TARANTOOL_STR, ''),
            ('current', TARANTOOL_NUM64, 0),
            ('created', TARANTOOL_NUM, 0),
            ('closed', TARANTOOL_NUM, 0)
        ]
    }
}