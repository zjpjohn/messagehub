package com.message.hub.server.contract;

/**
 * Created by shi on 8/24/2016.
 *  command exec base
 */
public class BaseCommand {
    /**
     * 命令类型，主要区分操作类型作用的
     */
    private CommandType cmdType;
    private String error;
    private String bizName;
    private String exchange;
    private Object message;



    public enum CommandType {
        subscribe,
        unsubscribe,
        pull, put,
        reply,
        error,
        status
    }
}
