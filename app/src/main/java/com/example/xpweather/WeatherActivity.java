package com.example.xpweather;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.xpweather.gson.Forecast;
import com.example.xpweather.gson.Weather;
import com.example.xpweather.service.AutoUpdateService;
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

    public DrawerLayout drawerLayout;

    private Button navButton;

    public SwipeRefreshLayout swipeRefresh;

    private ImageView bingPicImg;

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
        if (Build.VERSION.SDK_INT >= 21) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_weather);
        //初始化控件
        initView();

        //设置navButton点击事件
        setNavBtn();

        //加载背景图片
        loadImg();

        //判断缓存只是否有天气信息,并更具缓存情况来更新天气数据
        loadWeather();

    }


    /**
     * 设置NavBtn点击事件
     */
    private void setNavBtn() {
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据缓存加载天气信息
     */
    private void loadWeather() {
        String weatherString = SPUtil.getInstance().getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            //有缓存时候解析天气数据
            Log.e(TAG, "有缓存解析天气数据");
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存时查询天气信息
            Log.e(TAG, "没有缓存解析天气数据");
            weatherId = getIntent().getStringExtra("weather_id");
            Log.e(TAG, "weatherId" + weatherId);
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String id = SPUtil.getInstance().getString("weather_id");
//                requestWeather(weatherId);
                requestWeather(id);
            }
        });
    }

    /**
     * 加载必应每日一图
     */
    private void loadImg() {
        String bingPic = SPUtil.getInstance().getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            String requestBingPic = "http://guolin.tech/api/bing_pic";
            HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String bingPic = response.body().string();
                    SPUtil.getInstance().putString("bing_pic", bingPic);
                    Log.e(TAG,"bingPic："+bingPic);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                        }
                    });
                }
            });
        }
    }

    /**
     * 获取天气信息
     *
     * @param weatherId 查询城市的天气Id
     */
    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                "&key=da3f297db73c47abb9f7ae0ec149f0de";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast("获取天气信息失败", Toast.LENGTH_SHORT);
                swipeRefresh.setRefreshing(false);
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
                            Log.e(TAG,responseText);
                            SPUtil.getInstance().putString("weather", responseText);
                            SPUtil.getInstance().putString("weather_id",weather.basic.weatherId);
                            showWeatherInfo(weather);
                        } else {
                            ToastUtil.showToast("获取天气信息失败", Toast.LENGTH_SHORT);
                        }
                        swipeRefresh.setRefreshing(false);
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
        if (weather != null&"ok".equals(weather.status)){
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
            //启动服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else{
            ToastUtil.showToast("获取天气信息失败");
        }

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

        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navButton = (Button) findViewById(R.id.nav_button);
    }
}
