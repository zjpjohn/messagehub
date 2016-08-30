package com.message.hub.server.listener;

import com.message.hub.server.channel.ChannelManger;
import com.message.hub.server.connection.ConnectionManger;
import com.message.hub.server.contract.SubscribeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by shi on 8/30/2016.
 */
@Service
public class MessageHubMQListener {

    private final static Logger logger = LoggerFactory.getLogger(MessageHubMQListener.class);
    private ExecutorService executors = Executors.newSingleThreadExecutor();
    /**
     * 运行状态标识，(预留)
     */
    private boolean isRunning = true;
    @Autowired
    ChannelManger channelManger;



    public void unSubscribe() {
        isRunning = false;
        executors.shutdown();
    }

    public void start(String key, SubscribeCommand cmd, ConnectionManger connectionManger) {
        logger.info(key);
        executors.submit(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        /**
                         * 获取消息
                         */
                        Object obj = channelManger.getChannel().sub(key);
                        if (obj != null){
                            boolean r = connectionManger.respond(key, cmd ,obj);
                            if (!r){
                                //消息回退
                                /**
                                 * TODO 考虑消息回退?
                                 * TODO 出于安全性考虑，最好是使用先消费再移除
                                 */
                                //线程退出？
                            }
                        }
                        //休眠
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        logger.error("while", ex);
                    }
                }
            }
        });
    }
}
