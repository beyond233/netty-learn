package com.beyond233.netty.protocol;

import com.beyond233.netty.config.Config;
import com.beyond233.netty.protocol.message.LoginRequestMessage;
import com.beyond233.netty.protocol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * description:
 *
 * @author beyond233
 * @since 2021/5/22 21:43
 */
public class SerializerTest {
    public static void main(String[] args) {
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(loggingHandler, messageCodec, loggingHandler);

        LoginRequestMessage message = new LoginRequestMessage("admin", "admin");
        // 出站： 测试序列化
//        channel.writeOutbound(message);

        // 入站： 测试反列化
        ByteBuf byteBuf = message2byteBuf(message);
        channel.writeInbound(byteBuf);

    }

    /**
     * 将消息编码为byteBuf
     */
    public static ByteBuf message2byteBuf(Message msg) {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();

        // 1. 4字节的魔数： 用来再第一时间判定是否是无效数据包
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1字节的版本号
        out.writeByte(1);
        // 3. 1字节的序列化方式：代表消息正文采用那种序列化反序列化方式 0-jdk; 1-json
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1字节的指令类型: 代表业务类型，如登录、注册、单聊、群聊等
        out.writeByte(msg.getMessageType());
        // 5. 4个字节的请求序号： 为了双工通信，提供异步能力
        out.writeInt(msg.getSequenceId());
        // 无意义，只用于填充对齐，加上上面的凑齐2的N次方（共16字节）
        out.writeByte(0xff);
        // 6. 4个字节的消息正文的长度
        // 先获取内容的字节数组 (根据application.properties中配置的序列化算法将消息进行序列化)
        Serializer.Algorithm algorithm = Config.getSerializerAlgorithm();
        byte[] bytes = algorithm.serialize(msg);
        // 再写入内容的长度
        out.writeInt(bytes.length);
        // 7. 消息正文内容
        out.writeBytes(bytes);

        return out;
    }
}
