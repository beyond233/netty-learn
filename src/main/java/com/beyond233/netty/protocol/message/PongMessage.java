package com.beyond233.netty.protocol.message;

public class PongMessage extends Message {
    private static final long serialVersionUID = -1525931788525163185L;

    @Override
    public int getMessageType() {
        return PONG_MESSAGE;
    }
}
