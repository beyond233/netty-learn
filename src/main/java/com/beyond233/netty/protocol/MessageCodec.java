package com.beyond233.netty.protocol;

import com.beyond233.netty.protocol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * description: 消息编解码器
 *
 * @author beyond233
 * @since 2021/5/14 21:31
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 消息编码
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 4字节的魔数： 用来再第一时间判定是否是无效数据包
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1字节的版本号
        out.writeByte(1);
        // 3. 1字节的序列化方式：代表消息正文采用那种序列化反序列化方式 0-jdk; 1-json
        out.writeByte(0);
        // 4. 1字节的指令类型: 代表业务类型，如登录、注册、单聊、群聊等
        out.writeByte(msg.getMessageType());
        // 5. 4个字节的请求序号： 为了双工通信，提供异步能力
        out.writeInt(msg.getSequenceId());
        // 无意义，只用于填充对齐，加上上面的凑齐2的N次方（共16字节）
        out.writeByte(0xff);
        // 6. 4个字节的消息正文的长度
        // 先获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 再写入内容的长度
        out.writeInt(bytes.length);
        // 7. 消息正文内容
        out.writeBytes(bytes);
    }

    /**
     * 消息解码
     **/
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        // 用来补齐2的倍数的总位数的那个字节
        in.readByte();
        int length = in.readInt();
        // 读内容
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        // 根据对应序列化方式反序列化出对象
        if (serializeType == 0) {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Message message = (Message) ois.readObject();
            log.debug("消息解码：magicNum={}, version={}, serializeType={}, messageType={}, sequenceId={}, length={}"
                    , magicNum, version, serializeType, messageType, sequenceId, length);
            log.debug("消息解码：message={}", message);
        }
    }
}
