package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupMembersRequestMessage extends Message {
    private static final long serialVersionUID = -244607434568739861L;
    private String groupName;

    public GroupMembersRequestMessage(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GROUP_MEMBERS_REQUEST_MESSAGE;
    }
}
