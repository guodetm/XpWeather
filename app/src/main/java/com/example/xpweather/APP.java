package com.example.xpweather;

import android.app.Application;

import com.lzy.okgo.OkGo;

import org.litepal.LitePalApplication;


/**
 * Created by 肖磊 on 2017/8/7.
 */

public class APP extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LitePalApplication.initialize(this);
        OkGo.getInstance().init(this);
    }
}
