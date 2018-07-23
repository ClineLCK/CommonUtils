package com.lck.demo.commonutils.cache.redis.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lck.demo.commonutils.cache.redis.service.RedisBaseService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis 基础服务实现类
 *
 * @author ckli01
 * @date 2018/6/19
 */
@Component
@Getter
@SuppressWarnings("unchecked")
@Slf4j
public class RedisBaseServiceImpl<K, HK, V> implements RedisBaseService<K, HK, V> {


    /**
     * 主键key
     */
    protected K key;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public V get(K key) {
        if (key != null) {
            log.debug("redis get value key : {}", key);
            return (V) redisTemplate.opsForValue().get(key);
        } else {
            log.error("redis get value key can't be null");
            return null;
        }
    }

    @Override
    public void set(K key, V value) {
        if (key != null) {
            log.debug("redis set for key:{}   |  value:{} ", key, value);
            redisTemplate.opsForValue().set(key, value);
        } else {
            log.error("redis set key can't be null");
        }
    }

    @Override
    public boolean isExist() {
        if (getKey() == null) {
            log.error("redis isExist key can't be null");
            return false;
        }
        return redisTemplate.hasKey(getKey());
    }

    @Override
    public boolean isExist(K key) {
        if (key == null) {
            log.error("redis isExist key can't be null");
            return false;
        }
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete() {
        if (getKey() == null) {
            log.error("redis delete key can't be null");
        } else {
            redisTemplate.delete(getKey());
        }
    }

    @Override
    public void expire(Long times) {
        if (getKey() == null) {
            log.error("redis expire key can't be null");
        } else {
            log.debug("redis expire key:{} times:{}", getKey(), times);
            redisTemplate.expire(getKey(), times, TimeUnit.SECONDS);
        }
    }

    @Override
    public Long increment(K key, Long value) {
        if (key == null) {
            log.error("redis increment key can't be null");
            return null;
        } else {
            log.debug("redis increment key:{} for value:{} ", key, value);
            return redisTemplate.opsForValue().increment(key, value);
        }
    }

    @Override
    public boolean setNx(K key, V value) {
        if (key != null && value != null) {
            log.debug("redis setNx key:{} for value:{} ", key, value);

            byte[] keyByte = redisTemplate.getStringSerializer().serialize(key);
            byte[] valueByte = redisTemplate.getValueSerializer().serialize(value);
            return redisTemplate.getConnectionFactory().getConnection().setNX(keyByte, valueByte);
        } else {
            log.error("there are some problems to redis setNx key:{} for value:{} ", key, value);
        }
        return false;
    }

    @Override
    public V hashGet(HK key) {
        if (key == null || getKey() == null) {
            log.error("redis hashGet for key:{} hkey:{} can't be null", getKey(), key);
            return null;
        }
        log.debug("redis hashGet for key:{} hkey:{} ", getKey(), key);
        return (V) redisTemplate.opsForHash().get(getKey(), key);
    }

    @Override
    public void hashSet(HK key, V value) {
        if (getKey() == null || key == null || value == null) {
            log.error("redis hashSet for key:{} hkey:{}  value:{} can't be null", getKey(), key, value);
        } else {
            log.debug("redis hashSet for key:{} hkey:{}  value:{} ", getKey(), key, value);
            redisTemplate.opsForHash().put(getKey(), key, value);
        }
    }


    @Override
    public List<V> hashMultiGet(List<HK> keys) {
        if (getKey() == null || CollectionUtils.isEmpty(keys)) {
            log.error("redis hashMultiGet keys or Key can't be null  ");
            return new ArrayList<>();
        }
        log.debug("redis hashMultiGet for Key:{} keys:{}", getKey(), JSONObject.toJSONString(keys));
        return (List<V>) redisTemplate.opsForHash().multiGet(getKey(), keys);
    }

    @Override
    public void hashSet(Map<HK, V> map) {
        if (getKey() == null || CollectionUtils.isEmpty(map)) {
            log.error("redis hashSet map or key cant be null or empty ");
        } else {
            log.debug("redis hashSet  for key:{}  map:{} ", getKey(), JSONObject.toJSONString(map));
            redisTemplate.opsForHash().putAll(getKey(), map);
        }
    }

    @Override
    public List<V> hashValuesGetAll() {
        if (getKey() == null) {
            log.error("redis hashValuesGetAll key can't be null");
            return null;
        } else {
            log.debug("redis hashValuesGetAll for key:{} ", getKey());
            return (List<V>) redisTemplate.opsForHash().values(getKey());
        }
    }


}
