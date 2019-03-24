package com.sc.socket.rtmp.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Chu
 * *************************
 * Name：proxyRemoteConfig
 * Package Name：com.sc.socket.rtmp.proxy.config
 * Project Name：rtmp-proxy-server
 * Date: 2019/3/11
 * Time: 19:45
 * Description: No Description
 * *************************
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "rtmp.server")
@PropertySource(value = {"proxyConfig.properties"}, ignoreResourceNotFound = true, encoding = "utf-8")
public class ProxyServerConfig {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
