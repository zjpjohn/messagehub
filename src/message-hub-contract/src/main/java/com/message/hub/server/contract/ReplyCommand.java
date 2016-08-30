package com.message.hub.server.contract;

/**
 * Created by shi on 7/8/2016.
 */
public class ReplyCommand extends BaseCommandContract {

    public ReplyCommand() {
    }

    public ReplyCommand(String biz, String channel) {
        super.setCmdType(CommandType.reply);
        this.getHubMessage().setBizName(biz);
        this.getHubMessage().setChannel(channel);
    }

    public ReplyCommand(String biz, String channel, Object msg) {
        super.setCmdType(CommandType.reply);
        this.getHubMessage().setBizName(biz);
        this.getHubMessage().setChannel(channel);
        this.getHubMessage().setMessage(msg);
    }

    private HubMessage hubMessage = new HubMessage();

    public HubMessage getHubMessage() {
        return hubMessage;
    }

    public void setHubMessage(HubMessage hubMessage) {
        this.hubMessage = hubMessage;
    }
}
