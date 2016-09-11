# types
TARANTOOL_STR = 1
TARANTOOL_NUM = 2
TARANTOOL_NUM64 = 3
TARANTOOL_RAW = 4
TARANTOOL_ARRAY = 5

# structure: (NAME, TYPE, DEFAULT)

schema = {
    'users': {
        'fields': [
            ('id', TARANTOOL_NUM64, None),  # primary
            ('email', TARANTOOL_STR, ''),  # unique
            ('password', TARANTOOL_STR, ''),
            ('username', TARANTOOL_STR, ''),
            ('vkuid', TARANTOOL_NUM, 0),
            ('gcmid', TARANTOOL_STR, None)
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
            ('closed', TARANTOOL_NUM, 0),
            ('class', TARANTOOL_STR, ''),
            ('coords', TARANTOOL_ARRAY, []),
            ('is_closed', TARANTOOL_NUM, False)  # have no implementation yet
        ]
    },
    'stats': {
        'fields': [
            ('id', TARANTOOL_NUM64, None),
            ('qid', TARANTOOL_NUM64, 0),
            ('uid', TARANTOOL_NUM64, 0),
            ('wait_time', TARANTOOL_NUM64, 0),  # in seconds
            ('time_in', TARANTOOL_NUM, 0),
            ('time_out', TARANTOOL_NUM, 0),
            ('class', TARANTOOL_STR, '')
        ]
    },
    'coefs': {
        'fields': [
            ('id', TARANTOOL_NUM64, None),
            ('class', TARANTOOL_STR, ''),
            ('wait_avg', TARANTOOL_NUM, 0)
        ]
    }
}