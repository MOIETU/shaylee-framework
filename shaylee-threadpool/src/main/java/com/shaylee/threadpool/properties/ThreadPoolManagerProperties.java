package com.shaylee.threadpool.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Title: 线程池配置
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
@Getter
@Setter
@ToString
@Component("threadPoolManagerProperties")
@ConfigurationProperties(prefix = "thread-manager")
public class ThreadPoolManagerProperties {

    /**
     * 线程池列表
     */
    Map<String, PoolProperties> threadpool;

    @Getter
    @Setter
    public static class PoolProperties {
        /**
         * 池的核心大小，即最小值
         */
        private Integer corePoolSize;
        /**
         * 池的最大个数
         */
        private Integer maxPoolSize;
        /**
         * 线程空闲时长(单位：秒)
         */
        private Integer keepAliveTime;
        /**
         * 最大等待时长(单位：秒)
         */
        private Integer waitTime;
        /**
         * 当线程池繁忙时是否由调用者自己执行
         */
        private Boolean isCallerRuns;
    }
}
