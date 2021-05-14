package com.beyond233.netty.protocol.message;

public class PingMessage extends Message {
    private static final long serialVersionUID = -922300800205859326L;

    @Override
    public int getMessageType() {
        return PING_MESSAGE;
    }
}
