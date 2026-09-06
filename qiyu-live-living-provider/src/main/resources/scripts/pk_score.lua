-- KEYS[1] = PK state Hash
-- KEYS[2] = score dedupe Key: pkId + bizId
-- ARGV[1] = side (A/B)
-- ARGV[2] = addScore
-- ARGV[3] = dedupe TTL in seconds
-- return {-1}: not running; {-2}: already scored; otherwise scoreA, scoreB, version, status

local pkKey = KEYS[1]
local dedupeKey = KEYS[2]
local side = ARGV[1]
local addScore = tonumber(ARGV[2])
local dedupeTtl = tonumber(ARGV[3])

local status = redis.call('HGET', pkKey, 'status')
if status ~= 'RUNNING' then
    return {-1}
end

if redis.call('SET', dedupeKey, '1', 'NX', 'EX', dedupeTtl) == false then
    return {-2}
end

if side == 'A' then
    redis.call('HINCRBY', pkKey, 'scoreA', addScore)
else
    redis.call('HINCRBY', pkKey, 'scoreB', addScore)
end

local version = redis.call('HINCRBY', pkKey, 'version', 1)
local scoreA = redis.call('HGET', pkKey, 'scoreA')
local scoreB = redis.call('HGET', pkKey, 'scoreB')

return {scoreA, scoreB, version, status}
