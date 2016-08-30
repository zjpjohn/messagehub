package com.message.hub.server.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shi on 6/7/2016.
 */
@Service
public class ChannelManger {
    @Autowired
    RedisChannel redisChannel;

    public  IBaseChannel instance(){
        return redisChannel;
    }
}
