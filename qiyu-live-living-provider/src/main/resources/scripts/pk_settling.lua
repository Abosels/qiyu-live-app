-- KEYS[1] = PK state Hash
-- ARGV[1] = current epoch milliseconds
-- return {claimed, scoreA, scoreB, version, status}

local pkKey = KEYS[1]
local now = tonumber(ARGV[1])
local status = redis.call('HGET', pkKey, 'status')
local endTime = tonumber(redis.call('HGET', pkKey, 'endTime') or '0')

if status ~= 'RUNNING' or endTime > now then
    return {0, '', '', '', status or ''}
end

redis.call('HSET', pkKey, 'status', 'SETTLING')
local version = redis.call('HINCRBY', pkKey, 'version', 1)
return {1, redis.call('HGET', pkKey, 'scoreA'), redis.call('HGET', pkKey, 'scoreB'), version, 'SETTLING'}
