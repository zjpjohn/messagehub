package com.message.hub.server.contract;

/**
 * Created by shi on 6/29/2016.
 */
public class SubscribeCommand extends BaseCommandContract {
    public SubscribeCommand() {
        super.setCmdType(CommandType.subscribe);
    }

}
