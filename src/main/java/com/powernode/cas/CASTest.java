package com.powernode.cas;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/9/17
 *这是CAS操作
 *测试一下不同位置会不会有冲突
 我来试一下合并
 再测试一下
 */
public class CASTest {
    public static void main(String[] args) {
        CASCounter casCounter = new CASCounter();
        for (int i = 0; i < 1000; i++) {
            new Thread(
                    new Runnable(){
                @Override
                public void run() {
                    System.out.println(casCounter.increatementAndGet());
                }
            }).start();
        }
    }
}
//本地修改了这里
class CASCounter{
    volatile private long value;

    public long getValue() {
        return value;
    }

    private boolean compareAndSwap(long expectedValue,long newValue){
        synchronized (this){
            if (value==expectedValue){
                value=newValue;
                return true;
            }
            else {
                return false;
            }
        }
    }
    //自增的方法
    public long increatementAndGet(){
        long oldValue=value;
        long newValue=value+1;
        do{
            oldValue=value;
            newValue=value+1;
        }while (!compareAndSwap(oldValue,newValue));
        return newValue;
    }
}
