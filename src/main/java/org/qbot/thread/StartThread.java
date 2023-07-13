package org.qbot.thread;

import org.qbot.toolkit.Setting;

/**
 * GetThreadState class
 *
 * @author 649953543@qq.com
 * @date 2021/11/01
 */
public class StartThread extends Thread {
    /**
     * 每隔一分钟检测一次线程运行情况
     */
    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try {
            sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean autoFortune = Setting.getAutoFortune();
        boolean autoTips = Setting.getAutoTips();
        boolean autoNews = Setting.getAutoNews();
        AutoGetFortuneThread autoGetFortuneThread = new AutoGetFortuneThread();
        AutoThread autoThread = new AutoThread();
        if (autoFortune) {
            autoGetFortuneThread.start();
        }
        if (autoTips || autoNews) {
            autoThread.start();
        }
        while (true) {
            try {
                sleep(60000);
                if (autoFortune) {
                    if (!autoGetFortuneThread.isAlive()) {
                        autoGetFortuneThread = new AutoGetFortuneThread();
                        autoGetFortuneThread.start();
                    }
                }
                if (autoTips || autoNews) {
                    if (!autoThread.isAlive()) {
                        autoThread = new AutoThread();
                        autoThread.start();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
