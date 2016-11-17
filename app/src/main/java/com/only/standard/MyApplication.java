package com.only.standard;

import android.app.Application;
import android.content.Context;


import com.only.coreksdk.network.OkHttpUtils;

import java.util.HashMap;

/**
 * Created by only on 16/11/1.
 * Email: onlybeyond99@gmail.com
 */

public class MyApplication extends Application {

    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();

        //设置请求公共参数
        HashMap<String, String> commonRequestParams = new HashMap<>();
        commonRequestParams.put("platform", "android");//添加设备类型
        commonRequestParams.put("version", BuildConfig.VERSION_NAME);
        OkHttpUtils.init(this, commonRequestParams);
    }
}
