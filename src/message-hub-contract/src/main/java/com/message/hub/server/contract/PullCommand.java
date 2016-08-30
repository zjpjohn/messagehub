package com.message.hub.server.contract;

/**
 * 拉取消息模型
 * Created by shi on 7/8/2016.
 */
public class PullCommand extends BaseCommandContract {
    public PullCommand() {
        super.setCmdType(CommandType.pull);
    }

}
