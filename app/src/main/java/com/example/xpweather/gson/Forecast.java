package com.example.xpweather.gson;

import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 肖磊 on 2017/8/9.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
