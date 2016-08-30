package com.message.hub.server.message;

import com.message.hub.server.channel.ChannelManger;
import com.message.hub.server.contract.IPublish;
import com.message.hub.server.contract.HubMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shi on 6/6/2016.
 * //
 */

@Service
public class Publisher implements IPublish {

    @Autowired
    private ChannelManger channelManger;

    @Override
    public void pub(String queue, HubMessage message) {
        channelManger.getChannel().pub(queue, message);
    }
}
