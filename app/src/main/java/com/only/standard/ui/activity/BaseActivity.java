package com.only.standard.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.only.coreksdk.modle.ServerResponseBean;
import com.only.coreksdk.network.RxHelp;
import com.only.coreksdk.utils.NetworkUtils;
import com.only.standard.R;
import com.only.standard.network.Api;

import static com.only.coreksdk.utils.LogUtils.*;


/**
 * Created by only on 16/6/23.
 * Email: onlybeyond99@gmail.com
 */

public abstract class BaseActivity extends AppCompatActivity  implements RxHelp.IResponse {

    private static String TAG=makeLogTag(BaseActivity.class);

    //state
    private boolean isInitTop=true;// is init toolbar
    private boolean isHandlerNetworkError = true;//是否在基类处理网络异常


    private boolean isRegisterEvent;

    //data
    private long requestStartingTime;//record request data start time


    //view
    protected Toolbar toolbar;


    public void setInitTop(boolean initTop) {
        isInitTop = initTop;
    }

    public void setRegisterEvent(boolean registerEvent) {
        isRegisterEvent = registerEvent;
    }

    public void setHandlerNetworkError(boolean handlerNetworkError) {
        isHandlerNetworkError = handlerNetworkError;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            restore(savedInstanceState);
        } else {
            initData();
        }
        initView();
        initTop();
        fillDate();
        requestData();
    }

    /**
     * restore data from savedInstanceState
     */
    public void restore(Bundle savedInstanceState) {
    }



    /**
     * init  get query from other page
     */
    public void initData() {

    }



    /**
     * find view from layout and set listener
     */
    public abstract void initView();

    public  void initTop(){
        if(isInitTop){
            toolbar=(Toolbar)findViewById(R.id.toolbar);
            if(toolbar!=null) {
                toolbar.setNavigationIcon(R.mipmap.arrow_back);
                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        }

    }
    /**
     * init data
     */
    public abstract void fillDate();

    /**
     * network request
     */
     public void requestData(){
         if (!NetworkUtils.isAvailable(this)) {
             showToast(getString(R.string.network_bed));
         }
         requestStartingTime=System.currentTimeMillis();
     };

    /**
     * 页面中有网络请求时，返回数据在此，该serverResponseBean.result只包括ret=200的情况，
     * 如果子类需要自己处理其它情况,可以设置isHandlerNetworkError属性
     * * result为JsonObject类型
     *
     * @param serverResponseBean
     */
    public abstract void returnData(ServerResponseBean serverResponseBean);



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /********************************
     * jump to other activity
     *******************************************/
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    public void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    public void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 显示TOAST
     */
    public void showToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * 显示TOAST
     */
    public void showToast(final int resId) {
        if (resId > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void response(ServerResponseBean serverResponseBean) {
        if (isHandlerNetworkError) {
            if (TextUtils.isEmpty(serverResponseBean.error)) {
                JsonObject results = serverResponseBean.results;
                if (results != null) {
                    String ret = results.get("ret").getAsString();
                    if (Api.ARG_RET_NUM.equals(ret)) {

                        returnData(serverResponseBean);
                        LOGD(TAG, "---");
                    } else {
                        String message = results.get(Api.ARG_MESSAGE).getAsString();
                        if (!TextUtils.isEmpty(message)) {
                            showToast(message);
                        }
                    }
                } else {

                }
            } else {
                showToast(serverResponseBean.error);
            }
        } else {
            returnData(serverResponseBean);
        }

    }





}
