package com.shaylee.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程池执行类 在JDK ThreadPoolExecutor基础上 添加可暂停、恢复功能
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
public class ShayleeThreadPoolExecutor extends ThreadPoolExecutor {
    private static Logger logger = LoggerFactory.getLogger(ShayleeThreadPoolExecutor.class);
    public static final String DEFAULT_REJECTED_ERRMSG = "The system is busy, please try again later!";
    public static final String DEFAULT_PAUSED_ERRMSG = "Suspension of service, please try again later!";
    public static final String POOL_NAME_PREFIX = "thread-pool";

    /**
     * 是否已经暂停
     */
    private boolean paused = false;
    /**
     * 暂停锁
     */
    private ReentrantLock pauseLock = new ReentrantLock();
    /**
     * 没有暂停的条件锁
     */
    private Condition unpaused = pauseLock.newCondition();
    /**
     * 线程工程类
     */
    private ShayleeThreadFactory threadFactory;
    /**
     * 拒绝请求处理器
     */
    private ShayleeRejectedExecutionHandler handler;
    /**
     * 请求等待时长
     */
    private Long waitTime;
    /**
     * 线程池名
     */
    private String name;
    /**
     * 并发控制信号量
     */
    Semaphore semaphore;

    public ShayleeThreadPoolExecutor(String poolName, int maximumPoolSize, long keepAliveTime,
                                     long waitTime, boolean isCallerRuns) {
        this(poolName, 0, maximumPoolSize, keepAliveTime, waitTime, isCallerRuns);
    }

    public ShayleeThreadPoolExecutor(String poolName, int corePoolSize, int maximumPoolSize,
                                     long keepAliveTime, long waitTime, boolean isCallerRuns) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, waitTime,
                new ShayleeThreadFactory(POOL_NAME_PREFIX, poolName), isCallerRuns);
        this.name = poolName;
    }

    /**
     * 构造ThreadPoolExecutor线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   线程空闲自动退出时长（秒）
     * @param waitTime        请求等待时长
     * @param threadFactory   线程工厂类
     * @param isCallerRuns    拒绝请求处理类
     */
    private ShayleeThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                      long waitTime, ShayleeThreadFactory threadFactory, boolean isCallerRuns) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, waitTime, threadFactory,
                new ShayleeRejectedExecutionHandler(isCallerRuns, DEFAULT_REJECTED_ERRMSG));
    }

    /**
     * 构造ThreadPoolExecutor线程池
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   线程空闲自动退出时长（秒）
     * @param waitTime        请求等待时长
     * @param threadFactory   线程工厂类
     * @param handler         拒绝请求处理类
     */
    private ShayleeThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                      long waitTime, ShayleeThreadFactory threadFactory, ShayleeRejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new SynchronousQueue<>(), threadFactory, handler);
        this.handler = handler;
        this.waitTime = waitTime;
        this.semaphore = new Semaphore(maximumPoolSize - 1);
        this.threadFactory = threadFactory;
        if (keepAliveTime > 0) {
            this.allowCoreThreadTimeOut(true);
        }
    }

    /**
     * 是否已暂停了
     *
     * @return 是返回true，不是返回false
     */
    public boolean isPaused() {
        pauseLock.lock();
        try {
            return paused;
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * 执行前
     *
     * @see ThreadPoolExecutor#beforeExecute(Thread, Runnable)
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        // 一直等到不是暂停为止
        while (isPaused()) {
            try {
                unpaused.await();
            } catch (InterruptedException e) {
                t.interrupt();
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        pauseLock.lock();
        try {
            paused = true;
            handler.setRejectedErrMsg(DEFAULT_PAUSED_ERRMSG);
        } finally {
            pauseLock.unlock();
        }
    }

    /**
     * 恢复继续执行
     */
    public void resume() {
        pauseLock.lock();
        try {
            paused = false;
            handler.setRejectedErrMsg(DEFAULT_REJECTED_ERRMSG);
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    private void checkPause() {
        pauseLock.lock();
        try {
            if (paused) {
                throw new RejectedExecutionException(DEFAULT_PAUSED_ERRMSG);
            }
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public void execute(final Runnable command) {
        checkPause();

        boolean ok = true;
        try {
            if (semaphore != null) {
                if (waitTime > 0) {
                    ok = semaphore.tryAcquire(waitTime, TimeUnit.SECONDS);
                } else {
                    ok = semaphore.tryAcquire();
                }
            }
        } catch (InterruptedException e) {
            ok = false;
        }
        if (!ok) {
            String msg = String.format(
                    "Thread pool is EXHAUSTED!"
                            + " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d),"
                            + " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                    this.name, this.getPoolSize(), this.getActiveCount(), this.getCorePoolSize(),
                    this.getMaximumPoolSize(), this.getLargestPoolSize(), this.getTaskCount(),
                    this.getCompletedTaskCount(), this.isShutdown(), this.isTerminated(),
                    this.isTerminating());
            logger.error(msg);
            String rejectedErrMsg = DEFAULT_REJECTED_ERRMSG + "The Number of waiting requests is "
                    + semaphore.getQueueLength();
            handler.rejectedExecution(rejectedErrMsg);
            return;
        } else {
            /*assert semaphore != null;
            logger.info("可用数={}", semaphore.availablePermits());*/
        }

        super.execute(() -> {
            try {
                command.run();
            } finally {
                if (semaphore != null) {
                    semaphore.release();
                }
            }
        });
    }

    @Override
    public ShayleeThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public String getName() {
        return name;
    }

    public ShayleeRejectedExecutionHandler getHandler() {
        return handler;
    }

}
