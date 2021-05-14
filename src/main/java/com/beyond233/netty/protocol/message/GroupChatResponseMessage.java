package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupChatResponseMessage extends AbstractResponseMessage {
    private static final long serialVersionUID = -7516846649008779279L;
    private String from;
    private String content;

    public GroupChatResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    public GroupChatResponseMessage(String from, String content) {
        this.from = from;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return GROUP_CHAT_RESPONSE_MESSAGE;
    }
}
