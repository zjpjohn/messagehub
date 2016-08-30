package com.message.hub.server.contract;

/**
 * Created by shi on 7/8/2016.
 */
public class PutCommand extends BaseCommandContract {
    public PutCommand() {
        super.setCmdType(CommandType.put);
    }

}
