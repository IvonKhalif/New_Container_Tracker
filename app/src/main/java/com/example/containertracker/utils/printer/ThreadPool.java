package com.example.containertracker.utils.printer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator
 */
public class ThreadPool {

    private static ThreadPool threadPool;
    /**
     * kumpulan utas java
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * jumlah maksimum utas
     */
    private final static int MAX_POOL_COUNTS = 20;

    /**
     * waktu kelangsungan hidup utas
     */
    private final static long ALIVETIME = 200L;

    /**
     * jumlah utas inti
     */
    private final static int CORE_POOL_SIZE = 20;

    /**
     * antrian cache kumpulan utas
     */
    private BlockingQueue<Runnable> mWorkQueue = new ArrayBlockingQueue<>(CORE_POOL_SIZE);

    /**
     * Menyediakan kumpulan utas dengan kemampuan untuk membuat utas baru
     */
    private ThreadFactory threadFactory = new ThreadFactoryBuilder("ThreadPool");

    private ThreadPool() {
        //Inisialisasi kumpulan utas Jumlah utas inti adalah 20, jumlah utas maksimum adalah 30, kelangsungan hidup utas adalah 200L, antrian utas mWorkQueue,
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_COUNTS, ALIVETIME, TimeUnit.SECONDS, mWorkQueue, threadFactory);
    }

    public static ThreadPool getInstantiation() {
        if (threadPool == null) {
            threadPool = new ThreadPool();
        }
        return threadPool;
    }

    /**
     * Tambahkan tugas ke kumpulan utas
     */
    public void addTask(Runnable runnable) {
        if (runnable == null) {
            throw new NullPointerException("addTask(Runnable runnable)Parameter yang masuk kosong");
        }
        if (threadPoolExecutor != null && threadPoolExecutor.getActiveCount() < MAX_POOL_COUNTS) {
            threadPoolExecutor.execute(runnable);
        }
    }

    public void stopThreadPool() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
            threadPoolExecutor = null;
            threadPool = null;
        }
    }
}
