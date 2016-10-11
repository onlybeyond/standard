package com.only.coreksdk.network;


import android.text.TextUtils;
import android.util.Log;

import com.only.coreksdk.modle.ServerResponseBean;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.only.coreksdk.utils.LogUtils.*;

/**
 * Created by only on 16/6/24.
 * Email: onlybeyond99@gmail.com
 */
public class RxHelp {

    private static String TAG = RxHelp.class.getSimpleName();
    Observable<ResponseBody> mObservable;
    private IResponse mIResponse;
    private HashMap<String,String> mRequestParams;//请求参数
    private String mApiName;//请求的api 可以用于区别同一个页面中的不同请求返回
    private String mApiFrom;//多个Fragment可能用到同一个接口,方便出错时找到问题

    public RxHelp( Observable<ResponseBody> observable,String apiName,IResponse response) {
        this(observable,apiName,null,response);

    }

    public RxHelp( Observable<ResponseBody> observable,String apiName,HashMap<String,String> requestParams ,IResponse response) {
        this(observable,apiName,"",requestParams,response);

    }

    public RxHelp( Observable<ResponseBody> observable,String apiName,String apiFrom,HashMap<String,String> requestParams ,IResponse response) {
        this.mObservable = observable;
        this.mIResponse = response;
        this.mRequestParams=requestParams;
        this.mApiFrom=apiFrom;
        this.mApiName=apiName;

    }


    public interface IResponse {
        void response(ServerResponseBean t);
    }

    public void request() {
        mObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer <ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ServerResponseBean serverResponseBean=new ServerResponseBean();
                        serverResponseBean.error=e.getMessage();
                        LOGD(TAG, "--- network error" + e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        ServerResponseBean serverResponseBean=new ServerResponseBean();
                        serverResponseBean.apiName=mApiName;
                        if(mRequestParams!=null){
                            serverResponseBean.params=mRequestParams;
                        }
                        if(!TextUtils.isEmpty(mApiFrom)){
                            serverResponseBean.apiFrom=mApiFrom;
                        }
                        try {
                            serverResponseBean.results=responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            serverResponseBean.error=e.getMessage();
                        }

                        mIResponse.response(serverResponseBean);

                    }
                });
    }

}
