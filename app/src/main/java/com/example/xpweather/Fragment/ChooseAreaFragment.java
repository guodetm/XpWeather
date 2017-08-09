package com.example.xpweather.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.xpweather.R;
import com.example.xpweather.dbModel.CityModel;
import com.example.xpweather.dbModel.CountryModel;
import com.example.xpweather.dbModel.ProvinceModel;
import com.example.xpweather.util.HttpUtil;
import com.example.xpweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 肖磊 on 2017/8/7.
 */

public class ChooseAreaFragment extends Fragment {
    private  static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;


    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<>();

    private Button backBtn;
    private TextView titleTxt;
    private ProgressDialog progressDialog;

    /**
     * 省、市、县列表
     */
    private List<ProvinceModel> provinceList;
    private List<CityModel> cityList;
    private List<CountryModel> countryList;
    /**
     * 被选中的省、市、县
     */
    private ProvinceModel selectedProvince;
    private CityModel selectedCity;
    private CountryModel selectedCountry;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, null);
        backBtn = (Button) view.findViewById(R.id.toolbar_left_btn);
        titleTxt = (TextView) view.findViewById(R.id.toolbar_title_tv);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 遍历所有省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleTxt.setText("中国");
        backBtn.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(ProvinceModel.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (ProvinceModel province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryFromServer(String address, final String type) {
        Log.e(TAG,"请求的地址为"+address);
        showProgreDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            /**网络请求失败时回调
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            /**网络请求成功时回调
             * @param call
             * @param response 网络请求成功时返回的数据
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    Log.e(TAG,"请求的json="+responseText);
                    Log.e(TAG,"请求的ProvinceId="+selectedProvince.getId());
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("country".equals(type)) {
                    result = Utility.handleCountryResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 关闭进度条
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 显示进度条
     */
    private void showProgreDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 遍历当前市下的所有县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleTxt.setText(selectedCity.getCityName());
//        backBtn.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getId())).find(CountryModel.class);
        if (countryList.size() > 0) {
            dataList.clear();
            for (CountryModel country : countryList) {
                dataList.add(country.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode +"/"+ cityCode;
            queryFromServer(address, "country");
        }
    }

    /**
     * 遍历当前省下的所有市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleTxt.setText(selectedProvince.getProvinceName());
        backBtn.setVisibility(View.VISIBLE);
        //查询当前CityModel表下provinceId等于当前选择的省级Id的所有市级数据，
        //等价于select * from CityModel where provinceId = selectedProvince.getId();
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getId())).find(CityModel.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (CityModel city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
}
