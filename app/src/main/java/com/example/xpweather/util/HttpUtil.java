package com.example.xpweather.util;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 肖磊 on 2017/8/7.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendOkGoRequest(String address,Call callback){

    }
    public static interface Call{
        public void callBack(String response);
    }

}
