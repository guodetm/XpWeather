package com.example.xpweather.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.xpweather.R;

/**
 * Created by 肖磊 on 2017/8/7.
 */

public class ChooseAreaFragment extends Fragment{
    public static final int LEVEL_PROVINCE = 0;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,null);
        return view;
    }
}
