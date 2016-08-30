package com.message.hub.server.command;

import com.message.hub.common.ClassConvertHelper;
import com.message.hub.common.JsonConverter;
import com.message.hub.server.channel.ChannelManger;
import com.message.hub.server.connection.ConnectionManger;
import com.message.hub.server.contract.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shi on 6/27/2016.
 */
@Service
public class CommandProcesser {
    private static Logger logger = LoggerFactory.getLogger(CommandProcesser.class);

    private static List<String> subQueue = new ArrayList<>();

    @Autowired
    ChannelManger channelManger;
    @Autowired
    ConnectionManger connectionManger;

    /**
     * 处理接受到的消息
     *
     * @param cmd
     * @param channel
     * @return
     */
    public ReplyCommand process(BaseCommandContract cmd, Channel channel) {
        ReplyCommand result = null;
        switch (cmd.getCmdType()) {
            case subscribe:
                subscribe(ClassConvertHelper.convert(cmd, SubscribeCommand.class), channel);
                break;
            case unsubscribe:
                unSubscribe();
                break;
//            case pull:
//                result = getMessage(ClassConvertHelper.convert(cmd, PullCommand.class));
//                break;
            case put:
                pubMessage(ClassConvertHelper.convert(cmd, PutCommand.class));
                break;
        }
        return result;
    }

    /**
     * 添加链接
     *
     * @param queueName
     * @param cmd
     * @param channel */
    private void addConnetcion(String queueName, SubscribeCommand cmd, Channel channel) {
        connectionManger.register(queueName,cmd, channel);
    }

    /**
     * 推送消息
     *
     * @param cmd
     */
    private void pubMessage(PutCommand cmd) {
        String queueName = getQueueName(cmd);
        channelManger.getChannel().pub(queueName, cmd.getMessage());
        logger.info(String.format("push message queue:%s ,content:%s", queueName, JsonConverter.toJson(cmd.getMessage())));
    }



    /**
     * 取消订阅
     */
    private void unSubscribe() {
//        subQueue.remove(queueName);
    }

    /**
     * 订阅
     */
    private void subscribe(SubscribeCommand cmd, Channel channel) {
        subQueue.add(getQueueName(cmd));
        addConnetcion(getQueueName(cmd),cmd, channel);
    }

    /**
     * 获取队列名
     *
     * @return
     */
    private String getQueueName(BaseCommandContract cmd) {
        if (cmd == null || cmd.getBizName() == null || cmd.getChannel() == null) {
            return null;
        }
        return String.format("hub-%s-%s", cmd.getBizName(), cmd.getChannel());
    }

    /**
     * 一个channel 因为异常终止
     * @param channel
     */
    public void close(Channel channel) {
        connectionManger.remove(channel);
    }
}
