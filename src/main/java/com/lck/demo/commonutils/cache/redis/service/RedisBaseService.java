package com.lck.demo.commonutils.cache.redis.service;

import java.util.List;
import java.util.Map;

/**
 * redis 基础服务
 *
 * @author ckli01
 * @date 2018/6/19
 */
public interface RedisBaseService<K, HK, V> {

//****************************   key - value  ***************************************

    /**
     * 通过key 获取value
     *
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 设置 key-value 缓存
     *
     * @param key
     * @param value
     */
    void set(K key, V value);

    /**
     * 判断键是否存在
     *
     * @return
     */
    boolean isExist();

    /**
     * 判断键是否存在
     *
     * @return
     */
    boolean isExist(K key);

    /**
     * 删除键
     *
     * @return
     */
    void delete();

    /**
     * 设置过期时间 秒
     */
    void expire(Long times);

    /**
     * 设置自增
     *
     * @param key
     * @param value
     * @return
     */
    Long increment(K key, Long value);

    /**
     * 设置监控值 若存在不设置，若不存在将value 设置进去
     *
     * @param key
     * @param value
     * @return
     */
    boolean setNx(K key, V value);

//*************************** hash  key - value  ***************************************

    /**
     * hash 通过key 获取value
     *
     * @param key
     * @return
     */
    V hashGet(HK key);

    /**
     * 设置 hash key-value 缓存
     *
     * @param key
     * @param value
     */
    void hashSet(HK key, V value);

    /**
     * 获取指定hashKey 里面所有 value
     *
     * @return
     */
    List<V> hashMultiGet(List<HK> keys);

    /**
     * 批量设置 hash key-value 缓存
     *
     * @param map
     */
    void hashSet(Map<HK, V> map);

    /**
     * 获取所有 hash key-value 缓存
     */
    List<V> hashValuesGetAll();


}
