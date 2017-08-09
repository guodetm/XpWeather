package com.example.xpweather.util;

import android.text.TextUtils;

import com.example.xpweather.dbModel.CityModel;
import com.example.xpweather.dbModel.CountryModel;
import com.example.xpweather.dbModel.ProvinceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 肖磊 on 2017/8/7.
 */

public class Utility {
    /**
     * @param response 接收到的省级数据
     * @return 如果传递的字符串为空则返回false，否则返回true
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0; i < allProvince.length(); i++) {
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    ProvinceModel province = new ProvinceModel();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 接收并处理服务器返回的市级数据
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    CityModel city = new CityModel();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.setCityName(cityObject.getString("name"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**接收并处理返回的县级数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountryResponse(String response, int cityId) {
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountry = new JSONArray(response);
                for (int i = 0;i<allCountry.length();i++){
                    JSONObject cityObject = allCountry.getJSONObject(i);
                    CountryModel country = new CountryModel();
                    country.setCityId(cityId);
                    country.setCountyName(cityObject.getString("name"));
                    country.setId(cityObject.getInt("id"));
                    country.setWeatherId(cityObject.getString("weather_id"));
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;


    }

}
