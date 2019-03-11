package com.sc.socket.rtmp.proxy.service;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface RedisService {
    //通过key设置string值的缓存
    public boolean set(String key, String value);
    //通过key获取string值的缓存
    public String get(String key);
    //通过key设置map值的缓存
    public boolean hMSet(String key, Map<String, String> map);
    //通过key获取map值的缓存
    public Map hMGet(String key);
    //通过key设置list值的缓存
    public boolean setList(String key, List<String> list);
    //通过key获取list值的缓存
    public List<String> getList(String key);
    //设置key的缓存的过期时间
    public boolean expire(String key, long expire);
    //list操作左插入
    public long lpush(String key, Object obj);
    //list操作右插入
    public long rpush(String key, Object obj);
    //list操作从左获取值，获取后值被移出list
    public String lpop(String key);
    //list操作从右获取值，获取后值被移出list
    public String rpop(String key);
    //通过key删除缓存
    public void removeByKey(String key);
    //追加hash值
    public boolean hset(String key, String hashKey, String value);
    //删除hash值
    public void hremove(String key, Object... hashKeys);
    /**
     * 获取list的Size
     * By Simon Chu Time 2017-09-07
     */
    public Integer getListSize(String key);
}