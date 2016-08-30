package com.message.hub.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Created by shi on 6/27/2016.
 */
@Service
@ConfigurationProperties(prefix = "spring.appConfig",locations = "classpath:/application.properties")
public class AppConfig {

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
