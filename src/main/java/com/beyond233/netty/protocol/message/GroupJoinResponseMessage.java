package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupJoinResponseMessage extends AbstractResponseMessage {

    private static final long serialVersionUID = -1131959859112472260L;

    public GroupJoinResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GROUP_JOIN_RESPONSE_MESSAGE;
    }
}
