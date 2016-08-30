package com.message.hub.server.contract;

/**
 * Created by shi on 6/7/2016.
 */
public interface IPublish {

    void pub(String queue, HubMessage message);
}
