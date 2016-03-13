import tarantool

# schema = {
#     0: {  # Space description
#           'name': 'standings',  # Space name
#           'default_type': tarantool.STR,  # Type that used to decode fields that are not listed below
#           'fields': {
#               0: ('standing_id', tarantool.NUM),
#               1: ('queue_id', tarantool.NUM64),
#               2: ('user_id', tarantool.STR),
#               3: ('prev_u_id', tarantool.)
#           },
#           'indexes': {
#               0: ('pk', [0]),  # (name, [field_no])
#           }
#     }
# }

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
    }
}