package com.only.coreksdk.network;


import android.text.TextUtils;

import com.google.gson.JsonParser;
import com.only.coreksdk.modle.ServerResponseBean;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;


import static com.only.coreksdk.utils.LogUtils.LOGD;
import static com.only.coreksdk.utils.LogUtils.LOGE;


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
        void response(ServerResponseBean serverResponseBean);
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
                        if(mRequestParams!=null){
                            serverResponseBean.params=mRequestParams;
                        }
                        if(!TextUtils.isEmpty(mApiFrom)){
                            serverResponseBean.apiFrom=mApiFrom;
                        }
                        serverResponseBean.error=e.getMessage();
                        if(e instanceof HttpException){
                            HttpException httpException=(HttpException)e;
                            serverResponseBean.retCode= httpException.code();
                            LOGE(TAG, "--api network http error" + e.getMessage()+"---error code"+serverResponseBean.retCode);


                        }else if(e instanceof ConnectException) {
                            //网络异常
                            serverResponseBean.retCode=-500;
                            LOGE(TAG, "--api network http error" + e.getMessage()+"---error code"+serverResponseBean.retCode);

                        }
                        mIResponse.response(serverResponseBean);
                        //这里的error不只是网络请求的error,还有可能是onNext中的错误,例如在onNext中JSON转换错误
                        LOGE(TAG, "--api  error" + e.getMessage()+"---error code"+serverResponseBean.retCode );
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        ServerResponseBean serverResponseBean=new ServerResponseBean();
                        serverResponseBean.apiName=mApiName;

                        String results="";

                        if(mRequestParams!=null){
                            serverResponseBean.params=mRequestParams;
                        }
                        if(!TextUtils.isEmpty(mApiFrom)){
                            serverResponseBean.apiFrom=mApiFrom;
                        }
                        try {
                             results=responseBody.string();

                            JsonParser jsonParser=new JsonParser();
                            serverResponseBean.results = jsonParser.parse(results).getAsJsonObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                            serverResponseBean.error=e.getMessage();
                        }
                       LOGD(TAG,"--api name"+mApiName+"--result"+results+
                               "--error"+serverResponseBean.error);
                        mIResponse.response(serverResponseBean);

                    }
                });
    }

}
