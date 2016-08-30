package com.message.hub.client.processer;

import com.message.hub.server.contract.HubMessage;

/**
 * Created by shi on 6/27/2016.
 */
public interface ISubscribeProcesser {
    void action(HubMessage msg);
}
