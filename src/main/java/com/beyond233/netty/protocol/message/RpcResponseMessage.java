package com.beyond233.netty.protocol.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author yihang
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {
    private static final long serialVersionUID = 507213291377582547L;
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
