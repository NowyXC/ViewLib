package com.nowy.viewlib.views.loadSir;

import android.os.Handler;
import android.os.Looper;

import com.nowy.viewlib.views.loadSir.callback.Callback;
import com.nowy.viewlib.views.loadSir.core.LoadService;


/**
 * Description:TODO
 * Create Time:2017/9/4 15:21
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class PostUtil {
    private static final int DELAY_TIME = 1000;

    public static void postCallbackDelayed(final LoadService loadService, final Class<? extends Callback> clazz) {
        postCallbackDelayed(loadService, clazz, DELAY_TIME);
    }

    public static void postCallbackDelayed(final LoadService loadService, final Class<? extends Callback> clazz, long
            delay) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadService.showCallback(clazz);
            }
        }, delay);
    }

    public static void postSuccessDelayed(final LoadService loadService) {
        postSuccessDelayed(loadService, DELAY_TIME);
    }

    public static void postSuccessDelayed(final LoadService loadService, long delay) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadService.showSuccess();
            }
        }, delay);
    }
}
