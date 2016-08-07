import tarantool
from taran import helper
import spaces
import settings

# tarantool connection
tarantool_conn = tarantool.connect(settings.TARANTOOL_HOST, settings.TARANTOOL_PORT)
tarantool_manager = helper.Manager(tarantool_conn, spaces.schema)
