local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

local current_tokens = tonumber(redis.call('GET', tokens_key))
local last_refreshed = tonumber(redis.call('GET', timestamp_key))

if current_tokens == nil then
    current_tokens = capacity
    last_refreshed = now
end

local time_passed = math.max(0, now - last_refreshed)
local tokens_to_add = time_passed * refill_rate

current_tokens = math.min(capacity, current_tokens + tokens_to_add)

local allowed = 0
if current_tokens >= requested then
    allowed = 1
    current_tokens = current_tokens - requested
end

local ttl = math.ceil(capacity / refill_rate) * 2
redis.call('SETEX', tokens_key, ttl, current_tokens)
redis.call('SETEX', timestamp_key, ttl, now)

return allowed