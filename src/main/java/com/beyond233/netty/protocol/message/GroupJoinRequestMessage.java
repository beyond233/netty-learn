package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupJoinRequestMessage extends Message {
    private static final long serialVersionUID = -4218503385388765674L;
    private String groupName;

    private String username;

    public GroupJoinRequestMessage(String username, String groupName) {
        this.groupName = groupName;
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return GROUP_JOIN_REQUEST_MESSAGE;
    }
}
