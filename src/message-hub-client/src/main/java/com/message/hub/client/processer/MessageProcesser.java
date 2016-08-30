package com.message.hub.client.processer;


import com.message.hub.client.network.WanderClient;
import com.message.hub.server.contract.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by shi on 6/12/2016.
 * 推送消息
 */

public class MessageProcesser {
    private static Logger logger = LoggerFactory.getLogger(MessageProcesser.class);
    private static Map<String, SuberModel> subMap = new HashMap<>();

    /**
     * 推送消息
     *
     * @param bizName
     * @param channel
     * @param msg
     */
    public void pub(String bizName, String channel, Object msg) {
        PutCommand cmd = new PutCommand();
        cmd.setBizName(bizName);
        cmd.setChannel(channel);
        cmd.setMessage(msg);
        WanderClient.send(cmd);
    }

    /**
     * 订阅消息
     *
     * @param bizName
     * @param channel
     * @param sub
     */
    public void sub(String bizName, String channel, ISubscribeProcesser sub) {
        if (bizName == null || channel.isEmpty() || sub == null) {
            return;
        }
        //订阅命令
        SubscribeCommand model = new SubscribeCommand();
        model.setBizName(bizName);
        model.setChannel(channel);
        WanderClient.send(model);
        //维护一个订阅列表
//        addSubMap(bizName, channel, sub);

        logger.info("sub" + bizName + "." + channel);
    }

    /**
     * 处理接受到的消息
     *
     * @param cmd
     */
    public void process(ReplyCommand cmd) throws Exception {
        //转换为命令
        if (cmd.getCmdType() == BaseCommandContract.CommandType.error) {
            throw new Exception(cmd.getError());
        } else {
            replyMessage(cmd.getHubMessage());
        }
    }

    /**
     * 处理回复消息
     *
     * @param model
     */
    private void replyMessage(HubMessage model) {
        //尝试获取msgid
        Object msgId = ((com.google.gson.internal.LinkedTreeMap) model.getMessage()).get("msgId");
        if (msgId != null) {
            model.setMsgId(msgId.toString());
        }
        //找到处理对象
        ISubscribeProcesser subscribeProcesser = getSubscribeProcesser(model);
        if (subscribeProcesser != null && model.getMessage() != null) {
            subscribeProcesser.action(model);
        }
    }

    /**
     * 添加订阅队列
     *
     * @param bizName
     * @param channel
     * @param sub
     */
    private static void addSubMap(String bizName, String channel, ISubscribeProcesser sub) {
        if (sub != null) {
            subMap.put(getKey(bizName, channel), new SuberModel(sub, bizName, channel));
        }
    }

    /**
     * 获取消息订阅者
     *
     * @param msg
     * @return
     */
    private static ISubscribeProcesser getSubscribeProcesser(HubMessage msg) {
        SuberModel suberModel = subMap.get(getKey(msg.getBizName(), msg.getChannel()));
        if (suberModel != null)
            return suberModel.subscribeProcesser;
        else
            return null;
    }

    /**
     * 开启拉取消息的job
     */
    public static void startJob() {
        //拉取消息

//        while (true) {
//            try {
//                Set<String> set = subMap.keySet();
//                if (!set.isEmpty()) {
//                    pull(set);
//                }
//                Thread.sleep(100);
//            } catch (Exception e) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e1) {
//
//                }
//                logger.error("job", e);
//            }
//        }
    }

    /**
     * 拉取消息
     *
     * @param set
     */
    private static void pull(Set<String> set) {
        PullCommand cmd = new PullCommand();
        for (String key : set) {
            SuberModel suberModel = subMap.get(key);
            if (suberModel != null) {
                cmd.setBizName(suberModel.bizName);
                cmd.setChannel(suberModel.channel);
                WanderClient.send(cmd);
            }
        }
    }

    /**
     * key转换一下
     *
     * @param bizName
     * @param channel
     * @return
     */
    private static String getKey(String bizName, String channel) {
        return bizName + channel;
    }

    /**
     * 清空并获取订阅列表
     *
     * @return
     */
    public static Map<String, SuberModel> clearAndGet() {
        if (subMap.isEmpty()) {
            return subMap;
        } else {
            Map<String, SuberModel> temp = subMap;
            subMap = new HashMap<>();
            return temp;
        }
    }

    public static class SuberModel {
        public ISubscribeProcesser subscribeProcesser;
        public String bizName;
        public String channel;

        public SuberModel(ISubscribeProcesser subscribeProcesser, String bizName, String channel) {
            this.subscribeProcesser = subscribeProcesser;
            this.bizName = bizName;
            this.channel = channel;
        }
    }
}
