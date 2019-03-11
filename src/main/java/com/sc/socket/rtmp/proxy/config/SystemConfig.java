package com.sc.socket.rtmp.proxy.config;

import com.sc.socket.rtmp.proxy.util.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfig {

    @Bean("springContextUtil")
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

}
