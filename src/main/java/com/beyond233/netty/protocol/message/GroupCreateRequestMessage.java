package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(callSuper = true)
public class GroupCreateRequestMessage extends Message {
    private static final long serialVersionUID = -1901444384983376413L;
    private String groupName;
    private Set<String> members;

    public GroupCreateRequestMessage(String groupName, Set<String> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GROUP_CREATE_REQUEST_MESSAGE;
    }
}
