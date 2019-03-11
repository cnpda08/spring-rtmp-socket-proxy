package com.sc.socket.rtmp.proxy.service.impl;

import com.sc.socket.rtmp.proxy.service.RedisService;
import com.sc.socket.rtmp.proxy.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @desc resdis service
 */
@Service
@Transactional
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean set(final String key, final String value) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        vo.set(key, value);
        return true;
    }

    public String get(final String key) {
        ValueOperations<String, String> vo = redisTemplate.opsForValue();
        return vo.get(key);
    }

    @Override
    public boolean hMSet(String key, Map<String, String> map) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        hash.putAll(key, map);
        return true;
    }

    @Override
    public Map hMGet(String key) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        return hash.entries(key);
    }

    @Override
    public boolean setList(String key, List<String> list) {
        ListOperations<String, String> operList = redisTemplate.opsForList();
        operList.trim(key, 1, 0);
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                operList.rightPush(key, list.get(i));
            }
        }
        return true;
    }

    @Override
    public List<String> getList(String key) {
        ListOperations<String, String> operList = redisTemplate.opsForList();
        return operList.range(key, 0, -1);
    }

    @Override
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    @Override
    public long lpush(final String key, Object obj) {
        final String value = JsonUtils.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.lPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    @Override
    public long rpush(final String key, Object obj) {
        final String value = JsonUtils.toJson(obj);
        long result = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                long count = connection.rPush(serializer.serialize(key), serializer.serialize(value));
                return count;
            }
        });
        return result;
    }

    @Override
    public String lpop(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res = connection.lPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    @Override
    public String rpop(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] res = connection.rPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
        return result;
    }

    @Override
    public void removeByKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean hset(String key, String hashKey, String value) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
        return true;
    }

    @Override
    public void hremove(String key, Object... hashKeys) {
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        hash.delete(key, hashKeys);
    }

    @Override
    public Integer getListSize(String key) {
        Integer result = redisTemplate.execute((RedisCallback<Integer>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            Long res = connection.lLen(serializer.serialize(key));
            return res.intValue();
        });
        return result;
    }
}