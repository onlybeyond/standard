package com.only.standard.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;


import com.only.coreksdk.modle.ServerResponseBean;
import com.only.coreksdk.network.FileBean;
import com.only.coreksdk.network.FilePossession;
import com.only.coreksdk.network.FileType;
import com.only.coreksdk.network.RxHelp;
import com.only.coreksdk.network.downFile.DownFileBean;
import com.only.coreksdk.network.downFile.DownFileListener;
import com.only.coreksdk.network.downFile.DownFileManager;
import com.only.coreksdk.network.downFile.DownMode;
import com.only.coreksdk.network.uploadFile.UploadListener;
import com.only.coreksdk.network.uploadFile.UploadSingleFile;
import com.only.standard.BuildConfig;
import com.only.standard.R;
import com.only.standard.network.Api;
import com.only.standard.network.ApiService;
import com.only.standard.network.RetrofitHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.only.coreksdk.utils.LogUtils.*;


public class UploadTestActivity extends BaseActivity implements RxHelp.IResponse, UploadListener, DownFileListener {
    public static String CLASS_NAME = UploadTestActivity.class.getSimpleName();
    private static String TAG = makeLogTag(UploadTestActivity.class);
    @BindView(R.id.tv_request)
    TextView tvRequest;
    @BindView(R.id.tv_down)
    TextView tvDown;
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.tv_down_single)
    TextView tvDownSingle;
    @BindView(R.id.tv_down_more)
    TextView tvDownMore;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.rv_test)
    RecyclerView rvTest;

    @Override
    public void initView() {
        setContentView(R.layout.activity_upload_test);
        ButterKnife.bind(this);
        tvContent.setText("dsdsd");


    }

    @Override
    public void fillDate() {

    }

    @Override
    public void requestData() {
        super.requestData();
        ApiService service = RetrofitHelper.getService(this);
        HashMap<String, String> params = new HashMap<>();
        params.put(Api.API_FROM, CLASS_NAME);
        RxHelp rxHelp = new RxHelp(service.checkVersion(params), Api.API_CHECK_VERSION, this);
        rxHelp.request();
    }

    @Override
    public void returnData(ServerResponseBean serverResponseBean) {
        tvContent.setText(serverResponseBean.results.toString());
    }

    /*@Override
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

    }*/
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



    @OnClick({R.id.tv_request, R.id.tv_down, R.id.tv_upload, R.id.tv_content, R.id.rv_test, R.id.tv_down_single, R.id.tv_down_more})
    public void onClick(View view) {
        String fileUrl = "http://7xpffz.com1.z0.glb.clouddn.com/qrcode_bg.png";
        String fileUrl2 = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1477364389&di=a8b66ccaaa6e0e9af5eebfd82e6d8e30&src=http://img.hb.aicdn.com/d2024a8a998c8d3e4ba842e40223c23dfe1026c8bbf3-OudiPA_fw580";
        String fileUrl3 = "http://img.hb.aicdn.com/761f1bce319b745e663fed957606b4b5d167b9bff70a-nfBc9N_fw658";
        switch (view.getId()) {

            case R.id.tv_request:
                break;
            case R.id.tv_down:
                DownFileManager downFileManager = new DownFileManager(this);

                FileBean fileBean = new FileBean();
                fileBean.fileUrl = fileUrl;
                fileBean.fileType = FileType.IMAGE;

                FileBean fileBeanTwo = new FileBean();
                fileBeanTwo.fileUrl = fileUrl2;
                fileBeanTwo.fileType = FileType.IMAGE;

                FileBean fileBeanThree = new FileBean();
                fileBeanThree.fileUrl = fileUrl3;
                fileBeanThree.fileType = FileType.IMAGE;
                LinkedList<FileBean> fileBeenList = new LinkedList<>();


                fileBeenList.add(fileBean);
                fileBeenList.add(fileBeanTwo);
                fileBeenList.add(fileBeanThree);
                downFileManager.setDownTag("send one by one");
//                downFileManager.s
                downFileManager.downFiles(fileBeenList, this);
//                DownSingleFile downSingleFile = new DownSingleFile(MainActivity.this);
//                downSingleFile.downFile(fileUrl, MainActivity.this);
//                downSingleFile.setFileType(FileType.IMAGE);
                break;
            case R.id.tv_upload:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 100);
                break;

            case R.id.tv_down_single:
                FileBean fileBeanSingle = new FileBean();
                fileBeanSingle.fileUrl = fileUrl;
                fileBeanSingle.fileType = FileType.IMAGE;
                fileBeanSingle.filePossession= FilePossession.PUBLIC_FILE;
                DownFileManager downFileManagerSingle = new DownFileManager(this);
                downFileManagerSingle.setDownTag("single");
                downFileManagerSingle.downSingleFile(fileBeanSingle, this);


                break;
            case R.id.tv_down_more:
                DownFileManager downFileManagerMore = new DownFileManager(this);
                FileBean fileBeanMore = new FileBean();
                fileBeanMore.fileUrl = fileUrl;
                fileBeanMore.fileType = FileType.IMAGE;

                FileBean fileBeanMoreTwo = new FileBean();
                fileBeanMoreTwo.fileUrl = fileUrl2;
                fileBeanMoreTwo.fileType = FileType.IMAGE;
                LinkedList<FileBean> fileBeenListMore = new LinkedList<>();
                fileBeenListMore.add(fileBeanMore);
                fileBeenListMore.add(fileBeanMoreTwo);

                downFileManagerMore.setDownMode(DownMode.DOWN_ALL);
                downFileManagerMore.setDownTag("send more");
                downFileManagerMore.downFiles(fileBeenListMore, this);
                break;

        }

    }

    @Override
    public void downFileListenerProcess(DownFileBean downFileBean) {
        LOGD("test", "--progress file tag" + downFileBean.tag + "--progress" + downFileBean.progress +
                "--send file" + downFileBean.sendingCount +
                "--success file" + downFileBean.successCount + "--fail file" + downFileBean.failCount);

        if (downFileBean.downMode == DownMode.DOWN_ALL) {
            List<FileBean> fileBeenList = downFileBean.fileBeenList;
            String strText = "";
            int size = fileBeenList.size();
            for (int i = 0; i < size; i++) {
                strText += "---progress " + i + "---" + fileBeenList.get(i).progress + "\n";
            }
            tvContent.append(strText);
        } else {
            tvContent.append("sign" + downFileBean.tag + " progress:" + downFileBean.progress + "\n");
        }
    }

    @Override
    public void downFileListenerFinish(DownFileBean downFileBean) {
        LOGD("test", "---finish file tag " + downFileBean.tag + "---progress" + downFileBean.progress +
                "---send file" + downFileBean.sendingCount +
                "---success file" + downFileBean.successCount + "---fail file" + downFileBean.failCount + "error:" + downFileBean.error + "is success:" + downFileBean.isSuccess);

        tvContent.append("下载完成");
    }







}
