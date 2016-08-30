package com.message.hub.server.contract;

/**
 * Created by shi on 6/7/2016.
 */
public interface ISubscribe {

    Object  sub(String queue);

    int getPort();
}
