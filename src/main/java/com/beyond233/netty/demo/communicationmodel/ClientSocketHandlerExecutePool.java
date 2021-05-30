package com.beyond233.netty.demo.communicationmodel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: 处理客户端连接的线程池
 *
 * @author beyond233
 * @since 2021/5/30 11:10
 */
public class ClientSocketHandlerExecutePool {

    private ExecutorService executorService;

    /**
     * 构造处理客户端连接的线程池
     *
     * @param maxPoolSize 线程池最大容量
     * @param queueSize   阻塞队列大小
     * @since 2021/5/30 11:19
     */
    public ClientSocketHandlerExecutePool(int maxPoolSize, int queueSize) {
        // 获取当前设备Java虚拟机的可用的处理器数量
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (maxPoolSize < availableProcessors) {
            throw new RuntimeException("线程池最大容量应该大于等于当前设备可用的处理器数: maxPoolSize=" + maxPoolSize + ", availableProcessors=" + availableProcessors);
        }
        executorService = new ThreadPoolExecutor(availableProcessors, maxPoolSize, 120L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize), new ClientSocketHandlerThreadFactory());
    }


    /**
     * 执行任务
     *
     * @param task 任务
     * @since 2021/5/30 11:20
     */
    public void execute(Runnable task) {
        executorService.execute(task);
    }

}
