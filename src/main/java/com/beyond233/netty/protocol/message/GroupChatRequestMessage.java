package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupChatRequestMessage extends Message {
    private static final long serialVersionUID = 2313082997131693549L;
    private String content;
    private String groupName;
    private String from;

    public GroupChatRequestMessage(String from, String groupName, String content) {
        this.content = content;
        this.groupName = groupName;
        this.from = from;
    }

    @Override
    public int getMessageType() {
        return GROUP_CHAT_REQUEST_MESSAGE;
    }
}
