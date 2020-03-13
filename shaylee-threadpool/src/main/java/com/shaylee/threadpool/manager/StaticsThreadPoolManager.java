package com.shaylee.threadpool.manager;

import com.shaylee.threadpool.ThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程池管理
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
public class StaticsThreadPoolManager {

    private static Map<String, ThreadPool> threadPools = new ConcurrentHashMap<>();

    /**
     * 创建线程池
     *
     * @param poolName        线程池名称
     * @param corePoolSize    内核线程数
     * @param maximumPoolSize 繁忙时最大线程数
     * @param keepAliveTime   线程空闲自动退出时长（秒）
     * @param waitTime        等待超时时长（秒）
     * @return 线程池
     */
    public static ThreadPool createThreadPool(String poolName, int corePoolSize,
                                              int maximumPoolSize, long keepAliveTime, long waitTime, boolean isCallerRuns) {
        ThreadPool threadPool = new ThreadPool(poolName, corePoolSize, maximumPoolSize,
                keepAliveTime, waitTime, isCallerRuns);
        threadPools.put(poolName, threadPool);
        return threadPool;
    }

    /**
     * 根据线程池名称获取线程池
     *
     * @param poolName 线程池名称
     * @return 线程池
     */
    public static ThreadPool getThreadPool(String poolName) {
        return threadPools.get(poolName);
    }

    /**
     * 获取所有线程池
     *
     * @return 线程池Map容器
     */
    public static Map<String, ThreadPool> getThreadPools() {
        return threadPools;
    }

}
