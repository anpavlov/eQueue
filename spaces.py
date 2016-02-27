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