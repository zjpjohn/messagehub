package com.message.hub.server.message;

import com.message.hub.server.channel.ChannelManger;
import com.message.hub.server.config.AppConfig;
import com.message.hub.server.contract.ISubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shi on 6/6/2016.
 */
@Service
public class Subscriber implements ISubscribe {

    @Autowired
    private ChannelManger channelManger;

    @Autowired
    AppConfig appConfig;

    @Override
    public Object sub(String queue) {
        return channelManger.instance().sub(queue);
    }

    @Override
    public int getPort() {
        return appConfig.getPort();
    }

}
