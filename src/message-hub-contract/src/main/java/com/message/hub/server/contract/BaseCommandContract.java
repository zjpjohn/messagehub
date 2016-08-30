package com.message.hub.server.contract;

/**
 * Created by shi on 7/8/2016.
 */
public  class BaseCommandContract {
    private CommandType cmdType;
    private String error;
    private String bizName;
    private String channel;
    private Object message;

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
    public CommandType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CommandType cmdType) {
        this.cmdType = cmdType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public enum CommandType {
        subscribe,
        unsubscribe,
        pull, put,
        reply,
        error,
        status
    }
}
