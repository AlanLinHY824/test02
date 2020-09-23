package com.powernode.atomicclass;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用原子变量类定义一个计数器
 * 所有地方都使用这一个计数器，可以设计为单例
 * @Author AlanLin
 * @Description
 * @Date 2020/9/21
 */
public class Indicator {
    private Indicator() {
    }
    private static final Indicator instance=new Indicator();
    public static Indicator getInstance(){
        return instance;
    }
    //记录请求总数
    private final AtomicLong requestCount=new AtomicLong(0);
    private final AtomicLong successCount=new AtomicLong(0);
    private final AtomicLong failedCount=new AtomicLong(0);
}
