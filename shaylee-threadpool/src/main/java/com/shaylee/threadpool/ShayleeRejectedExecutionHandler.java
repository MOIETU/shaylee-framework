package com.shaylee.threadpool;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池堵塞,拒绝请求的异常处理
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShayleeRejectedExecutionHandler extends CallerRunsPolicy implements RejectedExecutionHandler {

    /**
     * 当线程池繁忙时是否由调用者自己执行
     */
    private boolean isCallerRuns;
    /**
     * 拒绝请求次数
     */
    private AtomicLong rejectedTimes = new AtomicLong(0);
    /**
     * 拒绝请求反馈的错误信息
     */
    private String rejectedErrMsg;

    /**
     * 构造
     *
     * @param isCallerRuns   当线程池繁忙时是否有调用者自己执行
     * @param rejectedErrMsg 拒绝请求反馈的错误信息
     */
    public ShayleeRejectedExecutionHandler(boolean isCallerRuns, String rejectedErrMsg) {
        this.isCallerRuns = isCallerRuns;
        this.rejectedErrMsg = rejectedErrMsg;
    }

    /**
     * 获取拒绝请求次数
     *
     * @return 拒绝请求次数
     */
    public Long getRejectedTimes() {
        return rejectedTimes.get();
    }

    /**
     * 拒绝请求的处理
     *
     * @see RejectedExecutionHandler#rejectedExecution(Runnable, java.util.concurrent.ThreadPoolExecutor)
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        Semaphore semaphore = null;
        String msg = rejectedErrMsg;
        boolean callRuns = isCallerRuns;
        if (e instanceof ShayleeThreadPoolExecutor) {
            ShayleeThreadPoolExecutor mye = (ShayleeThreadPoolExecutor) e;
            semaphore = mye.semaphore;
            if (semaphore != null) {
                msg = rejectedErrMsg + " The Number of waiting requests is " + semaphore.getQueueLength();
            }
            if (mye.isPaused()) {
                callRuns = false;
            }
        }

        if (callRuns) {
            // CallerRunsPolicy
            super.rejectedExecution(r, e);
            return;
        }

        if (semaphore != null) {
            semaphore.release();
        }

        rejectedExecution(msg);
    }

    void rejectedExecution(String msg) {
        // 增加拒绝请求次数
        rejectedTimes.incrementAndGet();
        throw new RejectedExecutionException(msg);
    }
}
