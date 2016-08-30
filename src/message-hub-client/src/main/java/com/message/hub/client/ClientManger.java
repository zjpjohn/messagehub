package com.message.hub.client;

import com.message.hub.client.network.WanderClient;
import com.message.hub.client.processer.CommandProcesser;
import com.message.hub.client.processer.ISubscribeProcesser;
import com.message.hub.client.processer.MessageProcesser;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shi on 6/8/2016.
 */

public class ClientManger {

    private static ClientManger clientManger = new ClientManger();

    private ClientManger() {
    }

    MessageProcesser messageProcesser = new MessageProcesser();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String host = "";
    private int port = 10991;

    public static ClientManger instance() {
        return clientManger;
    }

    public void init(String serverHost) {
        init(serverHost, 10);
    }

    /**
     * 初始化
     *
     * @param serverHost
     * @param workPoolNum
     */
    public void init(String serverHost, int workPoolNum) {
        //解析服务地址
        parseServerHost(serverHost);
        //注入消息处理对象
        CommandProcesser.injection(messageProcesser);
        //启动客户端链接
        WanderClient.start(host, port, workPoolNum);
        //开启任务
        executorService.submit(() -> MessageProcesser.startJob());
    }

    /**
     * 推送消息
     *
     * @param bizName
     * @param channel
     * @param msg
     */
    public void pub(String bizName, String channel, Object msg) {
        messageProcesser.pub(bizName, channel, msg);
    }

    /**
     * 订阅消息队列
     *
     * @param bizName
     * @param channel
     * @param subscribeProcesser
     */
    public void sub(String bizName, String channel, ISubscribeProcesser subscribeProcesser) {
        messageProcesser.sub(bizName, channel, subscribeProcesser);
    }

    /**
     * 重新订阅所有队列，重连后使用
     */
    public void reSub() {
        //清空并获取订阅列表
        Map<String, MessageProcesser.SuberModel> temp = MessageProcesser.clearAndGet();
        if (temp.isEmpty()) {
            return;
        } else {
            //重新订阅
            Set<String> keys = temp.keySet();
            for (String key : keys) {
                MessageProcesser.SuberModel model = temp.get(key);
                if (model != null) {
                    sub(model.bizName, model.channel, model.subscribeProcesser);
                }
            }
        }
    }

    /**
     * 转换服务地址
     *
     * @param serverHost
     */
    private void parseServerHost(String serverHost) {
        if (serverHost == null || serverHost.isEmpty()) {
            throw new InvalidParameterException("server host invalid error");
        }
        String[] sArr = serverHost.split(":");
        if (sArr == null || sArr.length != 2) {
            throw new InvalidParameterException("server host invalid error");
        } else {
            host = sArr[0];
            port = Integer.valueOf(sArr[1]);
            if (port <= 0) {
                throw new InvalidParameterException("server host invalid error");
            }
        }
    }
}
