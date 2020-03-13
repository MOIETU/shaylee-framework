package com.shaylee.threadpool;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 线程池类
 * 功能说明： 是线程池的总控构建类, 实现通过根据可传入参数构造相应的执行队列、线程工厂、线程池执行器
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
public class ThreadPool {
    /**
     * 线程池执行器
     */
    private ShayleeThreadPoolExecutor executor;

    /**
     * 构造线程池
     *
     * @param poolName        线程池名
     * @param corePoolSize    内核线程数
     * @param maximumPoolSize 繁忙时最大线程数
     * @param keepAliveTime   线程空闲自动退出时长（秒）
     * @param waitTime        等待超时时长（秒）
     * @param isCallerRuns    当线程池繁忙时是否有调用者自己执行
     */
    public ThreadPool(String poolName, int corePoolSize, int maximumPoolSize, long keepAliveTime,
                      long waitTime, boolean isCallerRuns) {
        // 线程池执行器
        this.executor = new ShayleeThreadPoolExecutor(poolName, corePoolSize, maximumPoolSize,
                keepAliveTime, waitTime, isCallerRuns);
    }

    /**
     * 线程池名称 getter
     *
     * @return 线程池名称
     */
    public String getPoolName() {
        return executor.getName();
    }

    /**
     * 内核线程数 getter
     *
     * @return 内核线程数
     */
    public int getCorePoolSize() {
        return this.executor.getCorePoolSize();
    }

    /**
     * 内核线程数 setter
     *
     * @param corePoolSize 内核线程数
     */
    public void setCorePoolSize(int corePoolSize) {
        this.executor.setCorePoolSize(corePoolSize);
    }

    /**
     * 最大线程数 getter
     *
     * @return 最大线程数
     */
    public int getMaximumPoolSize() {
        return this.executor.getMaximumPoolSize();
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.executor.setMaximumPoolSize(maximumPoolSize);
    }

    /**
     * 最大空闲时间 getter
     *
     * @return 最大空闲时间
     */
    public long getKeepAliveTime() {
        return this.executor.getKeepAliveTime(TimeUnit.SECONDS);
    }

    /**
     * 最大空闲时间 setter
     *
     * @param keepAliveTime 最大空闲时间
     */
    public void setKeepAliveTime(long keepAliveTime) {
        this.executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
    }

    /**
     * 超时时间(单位秒) getter
     *
     * @return 超时时间(单位秒)
     */
    public long getTimeOut() {
        return executor.getWaitTime();
    }

    /**
     * 拒绝请求次数 getter
     *
     * @return 拒绝请求次数
     */
    public long getRejectedTimes() {
        return executor.getHandler().getRejectedTimes();
    }

    /**
     * 拒绝请求反馈的错误信息 getter
     *
     * @return 拒绝请求反馈的错误信息
     */
    public String getRejectedErrMsg() {
        return executor.getHandler().getRejectedErrMsg();
    }

    /**
     * 线程池执行器 getter
     *
     * @return 线程池执行器
     */
    public ShayleeThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    /**
     * 等待执行任务数 getter
     *
     * @return 等待执行任务数
     */
    public int getQueueSize() {
        if (executor.semaphore == null) {
            return 0;
        }
        return executor.semaphore.getQueueLength();
    }

    /**
     * 当前线程数 getter
     *
     * @return 当前线程数
     */
    public int getThreadNumber() {
        return executor.getThreadFactory().getThreadCount();
    }

    /**
     * 活动线程数 getter
     *
     * @return 活动线程数
     */
    public int getActiveThread() {
        return this.executor.getActiveCount();
    }

    /**
     * 获取所有线程堆栈
     *
     * @return 所有线程堆栈(list)
     */
    public List<ThreadStack> getAllThreadStacks() {
        return this.executor.getThreadFactory().getAllThreadStacks();
    }

    /**
     * 是否暂停执行
     *
     * @return 如果暂停了返回true否则返回false
     */
    public boolean isPaused() {
        return this.executor.isPaused();
    }

    /**
     * 暂停执行
     */
    public void pause() {
        this.executor.pause();
    }

    /**
     * 恢复继续执行
     */
    public void resume() {
        this.executor.resume();
    }

    /**
     * 提交请求线程池
     *
     * @param r Runnable请求
     * @return Future对象
     */
    public Future<?> submit(Runnable r) {
        return this.executor.submit(r);
    }

    /**
     * 提交请求线程池
     *
     * @param c Callable请求
     * @return Future对象
     */
    public Future<?> submit(Callable<?> c) {
        return this.executor.submit(c);
    }

}
