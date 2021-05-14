package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatRequestMessage extends Message {
    private static final long serialVersionUID = -8307129004653405274L;
    private String content;
    private String to;
    private String from;

    public ChatRequestMessage() {
    }

    public ChatRequestMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return CHAT_REQUEST_MESSAGE;
    }
}
