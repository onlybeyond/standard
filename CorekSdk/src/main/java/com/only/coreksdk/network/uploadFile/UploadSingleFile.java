package com.only.coreksdk.network.uploadFile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.only.coreksdk.network.FileBean;
import com.only.coreksdk.network.FileStatus;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import static com.only.coreksdk.utils.LogUtils.*;


/**
 * Created by only on 16/10/14.
 * Email: onlybeyond99@gmail.com
 */

public class UploadSingleFile {

    private static String TAG = makeLogTag(UploadSingleFile.class);

    private UploadListener mUploadListener;
    private Handler mHandler;//回调主线程
    //    private String mFilePath;//上传文件的路径
//    private String mSign;//上传文件的标识
    private FileBean mFileBean;

    public UploadSingleFile() {
        mFileBean = new FileBean();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置文件的标识
     *
     * @param mSign
     */
    public void setSign(String mSign) {
        mFileBean.sign = mSign;
    }


    public void uploadFile(Context context, String filePath, UploadListener uploadFinishListener) {
        mUploadListener = uploadFinishListener;
        mFileBean.filePath = filePath;
        CloudManager.init(context);
        UploadFileTask uploadFileTask = new UploadFileTask();
        uploadFileTask.execute();
    }

    //上传文件的异步任务
    public class UploadFileTask extends AsyncTask<String, Integer, Object> implements UpCompletionHandler {
        public UploadFileTask() {

        }

        @Override
        protected Object doInBackground(String... params) {
            //七牛云上传
            LOGD(TAG,"--upload start file"+mFileBean.fileUrl);

            CloudManager.getInstance().uploadImage(mFileBean.filePath,
                    this,
                    new UploadOptions(null, null, false, new UploadProgressCallback(mFileBean.sign, mFileBean.filePath),
                            null));


            return null;
        }

        @Override
        public void complete(String s, ResponseInfo info, JSONObject response) {
            LOGD(TAG, "Upload success: key = " + s + ", rep = " + response);
            String key = null;
            String hash = null;
            if (mUploadListener != null) {
                if (info.isOK()) {
                    try {
                        key = response.getString("key");
                        hash = response.getString("hash");
                    } catch (final JSONException e) {
                        e.printStackTrace();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                LOGD(TAG, "---thread name" + Thread.currentThread().getName());
                                mFileBean.status = FileStatus.UPLOAD_FAIL;
                                mFileBean.error = e.getMessage();
                                mUploadListener.uploadFinish(mFileBean);
                            }
                        });
                    }

                    Pair<String, String> pair = new Pair<>(key, hash);

                    final String url = CloudManager.generateRemoteUri(pair.first);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LOGD(TAG, "---thread name" + Thread.currentThread().getName());
                            mFileBean.status = FileStatus.UPLOAD_SUCCESS;
                            mFileBean.fileUrl = url;
                            mUploadListener.uploadFinish(mFileBean);

                        }
                    });


                } else {
                    LOGD(TAG, "---info error" + info.error);
                    final String error = info.error;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            LOGD(TAG, "---thread name" + Thread.currentThread().getName());
                            mFileBean.status = FileStatus.UPLOAD_FAIL;
                            mFileBean.error = error;

                            mUploadListener.uploadFinish(mFileBean);
                        }
                    });
                }
            }

        }
    }


    public class UploadProgressCallback implements UpProgressHandler {
        private String sign;//标记
        private String path;//文件的 url

        public UploadProgressCallback(String sign, String path) {
            this.sign = sign;
            this.path = path;
        }

        @Override
        public void progress(String key, double percent) {

            if (mUploadListener != null) {
                mFileBean.status = FileStatus.UPLOADING;
                mFileBean.progress = percent;
                mUploadListener.uploadProgress(mFileBean);
            }
            LOGD(TAG,"--upload file"+mFileBean.fileUrl+"--progress"+percent);


        }
    }


}
