package com.only.coreksdk.network.downFile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.only.coreksdk.network.FileBean;
import com.only.coreksdk.network.FilePossession;
import com.only.coreksdk.network.FileStatus;
import com.only.coreksdk.network.FileType;
import com.only.coreksdk.network.OkHttpUtils;
import com.only.coreksdk.utils.FileUtils;
import com.only.coreksdk.utils.PathUtils;
import com.only.coreksdk.utils.SDCardUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.only.coreksdk.utils.ConstUtils.KB;
import static com.only.coreksdk.utils.LogUtils.*;


/**
 * Created by only on 16/10/19.
 * Email: onlybeyond99@gmail.com
 */

public class DownSingleFile {

    private static String TAG = makeLogTag(DownSingleFile.class);

    //basic data
    private FileBean mFileBean;
    private double mProgress;//下载进度





    //    private String mFileUrl;
    private Handler mHandler;
    private DownSingleFileListener mDownSingleFileListener;
    //    private FileStatus mFileStatus = FileStatus.DEFAULT;
    private int updateProgressSize = 100;//更新进度,默认是10k更新一次
    //    private String fileName;
    private Context mContext;



    /**
     * 设置文件的名字,如果没有设置则使用下载连接作为存储名字
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        mFileBean.fileName = fileName;
    }

    /**
     * 设置文件类型;图片或者默认文件
     *
     * @param fileType
     */
    public void setFileType(FileType fileType) {
        mFileBean.fileType = fileType;
    }

    /**
     * 设置文件的存放位置,公共目录或者私有目录
     */
    public void setFileProcession(FilePossession fileProcession) {
        mFileBean.filePossession = fileProcession;

    }

    public DownSingleFile(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void downFile(final FileBean fileBean, DownSingleFileListener downSingleFileListener) {

        mFileBean = fileBean;
        this.mDownSingleFileListener = downSingleFileListener;


        if (mFileBean.status == FileStatus.DEFAULT) {
            mFileBean.status=FileStatus.DOWNING;
            OkHttpClient okHttpClient = OkHttpUtils.getOkHttpClient(mContext);
            Request request = new Request.Builder().url(mFileBean.fileUrl).build();
            Call call = okHttpClient.newCall(request);
            //采用异步请求
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //下载失败
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownSingleFileListener != null) {
                                mFileBean.status = FileStatus.DOWN_FAIL;
                                mFileBean.error = e.getMessage();
                                mDownSingleFileListener.downSingleFileComplete(mFileBean);
                            }
                        }
                    });
                    LOGD(TAG, "---request fail error" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response != null) {
                        int code = response.code();
                        if (code == 200) {
                            LOGD(TAG, "---request success");

                            long contentLength = response.body().contentLength();
                            InputStream inputStream = response.body().byteStream();
                            File file = null;
                            LOGD(TAG, "---is has sd card" + SDCardUtils.isSDCardEnable());
                            if (TextUtils.isEmpty(mFileBean.fileName)) {
                                mFileBean.fileName = mFileBean.fileUrl.replaceAll("/", "").replaceAll("http:","");
                                mFileBean.fileName=mFileBean.fileName.substring(mFileBean.fileName.length()-40,mFileBean.fileName.length());//限制长度,防止文件过长导致文件创建不了。
                            }
                            //加时间戳防止一样
                            if (mFileBean.fileType == FileType.IMAGE) {
                                if (mFileBean.filePossession == FilePossession.PRIVATE_FILE) {
                                    file = new File(PathUtils.getPrivateImagePath(mContext) +File.separator+String.valueOf(System.currentTimeMillis()) +mFileBean.fileName);
                                } else {
                                    file = new File(PathUtils.getPublicImageFilePath() +File.separator+ String.valueOf(System.currentTimeMillis())+mFileBean.fileName);
                                }
                            } else {
                                if (mFileBean.filePossession == FilePossession.PRIVATE_FILE) {
                                    file = new File(PathUtils.getPrivateFilePath(mContext) +File.separator+String.valueOf(System.currentTimeMillis())+ mFileBean.fileName);

                                } else {
                                    file = new File(PathUtils.getPublicFilePath() +File.separator+String.valueOf(System.currentTimeMillis())+ mFileBean.fileName);

                                }
                            }
                            writeFile(inputStream, file, contentLength);
                        }
                    }
                }
            });
        } else {
            //文件正在下载中
            mFileBean.error = "文件正在下载中";
            mDownSingleFileListener.downSingleFileComplete(mFileBean);

        }


    }

    public boolean writeFile(InputStream inputStream, File file, final long contentLength) {
        LOGD(TAG, "--save file path" + file.getAbsolutePath());

        if (file == null) {
            return false;
        }


            if(!FileUtils.createOrExistsFile(file))
            if (!FileUtils.createFileByDeleteOldFile(file)) {
                LOGE(TAG,"没有读写权限或文件名不和法");
                if(mDownSingleFileListener!=null){
                    mFileBean.error="没有读写权限或文件名不和法";
                    mFileBean.status=FileStatus.DOWN_FAIL;
                    mDownSingleFileListener.downSingleFileComplete(mFileBean);
                }
                return false;
            }

        mFileBean.filePath = file.getAbsolutePath();
        int hasWrite = 0;
        int readLength = 0;
        byte[] bytes = new byte[KB];
        int count = 0;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            while ((readLength = inputStream.read(bytes)) != -1) {
                hasWrite += readLength;
                bufferedOutputStream.write(bytes, 0, readLength);
                bufferedOutputStream.flush();
                count++;
                LOGD(TAG, "--has write" + hasWrite + "--content length" + contentLength + "--count" + count + "--read length" + readLength+"url"+mFileBean.fileUrl);
                if (count == updateProgressSize || hasWrite == contentLength) {
                    count = 0;
                    final int writeLength = hasWrite;
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mDownSingleFileListener != null) {
                                double progress = writeLength / (double) contentLength;
                                mFileBean.progress = progress;
                                mFileBean.status = FileStatus.DOWNING;
                                mDownSingleFileListener.downSingleFileProgress(mFileBean);
                            }
                        }
                    });
                }

            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mDownSingleFileListener != null) {
                        mFileBean.status = FileStatus.DOWN_SUCCESS;
                        mDownSingleFileListener.downSingleFileComplete(mFileBean);
                    }
                }
            });
            if(mFileBean.fileType== FileType.IMAGE&&mFileBean.filePossession== FilePossession.PUBLIC_FILE){
                //公共的图片在图库显示
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                mContext.sendBroadcast(intent);
                LOGD(TAG,"---public image"+file.getAbsolutePath());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }
}
