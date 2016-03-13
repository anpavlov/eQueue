box.cfg{
    listen = 3301,
    wal_dir='xlog',
    snap_dir='snap',
}

local s = box.schema.space.create('standings', {if_not_exists = true})
s:create_index('primary', {type = 'tree', parts = {1, 'NUM', 2, 'NUM'}, if_not_exists = true})
s:create_index('secondary', {type='tree', parts = {1, 'NUM', 5, 'NUM'}, if_not_exists = true})
s:truncate()

local users = box.schema.space.create('users', {if_not_exists = true})
users:create_index('primary', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true})
users:create_index('email', {type = 'tree', parts = {2, 'STR'}, unique = true, if_not_exists = true})
users:truncate()

local session = box.schema.space.create('session', {if_not_exists = true})
session:create_index('primary', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true})
session:truncate()

--box.schema.user.grant('guest', 'read,write', 'space', 'standings')



-- auth testing: access control
--if not box.schema.user.exists('test') then
--    box.schema.user.create('test', {password = 'test'})
--    box.schema.user.grant('test', 'read,write,execute', 'universe')
--end
--


-- functions area:
function auto_inc_insert(space_name, tuple)
    local res = box.space[space_name]:auto_increment{tuple}
    return res
end

box.schema.user.grant('guest', 'read,write,execute', 'universe', nil, {if_not_exists=true})

local console = require 'console'
console.listen '0.0.0.0:33015'