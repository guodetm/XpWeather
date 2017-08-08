package com.example.xpweather.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xpweather.R;


/**
 *Created by 肖磊 on 2017/7/28.
 * 自定义的Toolbar在Layout里直接使用
 */
public class CustomToolBar extends LinearLayout {


    //左边按钮是否可见，是否设置图片,左边按钮默认的图片为返回箭头
    private Boolean isLeftBtnVisible;
    private int leftResId;

    //左边文字是否可见，设置左边文字的内容
    private Boolean isLeftTvVisible;
    private String leftTvText;

    //右边按钮是否可见，是否设置图片,右边按钮默认没有图片
    private Boolean isRightBtnVisible;
    private int rightResId;

    //右边文字是否可见，设置右边文字的内容
    private Boolean isRightTvVisible;
    private String rightTvText;

    //标题栏是否可见，标题栏的文字信息
    private Boolean isTitleVisible;
    private String titleText;

    //设置ToolBar背景色
    private int backgroundResId;

    public CustomToolBar(Context context) {
        this(context, null);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }


    /**
     * 初始化属性
     *
     * @param attrs
     */
    public void initView(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomToolBar);
        /**-------------获取左边按钮属性------------*/
        isLeftBtnVisible = typedArray.getBoolean(R.styleable.CustomToolBar_left_btn_visible, false);
        leftResId = typedArray.getResourceId(R.styleable.CustomToolBar_left_btn_src, -1);
        /**-------------获取左边文本属性------------*/
        isLeftTvVisible = typedArray.getBoolean(R.styleable.CustomToolBar_left_tv_visible, false);
        if (typedArray.hasValue(R.styleable.CustomToolBar_left_tv_text)) {
            leftTvText = typedArray.getString(R.styleable.CustomToolBar_left_tv_text);
        }
        /**-------------获取右边按钮属性------------*/
        isRightBtnVisible = typedArray.getBoolean(R.styleable.CustomToolBar_right_btn_visible, false);
        rightResId = typedArray.getResourceId(R.styleable.CustomToolBar_right_btn_src, -1);
        /**-------------获取右边文本属性------------*/
        isRightTvVisible = typedArray.getBoolean(R.styleable.CustomToolBar_right_tv_visible, false);
        if (typedArray.hasValue(R.styleable.CustomToolBar_right_tv_text)) {
            rightTvText = typedArray.getString(R.styleable.CustomToolBar_right_tv_text);
        }
        /**-------------获取标题属性------------*/
        isTitleVisible = typedArray.getBoolean(R.styleable.CustomToolBar_title_visible, false);
        if (typedArray.hasValue(R.styleable.CustomToolBar_title_text)) {
            titleText = typedArray.getString(R.styleable.CustomToolBar_title_text);
        }
        /**-------------背景颜色------------*/
        backgroundResId = typedArray.getResourceId(R.styleable.CustomToolBar_barBackground, -1);

        typedArray.recycle();

        /**-------------设置内容------------*/
        View barLayoutView = View.inflate(getContext(), R.layout.layout_common_toolbar, null);
        Button leftBtn = (Button) barLayoutView.findViewById(R.id.toolbar_left_btn);
        TextView leftTv = (TextView) barLayoutView.findViewById(R.id.toolbar_left_tv);
        TextView titleTv = (TextView) barLayoutView.findViewById(R.id.toolbar_title_tv);
        Button rightBtn = (Button) barLayoutView.findViewById(R.id.toolbar_right_btn);
        TextView rightTv = (TextView) barLayoutView.findViewById(R.id.toolbar_right_tv);
        RelativeLayout barRlyt = (RelativeLayout) barLayoutView.findViewById(R.id.toolbar_content_rlyt);

        leftTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "功能等待完善", Toast.LENGTH_SHORT).show();
            }
        });
        rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "功能等待完善", Toast.LENGTH_SHORT).show();
            }
        });
        if (isLeftBtnVisible) {
            leftBtn.setVisibility(VISIBLE);
        }
        if (isLeftTvVisible) {
            leftTv.setVisibility(VISIBLE);
        }
        if (isRightBtnVisible) {
            rightBtn.setVisibility(VISIBLE);
        }
        if (isRightTvVisible) {
            rightTv.setVisibility(VISIBLE);
        }
        if (isTitleVisible) {
            titleTv.setVisibility(VISIBLE);
        }
        leftTv.setText(leftTvText);
        rightTv.setText(rightTvText);
        titleTv.setText(titleText);

        if (leftResId != -1) {
            leftBtn.setBackgroundResource(leftResId);
        }
        if (rightResId != -1) {
            rightBtn.setBackgroundResource(rightResId);
        }
        if (backgroundResId != -1) {
//            barRlyt.setBackgroundColor(getResources().getColor(R.color.bg_toolbar));
            barRlyt.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_toolbar));
        }
        //将设置完成之后的View添加到此LinearLayout中
        addView(barLayoutView, 0);
    }
}
