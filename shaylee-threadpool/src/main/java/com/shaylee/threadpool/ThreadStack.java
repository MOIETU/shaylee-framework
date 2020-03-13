package com.shaylee.threadpool;

import lombok.Data;

/**
 * 线程堆栈信息
 * Project: shaylee-framework
 *
 * @author Adrian
 * @date 2020-03-13
 */
@Data
public class ThreadStack {
    /**
     * 线程ID
     */
    private Long id;
    /**
     * 线程名
     */
    private String name;
    /**
     * 堆栈信息
     */
    private String stack;
}
