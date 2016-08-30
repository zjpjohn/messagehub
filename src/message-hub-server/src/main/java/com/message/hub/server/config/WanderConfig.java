package com.message.hub.server.config;


import org.springframework.beans.factory.annotation.Value;

/**
 * Created by shi on 6/6/2016.
 */
public class WanderConfig {

    @Value("#{remoteSettings['redisHost']}")
    private String redisHost ;
    @Value("#{remoteSettings['redisPort']}")
    private int redisPort ;

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }
}
