package com.shaylee.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工厂类ShayleeThreadFactory
 * 使用说明： 在线程池ShayleeThreadFactory对象构造初始化时创建该工厂对象，
 * 用来替换默认的工厂，即传入ShayleeThreadFactory构造方法用作线程构建工厂对象
 * 功能说明： 在JDK ThreadFactory基础上
 * 添加线程运行前、运行后事件及错误日志， 以及获取线程堆栈的方法
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
public class ShayleeThreadFactory implements ThreadFactory {
    private static Logger logger = LoggerFactory.getLogger(ShayleeThreadFactory.class);
    /**
     * 线程容器
     */
    private static final Map<String, Thread> THREADS = new ConcurrentHashMap<>();
    /**
     * 线程号序列
     */
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    /**
     * 线程组
     */
    private final ThreadGroup group;
    /**
     * 线程名称前缀
     */
    private final String namePrefix;
    /**
     * 是否需要监控
     */
    private boolean monitor = false;

    /**
     * 构造
     *
     * @param poolNamePrefix 线程池前缀
     * @param poolName       线程池名称
     */
    public ShayleeThreadFactory(String poolNamePrefix, String poolName) {
        SecurityManager s = System.getSecurityManager();
        if (s != null) {
            group = s.getThreadGroup();
        } else {
            group = Thread.currentThread().getThreadGroup();
        }
        namePrefix = poolNamePrefix + "-" + poolName + "-";
    }

    /**
     * 创建新的线程
     *
     * @see ThreadFactory#newThread(Runnable)
     */
    @Override
    public Thread newThread(Runnable r) {
        final String name = namePrefix + THREAD_NUMBER.getAndIncrement();
        initFirstDo(name);
        Thread t = null;
        try {
            t = new Thread(group, r, name, 0) {
                @Override
                public void run() {
                    try {
                        runFirstDo(name, this);
                    } catch (Throwable e) {
                        logger.error("Initialization thread pre-work failed...", e);
                    }

                    try {
                        super.run();
                    } catch (Throwable e) {
                        logger.error("The thread run failed...", e);
                        e.printStackTrace();
                    } finally {
                        runFinallyDo(name, this);
                    }
                }
            };

            t.setContextClassLoader(Thread.currentThread().getContextClassLoader());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        } finally {
            initFinally(name, t);
        }
    }

    /**
     * 初始化线程前置工作
     * @param threadName 线程名
     */
    protected void initFirstDo(String threadName) {

    }

    /**
     * 线程池运行前置工作
     *
     * @param threadName 线程名
     * @param thread     线程
     */
    protected void runFirstDo(String threadName, Thread thread) {

    }

    /**
     * 线程池运行后置工作
     *
     * @param threadName 线程名
     * @param thread     线程
     */
    protected void runFinallyDo(String threadName, Thread thread) {
        THREADS.remove(threadName);
    }

    /**
     * 获取当前线程数
     *
     * @return 当前线程数
     */
    public int getThreadCount() {
        return THREADS.size();
    }

    /**
     * 初始化线程后置工作
     *
     * @param threadName 线程名称
     * @param thread     线程
     */
    protected void initFinally(String threadName, Thread thread) {
        if (monitor) {
            synchronized (THREADS) {
                try {
                    THREADS.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        THREADS.put(threadName, thread);
    }

    /**
     * 获取所有线程的堆栈信息
     *
     * @return 所有线程的堆栈信息
     */
    public List<ThreadStack> getAllThreadStacks() {
        try {
            monitor = true;
            Object[] os;
            synchronized (THREADS) {
                os = THREADS.values().toArray();
                THREADS.notify();
            }

            List<ThreadStack> list = new ArrayList<>();

            for (Object o : os) {
                ThreadStack stack = new ThreadStack();
                Thread thread = (Thread) o;
                stack.setName(thread.getName());
                stack.setId(thread.getId());
                StackTraceElement[] stackTrace = thread.getStackTrace();
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement stackTraceElement : stackTrace) {
                    sb.append(stackTraceElement).append("\n");
                }
                stack.setStack(sb.toString());
                list.add(stack);
            }
            return list;
        } finally {
            monitor = false;
        }
    }

}
