package com.message.hub.client.processer;

import com.message.hub.common.JsonConverter;
import com.message.hub.server.contract.ReplyCommand;

/**
 * Created by shi on 6/29/2016.
 */
public class CommandProcesser {

    private static MessageProcesser messageProcesser = null;
    private static CommandProcesser commandProcesser = new CommandProcesser();
    private CommandProcesser(){}

    /**
     * 获取对象
     * @return
     */
    public static CommandProcesser instance() {
        return commandProcesser;
    }

    /**
     * 注入处理对象
     * @param processer
     */
    public static void injection(MessageProcesser processer){
        messageProcesser = processer;
    }


    /**
     * 处理获取到的消息
     * @param s
     */
    public void process(String s) throws Exception {
        ReplyCommand replyCommand = JsonConverter.fromJson(s,ReplyCommand.class);
        messageProcesser.process(replyCommand);
    }
}
