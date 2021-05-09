package com.beyond233.netty.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * description: jdk的Future
 *
 * @author beyond233
 * @since 2021/5/9 17:54
 */
@Slf4j
public class JdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1.创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 2.执行任务
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 100;
            }
        });
        // 3.通过future获取线程执行的结果
        log.debug("等待结果");
        log.debug("结果是：{}", future.get());
    }
}
