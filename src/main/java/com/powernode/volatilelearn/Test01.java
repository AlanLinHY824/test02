package com.powernode.volatilelearn;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/17
 */
public class Test01 {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new MyThread().start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("atomicCount:"+MyThread.atomicCount.get());
        System.out.println("count:"+MyThread.count);
    }
    static class MyThread extends Thread{
        private static int count;
        private static AtomicInteger atomicCount=new AtomicInteger();
        public static void addCount(){
            for (int i = 0; i < 1000; i++) {
                atomicCount.getAndIncrement();
                count++;
            }
            System.out.println(Thread.currentThread().getName()+"atomicCount="+atomicCount.get());
            System.out.println(Thread.currentThread().getName()+"count="+count);
        }
        @Override
        public void run() {
            addCount();
        }
    }
}
