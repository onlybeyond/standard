package com.only.coreksdk.modle;

import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * Created by only on 16/6/15.
 * Email: onlybeyond99@gmail.com
 */
public class ServerResponseBean {


    public String error;
    public JsonObject results;
    public String apiName;
    public String apiFrom;//没有设置时为null
    public int retCode;//返回的码-500表示无网络连接
    public HashMap<String,String>params; //没有设置时为null




}
