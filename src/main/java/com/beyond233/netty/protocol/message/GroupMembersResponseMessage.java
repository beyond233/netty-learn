package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends Message {

    private static final long serialVersionUID = 1437498939403838535L;
    private Set<String> members;

    public GroupMembersResponseMessage(Set<String> members) {
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GROUP_MEMBERS_RESPONSE_MESSAGE;
    }
}
