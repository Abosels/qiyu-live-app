-- KEYS[1] = PK state Hash
-- ARGV[1] = scoreA
-- ARGV[2] = scoreB
-- return {-1} when PK is no longer running, otherwise current snapshot

local pkKey = KEYS[1]
if redis.call('HGET', pkKey, 'status') ~= 'RUNNING' then
    return {-1}
end

redis.call('HSET', pkKey, 'scoreA', ARGV[1], 'scoreB', ARGV[2])
local version = redis.call('HINCRBY', pkKey, 'version', 1)
return {ARGV[1], ARGV[2], version, 'RUNNING'}
