package com.only.standard.network;

import com.only.coreksdk.network.ApiConfig;

import java.util.HashMap;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by only on 16/6/14.
 */
public interface ApiService<T> {





    @POST(ApiConfig.API_CHECK_VERSION)
    @FormUrlEncoded
    Observable<ResponseBody>  checkVersion(@FieldMap HashMap<String,String> params);





}
