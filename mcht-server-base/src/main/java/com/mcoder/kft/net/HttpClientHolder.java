package com.mcoder.kft.net;

import okhttp3.OkHttpClient;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 1194688733@qq.com
 * @Description: http client
 * @date 2018/7/19 17:11
 */
public class HttpClientHolder {
    private static Lock lock = new ReentrantLock();
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder builder = new OkHttpClient.Builder();

    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            lock.lock();

            try {
                if (okHttpClient == null) {
                    okHttpClient = builder.build();
                }
            } finally {
                lock.unlock();
            }
        }

        return okHttpClient;
    }

    public static OkHttpClient.Builder builder() {
        return builder;
    }


}
