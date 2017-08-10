package com.example.xpweather;

import android.app.Application;
import android.content.Context;

import com.lzy.okgo.OkGo;

import org.litepal.LitePalApplication;


/**
 * Created by 肖磊 on 2017/8/7.
 */

public class APP extends Application {
    private static APP instance;

    // 全局 Context 获取
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public static APP getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LitePalApplication.initialize(this);
        OkGo.getInstance().init(this);
    }
}
