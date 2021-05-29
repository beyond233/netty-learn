package com.beyond233.netty.protocol.message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * description: 序列Id生成器
 *
 * @author beyond233
 * @since 2021/5/29 18:32
 */
public class SequenceIdGenerator {

    private static final AtomicInteger SEQUENCE_ID = new AtomicInteger();

    /**
     * 序列Id加1并返回
     **/
    public static Integer nextId() {
        return SEQUENCE_ID.incrementAndGet();
    }


}
