package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatResponseMessage extends AbstractResponseMessage {

    private static final long serialVersionUID = -1490344894610276481L;
    private String from;
    private String content;

    public ChatResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public ChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return CHAT_RESPONSE_MESSAGE;
    }
}
