package com.only.standard.ui.activity;

import android.text.TextUtils;
import android.widget.TextView;

import com.only.coreksdk.modle.ServerResponseBean;
import com.only.coreksdk.network.ApiConfig;
import com.only.coreksdk.network.RxHelp;
import com.only.standard.BuildConfig;
import com.only.standard.R;
import com.only.standard.network.ApiService;
import com.only.standard.network.RetrofitHelper;

import java.util.HashMap;
import static com.only.coreksdk.utils.LogUtils.*;

public class MainActivity extends BaseActivity implements RxHelp.IResponse {
    public static String CLASS_NAME=MainActivity.class.getSimpleName();
    private static String TAG=makeLogTag(MainActivity.class);

    //view
    private TextView tvContent;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        tvContent = (TextView) findViewById(R.id.tv_content);



    }

    @Override
    public void fillDate() {

    }

    @Override
    public void requestData() {
        super.requestData();
        ApiService service = RetrofitHelper.getService();
        HashMap<String,String> params=new HashMap<>();
        params.put("platform", "android");//添加设备类型
        params.put("version", BuildConfig.VERSION_NAME);
        params.put(ApiConfig.API_FROM, CLASS_NAME);
        RxHelp rxHelp = new RxHelp(service.checkVersion(params),ApiConfig.API_CHECK_VERSION,this);
        rxHelp.request();
    }

    @Override
    public void response(ServerResponseBean t) {
        //这里的result 是返回的字符串,考虑到如果返回不同的对象有多个请求就的有多个不同的回调,所以采用了通用的返回对象,
        LOGD(TAG,"--- server response "+t.results);
       if(TextUtils.isEmpty(t.error)) {
           StringBuilder builder = new StringBuilder();
           builder.append("request api:"+t.apiName+"\n");
           builder.append("result:"+t.results);
           tvContent.setText(builder);
       }else {
           showToast(t.error);
       }

    }
}
