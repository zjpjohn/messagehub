package com.message.hub.server.channel;

/**
 * Created by shi on 6/6/2016.
 * 所有通道的基类
 */
public interface IBaseChannel {

    /**
     * 发送消息
     * @param queue 通道名
     * @param message 消息内容
     */
     void pub(String queue, Object message) ;

    /**
     * 订阅消息队列
     * @param queue 通道名
     */
    Object sub(String queue) ;



}
