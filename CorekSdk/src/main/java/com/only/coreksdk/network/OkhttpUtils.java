package com.only.coreksdk.network;

import android.content.Context;


import com.only.coreksdk.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.only.coreksdk.utils.LogUtils.*;


/**
 * Created by only on 16/10/19.
 * Email: onlybeyond99@gmail.com
 * 保证全局只有一个设置相同的OkHttpClient
 */

public class OkHttpUtils {
    private static String TAG = makeLogTag(OkHttpUtils.class);

    private static OkHttpClient mOkHttpClient;

    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }

    public static void init(Context context, HashMap<String, String> commonRequestParams) {
        if (mOkHttpClient == null) {
            BasicParamsInterceptor basicParamsInterceptor = null;
            if (commonRequestParams != null) {
                basicParamsInterceptor = new BasicParamsInterceptor.Builder()
                        .addParamsMap(commonRequestParams).build();
            }
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    String name = Thread.currentThread().getName();

                    LOGD(TAG, "---thread name" + name);
                }
            });
            File httpCacheFile = new File(context.getExternalCacheDir() + File.separator + "xiaoluCache");
            LOGD(TAG, "---http cache file path" + httpCacheFile.getAbsolutePath());
//            Cache cache = new Cache(httpCacheFile, Config.HTTP_CACHE_SIZE);
            if (basicParamsInterceptor == null) {
                basicParamsInterceptor = new BasicParamsInterceptor.Builder().build();
            }
                mOkHttpClient = new OkHttpClient.Builder()
                        .readTimeout(10000L, TimeUnit.MILLISECONDS)
                        .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                        .addInterceptor(basicParamsInterceptor)
                        .addInterceptor(interceptor)
                        .build();
        }
    }

    public static OkHttpClient getOkHttpClient(Context context) {
        if (mOkHttpClient == null) {
            //最基本的设置
            init(context, null);
        }
        return mOkHttpClient;
    }

    public static Interceptor getNetWorkInterceptor(final Context context) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                if (NetworkUtils.isAvailable(context)) {
                    int maxAge = 60;
                    // 有网络时 设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    // 无网络时，设置超时为1周
                    int maxStale = 60 * 60 * 24 * 7;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
    }

    public static Interceptor getInterceptor(final Context context) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!NetworkUtils.isAvailable(context)) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }


}
