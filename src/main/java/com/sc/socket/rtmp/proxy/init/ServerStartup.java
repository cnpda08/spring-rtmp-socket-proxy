package com.sc.socket.rtmp.proxy.init;

import com.sc.socket.rtmp.proxy.config.ProxyLocalConfig;
import com.sc.socket.rtmp.proxy.config.ProxyRemoteConfig;
import com.sc.socket.rtmp.proxy.config.ProxyServerConfig;
import com.sc.socket.rtmp.proxy.server.RtmpSocketServer;
import com.sc.socket.rtmp.proxy.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Simon Chu
 * *************************
 * Name：SystemStartup
 * Package Name：
 * Project Name：
 * Date: 2019/3/9
 * Time: 0:39
 * Description: No Description
 * *************************
 */
@Component
public class ServerStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger log = LoggerFactory.getLogger(ServerStartup.class);

    @Autowired
    private RtmpSocketServer rtmpSocketServer;

    @Autowired
    private ProxyLocalConfig proxyLocalConfig;

    @Autowired
    private ProxyServerConfig proxyServerConfig;

    @Autowired
    private ProxyRemoteConfig proxyRemoteConfig;

    @Autowired
    private RedisService redisService;

    /**
     * Spring Done Start Socket Server
     *
     * @param applicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        log.info("**********************************************************************");
        log.info("Start Server Node: " + proxyServerConfig.getName());
        log.info("Clear Rtmp Socket Server Thread Count");
        log.info("**********************************************************************");
        redisService.set("rtmp:server:" + proxyServerConfig.getName(), "0");
        try {
            rtmpSocketServer.startServer(proxyLocalConfig.getPort(), proxyRemoteConfig.getPort(), proxyRemoteConfig.getAddress());
        } catch (IOException e) {
            log.error("Error Start Rtmp Socket Server", e);
        }
    }
}
