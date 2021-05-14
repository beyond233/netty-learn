package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {

    private static final long serialVersionUID = 3091170896845305220L;

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return LOGIN_RESPONSE_MESSAGE;
    }
}
