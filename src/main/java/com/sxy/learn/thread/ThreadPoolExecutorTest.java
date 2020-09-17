package com.sxy.learn.thread;

import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author sxy
 * @version 1.0
 * @className ThreadPoolExecutorTest
 * @date 2020/9/10 9:39
 */

public class ThreadPoolExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("my-pool-%d").build();

        //Common Thread Pool
        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 200,
                0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        for (;;) {
            pool.execute(()-> {
                for (int i = 0; i < 7; i++) {
                    System.out.println(Thread.currentThread().getName());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println(pool.getPoolSize() + " " + pool.getQueue().size() + " " + pool.getActiveCount());
            Thread.sleep(10);
        }
        //pool.shutdown();
    }
}
