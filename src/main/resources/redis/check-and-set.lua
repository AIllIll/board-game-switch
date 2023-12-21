local current = redis.call('GET', KEYS[1])
if current == ARGV[1] or (ARGV[3] and not current) -- 如果是空也可以set
then
    redis.call('SET', KEYS[1], ARGV[2])
    return true
end
return false