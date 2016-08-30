package com.message.hub.server.connection;

import com.message.hub.common.JsonConverter;
import com.message.hub.server.contract.ReplyCommand;
import com.message.hub.server.contract.SubscribeCommand;
import com.message.hub.server.listener.ListenerManger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shi on 8/30/2016.
 */
@Service
public class ConnectionManger {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManger.class);
    private static Map<String, List<Channel>> cacheContext = new HashMap<String, List<Channel>>();
    private static Map<ChannelId, String> channelIdMap = new HashMap<ChannelId, String>();

    @Autowired
    ListenerManger listenerManger;

    /**
     * 注册channel
     *
     * @param key
     * @param cmd
     * @param ctx
     */
    public void register(String key, SubscribeCommand cmd, Channel ctx) {
        put(key, ctx);
        /**
         * 监听mq
         */
        listenerManger.beginListen(key, cmd, this);
    }

    /**
     * 添加一个channel
     *
     * @param key
     * @param channel
     */
    private synchronized void put(String key, Channel channel) {
        List<Channel> channels = null;
        if (cacheContext.containsKey(key)) {
            channels = cacheContext.get(key);
        }

        if (channels == null) {
            channels = new ArrayList<Channel>();
        }
        if (!hasConnection(channel, channels)) {
            channels.add(channel);
            channelIdMap.put(channel.id(), key);
            cacheContext.put(key, channels);
        }
    }

    /**
     * 是否存在该channel
     *
     * @param channel
     * @param channels
     * @return
     */
    private boolean hasConnection(Channel channel, List<Channel> channels) {
        for (Channel cnl : channels) {
            if (cnl.id() == channel.id()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 回复消息
     *  @param key
     * @param cmd
     * @param msg
     */
    public boolean respond(String key, SubscribeCommand cmd, Object msg) {

        //获取链接
        List<Channel> channels = get(key);
        if (channels == null || channels.isEmpty()) {
            return false;
        } else {
            try {
                ReplyCommand replyCommand = getReply(cmd, msg);
                String s = JsonConverter.toJson(replyCommand);
                channels.get(0).writeAndFlush(s);
            }catch (Exception ex){
                return false;
            }
            return true;
        }
    }

    /**
     * 获取返回消息
     *
     * @param cmd
     * @param msg
     * @return
     */
    private ReplyCommand getReply(SubscribeCommand cmd, Object msg) {
        ReplyCommand reply = new ReplyCommand();
        if (cmd != null && msg != null) {
            reply.setBizName(cmd.getBizName());
            reply.setChannel(cmd.getChannel());
            reply.getHubMessage().setMessage(msg);
        }
        return reply;
    }


    public void remove(Channel channel) {
        if (channelIdMap.containsKey(channel.id())) {
            String key = channelIdMap.get(channel.id());
            remove(key, channel);
        }
    }

    private void remove(String key, Channel channel) {
        if (cacheContext.containsKey(key)) {
            List<Channel> channels = cacheContext.get(key);
            if (channels != null) {
                channels.remove(channel);
                channel = null;
            }
            /**
             * 如果该key下没有channel监听，则停止监听mq
             */
            if (channels == null || channels.size() == 0) {
                listenerManger.stopListen(key);
            }
        }

    }

    public List<Channel> get(String key) {
        return cacheContext.get(key);
    }

    public String getKey(ChannelId id) {
        return channelIdMap.get(id);
    }
}
