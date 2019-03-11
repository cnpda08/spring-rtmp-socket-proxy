package com.sc.socket.rtmp.proxy.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Chu
 * *************************
 * Name：SpringContextUtil
 * Package Name：
 * Project Name：
 * Date: 2019/3/10
 * Time: 22:38
 * Description: No Description
 * *************************
 */
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;


    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }


    /**
     * 获取applicationContext对象
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    /**
     * 根据bean的id来查找对象
     *
     * @param id
     * @return
     */

    public static <T> T getBeanById(String id) {
        return (T) applicationContext.getBean(id);
    }


    /**
     * 根据bean的class来查找对象
     *
     * @param c
     * @return
     */
    public static <T> T getBeanByClass(Class c) {
        return (T) applicationContext.getBean(c);
    }


    /**
     * 根据bean的class来查找所有的对象(包括子类)
     *
     * @param c
     * @return
     */
    public static Map getBeansByClass(Class c) {
        return applicationContext.getBeansOfType(c);
    }

    public static String getMessage(String key) {
        return applicationContext.getMessage(key, null, Locale.getDefault());
    }

}