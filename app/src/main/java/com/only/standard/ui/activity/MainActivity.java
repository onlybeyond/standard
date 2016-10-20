package com.only.standard.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.only.coreksdk.modle.ServerResponseBean;
import com.only.coreksdk.network.ApiConfig;
import com.only.coreksdk.network.FileBean;
import com.only.coreksdk.network.FileType;
import com.only.coreksdk.network.RxHelp;
import com.only.coreksdk.network.downFile.DownSingleFile;
import com.only.coreksdk.network.downFile.DownSingleFileListener;
import com.only.coreksdk.network.uploadFile.UploadListener;
import com.only.coreksdk.network.uploadFile.UploadSingleFile;
import com.only.standard.BuildConfig;
import com.only.standard.R;
import com.only.standard.network.ApiService;
import com.only.standard.network.RetrofitHelper;
import com.only.standard.ui.adapter.TestAdapter;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.only.coreksdk.utils.LogUtils.LOGD;
import static com.only.coreksdk.utils.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity implements RxHelp.IResponse, UploadListener, DownSingleFileListener {
    public static String CLASS_NAME = MainActivity.class.getSimpleName();
    private static String TAG = makeLogTag(MainActivity.class);
    @Bind(R.id.tv_request)
    TextView tvRequest;
    @Bind(R.id.tv_down)
    TextView tvDown;
    @Bind(R.id.tv_upload)
    TextView tvUpload;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.rv_test)
    RecyclerView rvTest;

@Override
    public void initView() {
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
        RecyclerView rvTest = (RecyclerView) findViewById(R.id.rv_test);
        rvTest.setLayoutManager(new LinearLayoutManager(this));
        rvTest.setAdapter(new TestAdapter(this));


    }

        @Override
    public void fillDate() {

    }

        @Override
    public void requestData() {
        super.requestData();
        ApiService service = RetrofitHelper.getService(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("platform", "android");//添加设备类型
        params.put("version", BuildConfig.VERSION_NAME);
        params.put(ApiConfig.API_FROM, CLASS_NAME);
        RxHelp rxHelp = new RxHelp(service.checkVersion(params), ApiConfig.API_CHECK_VERSION, this);
        rxHelp.request();
    }

    @Override
    public void response(ServerResponseBean t) {
        //这里的result 是返回的字符串,考虑到如果返回不同的对象有多个请求就的有多个不同的回调,所以采用了通用的返回对象,
        LOGD(TAG, "--- server response " + t.results);
        if (TextUtils.isEmpty(t.error)) {
            StringBuilder builder = new StringBuilder();
            builder.append("request api:" + t.apiName + "\n");
            builder.append("result:" + t.results);
            tvContent.setText(builder);
        } else {
//            showToast(t.error);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (data != null) {
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);

                if (cursor.moveToFirst()) {
                    String photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    LOGD("test", "---file path" + photo_path);
                    UploadSingleFile uploadSingleFile = new UploadSingleFile();
                    uploadSingleFile.uploadFile(this, photo_path, this);
                }
                cursor.close();
            }
        }
    }

    @Override
    public void uploadFinish(FileBean fileBean) {
        LOGD("test", "---filepath" + fileBean.filePath + "file url" + fileBean.fileUrl + "is success" + fileBean.status);
    }

    @Override
    public void uploadProgress(FileBean fileBean) {
        LOGD("test", "---filepath" + fileBean.filePath + "---progress" + fileBean.progress);
        tvContent.setText("---progress" + fileBean.progress);

    }

    @Override
    public void downSingleFileProgress(FileBean fileBean) {
        LOGD("test", "---fileUrl" + fileBean.fileUrl + "---progress" + fileBean.progress);
        tvContent.setText("---progress" + fileBean.progress);

    }

    @Override
    public void downSingleFileComplete(FileBean fileBean) {
        LOGD("test", "---fileUrl" + fileBean.fileUrl + "---status" + fileBean.status);
        tvContent.setText("文件下载完成,可在相册查看");
    }



    @OnClick({R.id.tv_request, R.id.tv_down, R.id.tv_upload, R.id.tv_content, R.id.rv_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_request:
                break;
            case R.id.tv_down:
                String fileUrl = "http://7xpffz.com1.z0.glb.clouddn.com/qrcode_bg.png";
                DownSingleFile downSingleFile = new DownSingleFile(MainActivity.this);
                downSingleFile.downFile(fileUrl, MainActivity.this);
                downSingleFile.setFileType(FileType.IMAGE);
                break;
            case R.id.tv_upload:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 100);
                break;

        }

    }
}
