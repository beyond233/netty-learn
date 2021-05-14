package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupQuitResponseMessage extends AbstractResponseMessage {
    private static final long serialVersionUID = -3704586455039610920L;

    public GroupQuitResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GROUP_QUIT_RESPONSE_MESSAGE;
    }
}
