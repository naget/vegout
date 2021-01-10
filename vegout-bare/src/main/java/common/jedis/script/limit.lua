--
-- Created by IntelliJ IDEA.
-- User: tianfeng
-- Date: 2021/1/9
-- Time: 10:03 下午
-- To change this template use File | Settings | File Templates.
--

--KEYS[1]:该次限流对应的key
--ARGV[1]:一分钟之前的时间戳
--ARGV[2]:此时此刻的时间戳
--ARGV[3]:允许通过的最大数量
--ARGV[4]:member名称（随机生成）
redis.call('zremrangeByScore', KEYS[1], 0, ARGV[1])
local res = redis.call('zrangeByScore', KEYS[1], ARGV[1], ARGV[2])
if (res == nil) or (table.getn(res) < tonumber(ARGV[3])) then
    redis.call('zadd', KEYS[1], ARGV[2], ARGV[4])
    return 0
else return 1 end




redis.call('zremrangeByScore', KEYS[1], 0, ARGV[1])
local res = redis.call('zcard', KEYS[1])
if (res == nil) or (res < tonumber(ARGV[3])) then
    redis.call('zadd', KEYS[1], ARGV[2], ARGV[4])
    return 0
else return 1 end

