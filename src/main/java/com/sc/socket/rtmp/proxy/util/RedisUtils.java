package com.sc.socket.rtmp.proxy.util;

import com.sc.socket.rtmp.proxy.service.RedisService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * RedisUtils
 */
@Component
public class RedisUtils {

    public static final Logger log = Logger.getLogger(RedisUtils.class);

    @Autowired
    private RedisService redisService;

    /**
     * 返回包含要翻译的字段值的T
     *
     * @param t
     * @param listKeys
     * @param sourceColumns
     * @param distColumns
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getFullValue(T t, String[] listKeys, String[] sourceColumns, String[] distColumns) throws Exception {
        Class clazz = t.getClass();
        if (t == null || listKeys == null || sourceColumns == null || distColumns == null) {
            return t;
        } else if (listKeys.length != distColumns.length || sourceColumns.length != distColumns.length) {
            throw new Exception("listKeys或者sourceColumns或者distColumns参数错误!");
        } else {
            for (int i = 0; i < sourceColumns.length; i++) {
                try {
                    String getMethod = "get" + sourceColumns[i].substring(0, 1).toUpperCase() + sourceColumns[i].substring(1);
                    Method getM = clazz.getDeclaredMethod(getMethod);

                    Object key = getM.invoke(t);
                    if (key != null) {
                        String keyValue = String.valueOf(key);
                        StringBuilder redisKey = new StringBuilder();
                        redisKey.append(listKeys[i]);
                        redisKey.append(".");
                        redisKey.append(keyValue);
                        String value = redisService.get(redisKey.toString());

                        String setMethod = "set" + distColumns[i].substring(0, 1).toUpperCase() + distColumns[i].substring(1);
                        Method setM = clazz.getDeclaredMethod(setMethod, String.class);
                        setM.invoke(t, value);
                    }
                } catch (Exception e) {
                    log.info(e.getMessage());
                    throw e;
                }
            }
            return t;
        }
    }

    /**
     * 返回包含要翻译的字段值的List<T>
     *
     * @param list
     * @param listKeys
     * @param sourceColumns
     * @param distColumns
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getFullValueList(List<T> list, String[] listKeys, String[] sourceColumns, String[] distColumns) throws Exception {
        if (list == null || list.isEmpty()) {
            return list;
        } else if (listKeys == null || sourceColumns == null || distColumns == null) {
            return list;
        } else if (listKeys.length != distColumns.length || sourceColumns.length != distColumns.length) {
            throw new Exception("listKeys或者sourceColumns或者distColumns参数错误!");
        } else {
            Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
                Class clazz = t.getClass();
                for (int j = 0; j < sourceColumns.length; j++) {
                    try {
                        Map<String, String> valueMap = null;
                        String mapKey = listKeys[j];
                        if (map.containsKey(mapKey)) {
                            valueMap = map.get(mapKey);
                        } else {
                            valueMap = redisService.hMGet(mapKey);
                            map.put(mapKey, valueMap);
                        }

                        String getMethod = "get" + sourceColumns[j].substring(0, 1).toUpperCase() + sourceColumns[j].substring(1);
                        Method getM = clazz.getDeclaredMethod(getMethod);
                        Object key = getM.invoke(t);
                        if (key != null) {
                            String keyValue = String.valueOf(key);
                            String value = valueMap.get(keyValue);

                            String setMethod = "set" + distColumns[j].substring(0, 1).toUpperCase() + distColumns[j].substring(1);
                            Method setM = clazz.getDeclaredMethod(setMethod, String.class);
                            setM.invoke(t, value);
                        }

                    } catch (Exception e) {
                        log.info(e.getMessage());
                        throw e;
                    }
                }
            }
            return list;
        }
    }

    /**
     * 返回包含要翻译的字段值的Page<T>
     *
     * @param page
     * @param listKeys
     * @param sourceColumns
     * @param distColumns
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Page<T> getFullValuePage(Page<T> page, String[] listKeys, String[] sourceColumns, String[] distColumns) throws Exception {
        if (page == null || page.getTotalElements() == 0) {
            return page;
        } else if (listKeys == null || sourceColumns == null || distColumns == null) {
            return page;
        } else if (listKeys.length != distColumns.length || sourceColumns.length != distColumns.length) {
            throw new Exception("listKeys或者sourceColumns或者distColumns参数错误!");
        } else {
            Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
            Iterator<T> iter = page.iterator();
            while (iter.hasNext()) {
                T t = iter.next();
                Class clazz = t.getClass();
                for (int j = 0; j < sourceColumns.length; j++) {
                    try {
                        Map<String, String> valueMap = null;
                        String mapKey = listKeys[j];
                        if (map.containsKey(mapKey)) {
                            valueMap = map.get(mapKey);
                        } else {
                            valueMap = redisService.hMGet(mapKey);
                            map.put(mapKey, valueMap);
                        }

                        String getMethod = "get" + sourceColumns[j].substring(0, 1).toUpperCase() + sourceColumns[j].substring(1);
                        Method getM = clazz.getDeclaredMethod(getMethod);
                        Object key = getM.invoke(t);
                        if (key != null) {
                            String keyValue = String.valueOf(key);
                            String value = valueMap.get(keyValue);

                            String setMethod = "set" + distColumns[j].substring(0, 1).toUpperCase() + distColumns[j].substring(1);
                            Method setM = clazz.getDeclaredMethod(setMethod, String.class);
                            setM.invoke(t, value);
                        }

                    } catch (Exception e) {
                        log.info(e.getMessage());
                        throw e;
                    }
                }
            }
            return page;
        }
    }

    /**
     * 返回包含要翻译的userName字段值的T
     *
     * @param t
     * @param sourceColumns
     * @param distColumns
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T getUserName(T t, String[] sourceColumns, String[] distColumns) throws Exception {
        Class clazz = t.getClass();
        if (t == null || sourceColumns == null || distColumns == null) {
            return t;
        } else if (sourceColumns.length != distColumns.length) {
            throw new Exception("sourceColumns或者distColumns参数错误!");
        } else {
            for (int i = 0; i < sourceColumns.length; i++) {
                try {
                    String getMethod = "get" + sourceColumns[i].substring(0, 1).toUpperCase() + sourceColumns[i].substring(1);
                    Method getM = clazz.getDeclaredMethod(getMethod);
                    Object keyValue = getM.invoke(t);
                    if (keyValue != null) {
                        String redisKey = "user_" + keyValue;
                        String value = redisService.get(redisKey);
                        String setMethod = "set" + distColumns[i].substring(0, 1).toUpperCase() + distColumns[i].substring(1);
                        Method setM = clazz.getDeclaredMethod(setMethod, String.class);
                        setM.invoke(t, value);
                    }
                } catch (Exception e) {
                    log.info(e.getMessage());
                    throw e;
                }
            }
            return t;
        }
    }

    /**
     * 返回包含要翻译的userName字段值的List<T>
     *
     * @param list
     * @param sourceColumns
     * @param distColumns
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> getUserNameList(List<T> list, String[] sourceColumns, String[] distColumns) throws Exception {
        if (list == null || list.isEmpty()) {
            return list;
        } else if (sourceColumns == null || distColumns == null) {
            return list;
        } else if (sourceColumns.length != distColumns.length) {
            throw new Exception("sourceColumns或者distColumns参数错误!");
        } else {
            Map<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < list.size(); i++) {
                T t = list.get(i);
                Class clazz = t.getClass();
                for (int j = 0; j < sourceColumns.length; j++) {
                    try {
                        String getMethod = "get" + sourceColumns[j].substring(0, 1).toUpperCase() + sourceColumns[j].substring(1);
                        Method getM = clazz.getDeclaredMethod(getMethod);
                        Object keyValue = getM.invoke(t);
                        if (keyValue != null) {
                            String redisKey = "user_" + keyValue;
                            String value = null;
                            if (map.containsKey(redisKey)) {
                                value = map.get(redisKey);
                            } else {
                                value = redisService.get(redisKey);
                                map.put(redisKey, value);
                            }
                            String setMethod = "set" + distColumns[j].substring(0, 1).toUpperCase() + distColumns[j].substring(1);
                            Method setM = clazz.getDeclaredMethod(setMethod, String.class);
                            setM.invoke(t, value);
                        }
                    } catch (Exception e) {
                        log.info(e.getMessage());
                        throw e;
                    }
                }
            }
            return list;
        }
    }

    /**
     * 返回包含要翻译的userName字段值的Page<T>
     *
     * @param page
     * @param sourceColumns
     * @param distColumns
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> Page<T> getUserNamePage(Page<T> page, String[] sourceColumns, String[] distColumns) throws Exception {
        if (page == null || page.getTotalElements() == 0) {
            return page;
        } else if (sourceColumns == null || distColumns == null) {
            return page;
        } else if (sourceColumns.length != distColumns.length) {
            throw new Exception("sourceColumns或者distColumns参数错误!");
        } else {
            Map<String, String> map = new HashMap<String, String>();
            Iterator<T> iter = page.iterator();
            while (iter.hasNext()) {
                T t = iter.next();
                Class clazz = t.getClass();
                for (int j = 0; j < sourceColumns.length; j++) {
                    try {
                        String getMethod = "get" + sourceColumns[j].substring(0, 1).toUpperCase() + sourceColumns[j].substring(1);
                        Method getM = clazz.getDeclaredMethod(getMethod);
                        Object keyValue = getM.invoke(t);
                        if (keyValue != null) {
                            String redisKey = "user_" + keyValue;
                            String value = null;
                            if (map.containsKey(redisKey)) {
                                value = map.get(redisKey);
                            } else {
                                value = redisService.get(redisKey);
                                map.put(redisKey, value);
                            }
                            String setMethod = "set" + distColumns[j].substring(0, 1).toUpperCase() + distColumns[j].substring(1);
                            Method setM = clazz.getDeclaredMethod(setMethod, String.class);
                            setM.invoke(t, value);
                        }
                    } catch (Exception e) {
                        log.info(e.getMessage());
                        throw e;
                    }
                }
            }
            return page;
        }
    }
}