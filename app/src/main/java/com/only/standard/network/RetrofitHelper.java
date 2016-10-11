package com.only.standard.network;


import com.only.coreksdk.network.ApiConfig;
import com.only.coreksdk.network.HttpLoggingInterceptor;
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
    private static OkHttpClient okHttpClient;
    static {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                String name = Thread.currentThread().getName();

                LogUtils.LOGD(TAG,"---thread name"+name);
            }
        });
        okHttpClient=new OkHttpClient.Builder().addInterceptor(interceptor).build();

    }



    /**
     * get
     * @return
     */
 public static ApiService getService(){
     Retrofit retrofit = getRetrofit();

     return retrofit.create(ApiService.class);
 }

    /**
     * create retrofit object
     * @return
     */
  private static Retrofit getRetrofit(){
     Retrofit retrofit=new Retrofit.Builder()
             .baseUrl(ApiConfig.API_HOST)
             .client(okHttpClient)
             .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
             .build();
      return retrofit;
    }
}
