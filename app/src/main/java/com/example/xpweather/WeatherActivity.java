package com.example.xpweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpweather.gson.Forecast;
import com.example.xpweather.gson.Suggestion;
import com.example.xpweather.gson.Weather;
import com.example.xpweather.util.HttpUtil;
import com.example.xpweather.util.SPUtil;
import com.example.xpweather.util.ToastUtil;
import com.example.xpweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 肖磊 on 2017/8/9.
 */

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";
    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.e(TAG, "现在在weatherActivity");
        //初始化控件
        initView();

        //判断缓存只是否有天气信息
        String weatherString = SPUtil.getInstance().getString("weather", null);
        if (weatherString != null) {
            //有缓存时候解析天气数据
            Log.e(TAG, "有缓存解析天气数据");
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //无缓存时查询天气信息
            Log.e(TAG, "没有缓存解析天气数据");
            String weatherId = getIntent().getStringExtra("weather_id");
            Log.e(TAG, "weatherId" + weatherId);
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 获取天气信息
     *
     * @param weatherId intent中读取的weatherId
     */
    private void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=da3f297db73c47abb9f7ae0ec149f0de";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast("获取天气信息失败", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求天气信息成功
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SPUtil.getInstance().putString("weather", responseText);
                            showWeatherInfo(weather);
                        } else {
                            ToastUtil.showToast("获取天气信息失败", Toast.LENGTH_SHORT);
                        }
                    }
                });

            }
        });
    }

    /**
     * 处理天气数据，显示天气信息
     *
     * @param weather 传入的天气实体类
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动建议" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

    }
}
