package com.only.standard.network;


import android.content.Context;
import android.text.TextUtils;

import com.only.coreksdk.network.HttpLoggingInterceptor;
import com.only.coreksdk.network.OkHttpUtils;
import com.only.coreksdk.utils.LogUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by only on 16/6/14.
 */
public class RetrofitHelper {

    private static String TAG= LogUtils.makeLogTag(RetrofitHelper.class);
    static {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                String name = Thread.currentThread().getName();

                LogUtils.LOGD(TAG,"---thread name"+name);
            }
        });
//        OkHttpUtils.setOkHttpClient(new OkHttpClient.Builder().addInterceptor(interceptor).build());
//        okHttpClient=OkHttpUtils.getOkHttpClient();

    }


    /**
     * get
     * @return
     */
public static ApiService getService(Context context){
   return   getService(context,"");
 }

    /**
     *
     * @param baseUrl 请求服务器,项目中很多情况会用到不同的主机,例如有时存储文件使用七牛的服务器,存储用户
     *                信息使用自己的服务器。
     * @return
     */
    public static ApiService getService(Context context,String baseUrl){
        Retrofit retrofit = getRetrofit(context,baseUrl);
        return retrofit.create(ApiService.class);
    }


    /**
     * create retrofit object
     * @return
     */
  private static Retrofit getRetrofit(Context context,String baseUrl){
      if(TextUtils.isEmpty(baseUrl)){
         baseUrl=Api.API_HOST;
      }
     OkHttpClient okHttpClient=OkHttpUtils.getOkHttpClient(context);
     Retrofit retrofit=new Retrofit.Builder()
             .baseUrl(baseUrl)
             .client(okHttpClient)
             .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
             .build();
      return retrofit;
    }
}
