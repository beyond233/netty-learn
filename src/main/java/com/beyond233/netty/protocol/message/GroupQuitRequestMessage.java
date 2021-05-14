package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupQuitRequestMessage extends Message {
    private static final long serialVersionUID = 1782100736599026303L;
    private String groupName;

    private String username;

    public GroupQuitRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return GROUP_QUIT_REQUEST_MESSAGE;
    }
}
