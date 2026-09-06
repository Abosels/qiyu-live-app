-- KEYS[1] = global PK waiting ZSet
-- KEYS[2] = current room waiting marker
-- ARGV[1] = roomId
-- ARGV[2] = apply timestamp
-- ARGV[3] = marker TTL seconds
-- ARGV[4] = waiting marker key prefix

local poolKey = KEYS[1]
local myWaitKey = KEYS[2]
local myRoomId = ARGV[1]
local myScore = ARGV[2]
local ttl = tonumber(ARGV[3])
local waitKeyPrefix = ARGV[4]

if redis.call('EXISTS', myWaitKey) == 1 then
    return {'DUPLICATE', ''}
end

local members = redis.call('ZRANGE', poolKey, 0, -1)
for i = 1, #members do
    local candidate = members[i]
    if candidate ~= myRoomId then
        -- A ZSet member without its TTL marker is stale. Remove and keep looking.
        if redis.call('EXISTS', waitKeyPrefix .. candidate) == 1 then
            redis.call('ZREM', poolKey, candidate)
            redis.call('DEL', waitKeyPrefix .. candidate)
            return {'MATCHED', candidate}
        end
        redis.call('ZREM', poolKey, candidate)
    end
end

redis.call('ZADD', poolKey, myScore, myRoomId)
redis.call('SETEX', myWaitKey, ttl, myScore)
return {'WAITING', ''}
