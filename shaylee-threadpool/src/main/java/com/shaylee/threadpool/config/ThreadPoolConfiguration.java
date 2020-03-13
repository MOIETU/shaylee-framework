package com.shaylee.threadpool.config;

import com.shaylee.threadpool.ThreadPool;
import com.shaylee.threadpool.manager.ThreadPoolManager;
import com.shaylee.threadpool.properties.ThreadPoolManagerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Title: 线程池配置
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
@Configuration
public class ThreadPoolConfiguration {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ThreadPoolManagerProperties threadPoolManagerProperties;

    @Bean("threadPools")
    public Map<String, ThreadPool> threadPoolMap() {
        logger.info("initThreadPool begin...");
        Map<String, ThreadPool> threadPoolMap = this.initThreadPool();
        logger.info("initThreadPool end...");
        return threadPoolMap;
    }

    /**
     * 初始化线程池
     */
    private Map<String, ThreadPool> initThreadPool() {
        Map<String, ThreadPoolManagerProperties.PoolProperties> threadpool = threadPoolManagerProperties.getThreadpool();
        if (threadpool == null) {
            logger.warn("no thread pool config found...");
            return null;
        }
        Map<String, ThreadPool> threadPoolMap = new LinkedHashMap<>(threadpool.size());
        for (Map.Entry<String, ThreadPoolManagerProperties.PoolProperties> threadPoolEntry : threadpool.entrySet()) {
            ThreadPoolManagerProperties.PoolProperties poolProperties = threadPoolEntry.getValue();
            try {
                // 池的核心大小，即最小值
                int corePoolSize = poolProperties.getCorePoolSize();
                // 池的最大个数
                int maximumPoolSize = poolProperties.getMaxPoolSize();
                if (maximumPoolSize < corePoolSize) {
                    maximumPoolSize = corePoolSize;
                }
                // 线程空闲时长(单位：秒)
                int keepAliveTime = poolProperties.getKeepAliveTime();
                if (keepAliveTime <= 0) {
                    keepAliveTime = 120;
                }
                // 最大等待时长(单位：秒)
                int waitTime = poolProperties.getWaitTime();
                if (waitTime < 0) {
                    waitTime = 45;
                }
                // 当拒绝时是否由调用者执行
                boolean isCallerRuns = poolProperties.getIsCallerRuns();
                // 创建线程池
                ThreadPool pool = ThreadPoolManager.createThreadPool(
                        threadPoolEntry.getKey(), corePoolSize, maximumPoolSize,
                        keepAliveTime, waitTime, isCallerRuns);
                threadPoolMap.put(threadPoolEntry.getKey(), pool);
                logger.info("init the threadpool[" + threadPoolEntry.getKey() + "] end");
            } catch (Exception e) {
                logger.error("init the threadpool failed, poolCode=" + threadPoolEntry.getKey(), e);
            }
        }
        return threadPoolMap;
    }
}
