package com.beyond233.netty.protocol.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    private static final long serialVersionUID = -3468140017942841628L;

    /**
     * 根据消息类型字节，获得对应的消息 class
     *
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    /**
     * 获取消息类型
     *
     * @return 消息类型
     */
    public abstract int getMessageType();

    public static final int LOGIN_REQUEST_MESSAGE;
    public static final int LOGIN_RESPONSE_MESSAGE = 1;
    public static final int CHAT_REQUEST_MESSAGE;
    public static final int CHAT_RESPONSE_MESSAGE = 3;
    public static final int GROUP_CREATE_REQUEST_MESSAGE;
    public static final int GROUP_CREATE_RESPONSE_MESSAGE = 5;
    public static final int GROUP_JOIN_REQUEST_MESSAGE;
    public static final int GROUP_JOIN_RESPONSE_MESSAGE;
    public static final int GROUP_QUIT_REQUEST_MESSAGE;
    public static final int GROUP_QUIT_RESPONSE_MESSAGE = 9;
    public static final int GROUP_CHAT_REQUEST_MESSAGE = 10;
    public static final int GROUP_CHAT_RESPONSE_MESSAGE = 11;
    public static final int GROUP_MEMBERS_REQUEST_MESSAGE = 12;
    public static final int GROUP_MEMBERS_RESPONSE_MESSAGE = 13;
    public static final int PING_MESSAGE = 14;
    public static final int PONG_MESSAGE = 15;
    /**
     * 请求类型 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 101;
    /**
     * 响应类型 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 102;

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        GROUP_QUIT_REQUEST_MESSAGE = 8;
        GROUP_JOIN_RESPONSE_MESSAGE = 7;
        GROUP_JOIN_REQUEST_MESSAGE = 6;
        GROUP_CREATE_REQUEST_MESSAGE = 4;
        CHAT_REQUEST_MESSAGE = 2;
        LOGIN_REQUEST_MESSAGE = 0;
        messageClasses.put(LOGIN_REQUEST_MESSAGE, LoginRequestMessage.class);
        messageClasses.put(LOGIN_RESPONSE_MESSAGE, LoginResponseMessage.class);
        messageClasses.put(CHAT_REQUEST_MESSAGE, ChatRequestMessage.class);
        messageClasses.put(CHAT_RESPONSE_MESSAGE, ChatResponseMessage.class);
        messageClasses.put(GROUP_CREATE_REQUEST_MESSAGE, GroupCreateRequestMessage.class);
        messageClasses.put(GROUP_CREATE_RESPONSE_MESSAGE, GroupCreateResponseMessage.class);
        messageClasses.put(GROUP_JOIN_REQUEST_MESSAGE, GroupJoinRequestMessage.class);
        messageClasses.put(GROUP_JOIN_RESPONSE_MESSAGE, GroupJoinResponseMessage.class);
        messageClasses.put(GROUP_QUIT_REQUEST_MESSAGE, GroupQuitRequestMessage.class);
        messageClasses.put(GROUP_QUIT_RESPONSE_MESSAGE, GroupQuitResponseMessage.class);
        messageClasses.put(GROUP_CHAT_REQUEST_MESSAGE, GroupChatRequestMessage.class);
        messageClasses.put(GROUP_CHAT_RESPONSE_MESSAGE, GroupChatResponseMessage.class);
        messageClasses.put(GROUP_MEMBERS_REQUEST_MESSAGE, GroupMembersRequestMessage.class);
        messageClasses.put(GROUP_MEMBERS_RESPONSE_MESSAGE, GroupMembersResponseMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequestMessage.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponseMessage.class);
    }

}
