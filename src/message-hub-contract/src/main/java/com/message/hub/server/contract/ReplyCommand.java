package com.message.hub.server.contract;

/**
 * Created by shi on 7/8/2016.
 */
public class ReplyCommand extends BaseCommandContract {

    public ReplyCommand() {
    }

    public ReplyCommand(String biz, String channel) {
        super.setCmdType(CommandType.reply);
        this.getWanderMessage().setBizName(biz);
        this.getWanderMessage().setChannel(channel);
    }

    public ReplyCommand(String biz, String channel, Object msg) {
        super.setCmdType(CommandType.reply);
        this.getWanderMessage().setBizName(biz);
        this.getWanderMessage().setChannel(channel);
        this.getWanderMessage().setMessage(msg);
    }

    private WanderMessage wanderMessage = new WanderMessage();

    public WanderMessage getWanderMessage() {
        return wanderMessage;
    }

    public void setWanderMessage(WanderMessage wanderMessage) {
        this.wanderMessage = wanderMessage;
    }
}
