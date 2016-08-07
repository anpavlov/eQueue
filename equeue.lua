box.cfg{
    listen = 3301,
--    wal_dir='xlog',
--    snap_dir='snap',
}

local s = box.schema.space.create('standings', {if_not_exists = true})
s:create_index('primary', {type = 'tree', parts = {1, 'NUM', 2, 'NUM'}, if_not_exists = true})
s:create_index('qid', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true, unique = false})
s:create_index('secondary', {type='tree', parts = {1, 'NUM', 5, 'NUM'}, if_not_exists = true, unique = false})
s:create_index('user_id', {type = 'tree', parts = {2, 'NUM'}, if_not_exists = true, unique = false})
s:create_index('qid_u', {type = 'tree', parts = {1, 'NUM', 2, 'NUM'}, if_not_exists = true, unique = false})
--s:truncate()

local users = box.schema.space.create('users', {if_not_exists = true})
users:create_index('primary', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true})
users:create_index('email', {unique = false, type = 'tree', parts = {2, 'STR'}, if_not_exists = true})
users:create_index('vkuid', {type = 'tree', parts = {5, 'NUM'}, unique = false, if_not_exists = true})
--users:truncate()

local session = box.schema.space.create('sessions', {if_not_exists = true})
session:create_index('primary', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true})
session:create_index('token', {type='tree', parts = {2, 'STR'}, if_not_exists = true})
session:create_index('user', {type='tree', parts = {4, 'NUM'}, unique = false, if_not_exists = true})
--session:truncate()

local queues = box.schema.space.create('queues', {if_not_exists = true})
queues:create_index('primary', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true})
queues:create_index('qid_user', {type = 'tree', parts = {1, 'NUM', 2, 'NUM'}, if_not_exists = true})
queues:create_index('userid', {type = 'tree', parts = {2, 'NUM'}, unique = false, if_not_exists = true})
queues:create_index('name', {type = 'tree', parts = {3, 'STR'}, unique = false, if_not_exists = true})
queues:create_index('coords', {type = 'rtree', parts = {9, 'array'}, unique = false, if_not_exists = true})
--queues:truncate()

local stats = box.schema.space.create('stats', {if_not_exists = true})
stats:create_index('primary', {type = 'tree', parts = {1, 'NUM'}, if_not_exists = true})
stats:create_index('qid', {type = 'tree', parts = {2, 'NUM'}, unique = false, if_not_exists = true})

local coefs = box.schema.space.create('coefs', {if_not_exists = true})
coefs:create_index('primary', {type='tree', parts = {1, 'NUM'}, if_not_exists = true})



-- functions area:
function auto_inc_insert(space_name, ...)
    local res = box.space[space_name]:auto_increment{...}
    return res
end

function search_by_like(space_name, index_name, col_number, value)
    local space_values = box.space[space_name].index[index_name]:select{}
    local result = {}
    local k = 1
    for i,v in ipairs(space_values) do
        local res = string.match(string.lower(tostring(v[col_number])), '%w*' .. string.lower(value) .. '%w*')
        if res ~= nil and res ~= '' then
            result[k] = v
            k = k + 1
        end
    end
    return result
end

function search_by_coords(space_name, index_name, value)
    local result = box.space[space_name].index[index_name]:select(value, {iterator='overlaps'})
    return result
end

-- returns user's position in queue or -1
function user_number(qid, uid)
    local result = box.space.standings.index.secondary:select{qid}
    local k = 0
    for i,q in ipairs(result) do
        if q[2] == uid then
            return k + 1
        end
        k = k + 1
    end
    return -1
end

-- returns user's timestamp of standing
function user_timestamp(qid, uid)
    local result = box.space.standings.index.primary:select{qid, uid}
    return result[1][5]
end

box.schema.user.grant('guest', 'read,write,execute', 'universe', nil, {if_not_exists=true})

local console = require 'console'
console.listen '0.0.0.0:33015'