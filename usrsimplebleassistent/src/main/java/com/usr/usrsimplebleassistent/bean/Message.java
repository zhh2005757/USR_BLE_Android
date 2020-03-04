package com.usr.usrsimplebleassistent.bean;

/**
 * Created by Administrator on 2015-07-28.
 */
public class Message {
    private String content;
    private MESSAGE_TYPE type;
    //option success (e.g:write option success)
    private boolean done;

    public Message(){};

    public Message(MESSAGE_TYPE type, String content) {
        this.type = type;
        this.content = content;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public MESSAGE_TYPE getType() {
        return type;
    }

    public void setType(MESSAGE_TYPE type) {
        this.type = type;
    }


    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public static enum MESSAGE_TYPE{
        SEND,RECEIVE;
    }
}
