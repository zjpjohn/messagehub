package com.message.hub.server.command;

import com.message.hub.server.channel.IBaseChannel;
import com.message.hub.common.ClassConvertHelper;
import com.message.hub.common.JsonConverter;
import com.message.hub.server.contract.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shi on 6/27/2016.
 */
public class CommandProcesser {
    private static Logger logger = LoggerFactory.getLogger(CommandProcesser.class);
    private static IBaseChannel baseChannel;
    private static List<String> subQueue = new ArrayList<>();

    public static CommandProcesser instance() {
        return new CommandProcesser();
    }

    /**
     * 注入处理对象
     *
     * @param channel
     */
    public static void injection(IBaseChannel channel) {
        baseChannel = channel;
    }

    /**
     * 处理接受到的消息
     *
     * @param cmd
     * @return
     */
    public ReplyCommand process(BaseCommandContract cmd) {
        ReplyCommand result = null;
        switch (cmd.getCmdType()) {
            case subscribe:
                subscribe(ClassConvertHelper.convert(cmd, SubscribeCommand.class));
                break;
            case unsubscribe:
                unSubscribe();
                break;
            case pull:
                result = getMessage(ClassConvertHelper.convert(cmd, PullCommand.class));
                break;
            case put:
                pubMessage(ClassConvertHelper.convert(cmd, PutCommand.class));
                break;
        }
        return result;
    }

    /**
     * 推送消息
     *
     * @param cmd
     */
    private void pubMessage(PutCommand cmd) {
        String queueName = getQueueName(cmd);
        baseChannel.pub(queueName, cmd.getMessage());
        logger.info(String.format("push message queue:%s ,content:%s", queueName, JsonConverter.toJson(cmd.getMessage())));
    }

    /**
     * 获取消息
     *
     * @param cmd
     * @return
     */
    private ReplyCommand getMessage(PullCommand cmd) {
        String queueName = getQueueName(cmd);
        ReplyCommand result = new ReplyCommand(cmd.getBizName(), cmd.getChannel());
        //判断是否在订阅队列里面
        if (subQueue.contains(queueName)) {
            Object s = baseChannel.sub(queueName);
            if (s == null) {
                return null;
            } else
                result.getWanderMessage().setMessage(s);
        } else {
            result.setError("not subscribe this queue");
            result.setCmdType(BaseCommandContract.CommandType.error);
        }
        logger.info(String.format("pull message:%s . result: %s", queueName, JsonConverter.toJson(result.getWanderMessage())));
        return result;
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
    private void subscribe(SubscribeCommand cmd) {
        subQueue.add(getQueueName(cmd));
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
}
