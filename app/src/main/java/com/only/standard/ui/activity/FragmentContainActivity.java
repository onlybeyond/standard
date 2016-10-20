package com.only.standard.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.only.standard.R;


/**
 * Created by only on 16/9/7.
 * Email: onlybeyond99@gmail.com
 */
public class FragmentContainActivity extends BaseActivity {

    public static String CLASS_NAME=FragmentContainActivity.class.getSimpleName();

    private String mResult;
    private String mTitle;
    private String fragmentType;
    private TextView tvTitle;

    @Override
    public void initData() {
        super.initData();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fragmentType = extras.getString("fragmentType");
            mResult = extras.getString("result");

        }
    }

    @Override
    public void initTop() {
        super.initTop();
        tvTitle = (TextView) findViewById(R.id.toolbar_title);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_contain_fragment);
        LinearLayout llFragment = (LinearLayout) findViewById(R.id.ll_fragment);
        ImageView ivBg=(ImageView)findViewById(R.id.iv_bg);


    }

    @Override
    public void fillDate() {


        if(!TextUtils.isEmpty(fragmentType)) {
            if (fragmentType.equals("")) {
              //这可以为不同的fragment 填值
            }
        }else {
            //加载默认的fragment


        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
