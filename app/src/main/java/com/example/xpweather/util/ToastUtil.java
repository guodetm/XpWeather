package com.kingeid.campusguardian.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.kingeid.campusguardian.App;

/**
 * Created by joker on 2017/7/12.
 */

public class ToastUtil {
    private static Toast toast;

    public static void showToast(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        showToast(content, Toast.LENGTH_SHORT);
    }

    public static void showToast(String content, int duration) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(App.getAppContext(), content, duration);
        } else {
            toast.setText(content);
        }

        toast.show();
    }
}
