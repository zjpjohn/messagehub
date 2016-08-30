package com.message.hub.server.listener;

import com.message.hub.server.connection.ConnectionManger;
import com.message.hub.server.contract.SubscribeCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shi on 8/17/2016.
 */
@Service
public class ListenerManger {
    static Map<String, MessageHubMQListener> listenerMap = new HashMap<String, MessageHubMQListener>();

    @Autowired
    MessageHubMQListener messageHubMQListener;


    public synchronized static void stopListen(String key) {
        if (listenerMap.containsKey(key)) {
            MessageHubMQListener messageHubMQListener = listenerMap.get(key);
            if (messageHubMQListener != null) {
                messageHubMQListener.unSubscribe();
                messageHubMQListener = null;
            }
            listenerMap.remove(key);
        }
    }

    public synchronized  void beginListen(String key, SubscribeCommand cmd, ConnectionManger connectionManger) {
        if (listenerMap.containsKey(key) && listenerMap.get(key) != null) {
            /**
             * 已经有监听对象，直接return
             */
            return;
        } else {
            /**
             * TODO 添加监听对象
             */

            MessageHubMQListener listener = listen(key,cmd,connectionManger);
            listenerMap.put(key, listener);
        }
    }

    private  MessageHubMQListener listen(String key, SubscribeCommand cmd, ConnectionManger connectionManger) {
        messageHubMQListener.start(key,cmd,connectionManger);
        return messageHubMQListener;
    }

}
