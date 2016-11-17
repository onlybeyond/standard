package com.only.coreksdk.network.downFile;

import android.content.Context;
import android.text.TextUtils;


import com.only.coreksdk.network.FileBean;
import com.only.coreksdk.network.FileStatus;
import com.only.coreksdk.utils.NetworkUtils;

import java.util.LinkedList;
import java.util.List;

import static com.only.coreksdk.utils.LogUtils.*;


/**
 * Created by only on 16/10/22.
 * Email: onlybeyond99@gmail.com
 */

public class DownFileManager implements DownSingleFileListener {

    private static String TAG = makeLogTag(DownFileManager.class);
    private static int MAX_FAIL_COUNT = 3;//每个文件得最大失败次数

    //state

    private boolean returnDetail;//是否返回详细数据,也就是 fileBeanList是否返回


    //basic data
    private int fileCount;
    private int finishCount = 0;
    //下载模式,一个一个的下载,或者一起下载,一个一个的下载会有一个总的进度,一起下载有单独的进度,
    //总进度为0
    private DownMode mDownMode = DownMode.ONE_BY_ONE;
    //每一个下载的标记,默认会设置成第一下载文件的url,当没有下载文件时为空
    private String mDownTag = "";

    //object data
    private Context mContext;
    //原下载集合
    private LinkedList<FileBean> mFileBeenList;
    //下载集合
    private LinkedList<FileBean> mDownFileList;
    //下载失败集合
    private LinkedList<FileBean> mFailFileList;
    //正在下载的集合
    private LinkedList<FileBean> mSendingFileList;
    //下载成功的集合
    private LinkedList<FileBean> mSuccessFileList;
    private DownFileListener mDownFileListener;

    public DownFileManager(Context mContext) {
        mDownFileList = new LinkedList<>();
        mFailFileList = new LinkedList<>();
        mSendingFileList = new LinkedList<>();
        mSuccessFileList = new LinkedList<>();
        mFileBeenList = new LinkedList<>();


        this.mContext = mContext;
    }

    public void setDownTag(String downTag) {
        this.mDownTag = downTag;
    }

    public void setDownMode(DownMode downMode) {
        this.mDownMode = downMode;
    }

    public void downSingleFile(FileBean fileBean, DownFileListener downFileListener) {

        mDownFileListener = downFileListener;
        fileCount = 1;
        mFileBeenList.add(fileBean);
        mDownFileList.add(fileBean);
        if (!checkData(mDownFileList))
            return;
        mSendingFileList.add(fileBean);
        mDownFileList.remove(fileBean);
        fileBean.status = FileStatus.DEFAULT;
        DownSingleFile downSingleFile = new DownSingleFile(mContext);
        downSingleFile.downFile(fileBean, this);

    }

    public void downFiles(LinkedList<FileBean> fileBeenList, DownFileListener downFileListener) {
        mDownFileListener = downFileListener;
        if (!checkData(fileBeenList))
            return;
        mFileBeenList.addAll(fileBeenList);
        mDownFileList.addAll(fileBeenList);
        if (DownMode.ONE_BY_ONE.equals(mDownMode)) {
            FileBean fileBean = mDownFileList.removeFirst();
            mSendingFileList.add(fileBean);
            fileBean.status = FileStatus.DEFAULT;
            DownSingleFile downSingleFile = new DownSingleFile(mContext);
            downSingleFile.downFile(fileBean, this);

        } else {
            int fileCount = fileBeenList.size();
            for (int i = 0; i < fileCount; i++) {
                FileBean fileBean = fileBeenList.get(i);
                mSendingFileList.add(fileBean);
                mDownFileList.remove(fileBean);
                fileBean.status = FileStatus.DEFAULT;
                DownSingleFile downSingleFile = new DownSingleFile(mContext);
                downSingleFile.downFile(fileBean, this);
            }

        }


    }

    @Override
    public synchronized void downSingleFileProgress(FileBean fileBean) {

        int totalCount = mFileBeenList.size();
        int successCount = mSuccessFileList.size();
        int failCount = mFailFileList.size();
        int sendingCount = mSendingFileList.size();
        double progress = 0;//总的进度,一个一个上传有,多个一起上传为默认值0
        if (DownMode.ONE_BY_ONE == mDownMode) {
            progress = successCount / (double) totalCount + (1 / (double) totalCount) * fileBean.progress;
        }


        LOGD(TAG, "--total count" + totalCount + "---success count" + successCount + "---fail count" + failCount + "sending counet" + sendingCount + "---progress" + progress + "--file progress" + fileBean.progress);
        DownFileBean downFileBean = parseRetureData(successCount, failCount, sendingCount, progress);
        mDownFileListener.downFileListenerProcess(downFileBean);


    }

    @Override
    public void downSingleFileComplete(FileBean fileBean) {
        LOGD(TAG, "--downSingleFileComplete file url" + fileBean.fileUrl + "file status:" + fileBean.status);
        if (fileBean.status == FileStatus.DOWN_SUCCESS) {
            //上传成功
            boolean remove = mSendingFileList.remove(fileBean);
            mSuccessFileList.add(fileBean);
        } else if (fileBean.status == FileStatus.DOWN_FAIL) {
            //上传失败
            LOGD(TAG, "---fail error " + fileBean.error + "error count" + fileBean.failCount);
            boolean remove = mSendingFileList.remove(fileBean);
            fileBean.failCount++;
            mFailFileList.add(fileBean);
        }
        int totalCount = mFileBeenList.size();
        int successCount = mSuccessFileList.size();
        int failCount = mFailFileList.size();
        int sendingCount = mSendingFileList.size();
        LOGD(TAG, "---total count" + totalCount + "---success count" + successCount + "---fail count" + failCount + "sending counet" + sendingCount);
        if (successCount == totalCount) {
            //全部上传成功
            if (mDownFileListener != null) {
                DownFileBean downFileBean = parseRetureData(successCount, failCount, sendingCount, 1);
                downFileBean.isSuccess = true;
                mDownFileListener.downFileListenerFinish(downFileBean);

            }
            cleanData();


        } else {
            if (NetworkUtils.isAvailable(mContext)) {
                if (mDownFileList.size() > 0) {
                    FileBean removeFileBean = mDownFileList.removeFirst();
                    removeFileBean.status = FileStatus.DEFAULT;
                    mSendingFileList.add(removeFileBean);
                    DownSingleFile downSingleFile = new DownSingleFile(mContext);
                    downSingleFile.downFile(removeFileBean, this);

                } else {
                    if (mFailFileList.size() > 0) {
                        FileBean failFileBean = mFailFileList.removeFirst();
                        if (failFileBean.failCount <= MAX_FAIL_COUNT) {
                            LOGD(TAG, "--- resend error" + fileBean.error + "---resend count" + fileBean.failCount + "---fail file" + fileBean.fileUrl);
                            mSendingFileList.add(failFileBean);
                            mFailFileList.remove(failFileBean);
                            failFileBean.status = FileStatus.DEFAULT;
                            DownSingleFile downSingleFile = new DownSingleFile(mContext);
                            downSingleFile.downFile(failFileBean, this);

                        } else {
                            //大于最大失败次数,直接返回
                            LOGD(TAG, "---fail error" + fileBean.error + "---fail url" + fileBean.fileUrl);
                            if (mDownFileListener != null) {
                                DownFileBean downFileBean = parseRetureData(successCount, failCount, sendingCount, 0);
                                downFileBean.downMode = mDownMode;

                                mDownFileListener.downFileListenerFinish(downFileBean);
                            }
                        }
                    }
                }
            }

        }


    }

    private DownFileBean parseRetureData(int successCount, int failCount, int sendingCount, double progress) {
        DownFileBean downFileBean = new DownFileBean();
        downFileBean.failCount = failCount;
        downFileBean.sendingCount = sendingCount;
        downFileBean.successCount = successCount;
        downFileBean.progress = progress;
        downFileBean.fileBeenList = mFileBeenList;
        downFileBean.downMode = mDownMode;
        downFileBean.tag = mDownTag;
        if (returnDetail) {
            downFileBean.fileBeenList = mFileBeenList;
        }
        return downFileBean;
    }

    /**
     * 清理数据
     */
    private void cleanData() {
        mSuccessFileList = null;
        mFailFileList = null;
        mSendingFileList = null;
        mDownFileList = null;
    }

    /**
     * 检测数据
     */
    private boolean checkData(List<FileBean> fileBeanList) {
        if (fileBeanList != null && fileBeanList.size() > 0) {
            String fileUrls = "";
            int size = fileBeanList.size();
            int checkNum = 0;
            for (int i = 0; i < size; i++) {
                String fileUrl = fileBeanList.get(i).fileUrl;
                if (!TextUtils.isEmpty(fileUrl)) {
                    if (i == 0) {
                        //设置下载标识
                        if (TextUtils.isEmpty(mDownTag)) {
                            mDownTag = fileUrl;
                        }
                    }
                    if (!fileUrls.contains(fileUrl)) {
                        fileUrls += fileUrl;
                    } else {
                        //下载链接重复异常
                        if (mDownFileListener != null) {
                            DownFileBean downFileBean = new DownFileBean();
                            downFileBean.downMode = mDownMode;
                            downFileBean.isSuccess = false;
                            downFileBean.error = "有相同的下载链接";
                            downFileBean.tag = mDownTag;
                            mDownFileListener.downFileListenerFinish(downFileBean);
                        }
                        return false;
                    }

                } else {
                    //下载链接为空异常
                    if (mDownFileListener != null) {
                        DownFileBean downFileBean = new DownFileBean();
                        downFileBean.downMode = mDownMode;
                        downFileBean.isSuccess = false;
                        downFileBean.error = "第" + i + "文件下载链接为null";
                        downFileBean.tag = mDownTag;
                        mDownFileListener.downFileListenerFinish(downFileBean);
                    }
                    return false;

                }
            }

        } else {
            if (mDownFileListener != null) {
                //无下载内容异常
                if (mDownFileListener != null) {
                    DownFileBean downFileBean = new DownFileBean();
                    downFileBean.isSuccess = false;
                    downFileBean.error = "无下载内容";
                    downFileBean.downMode = mDownMode;
                    if (!TextUtils.isEmpty(mDownTag)) {
                        downFileBean.tag = mDownTag;
                    }
                    mDownFileListener.downFileListenerFinish(downFileBean);
                }
                return false;
            }
        }
        return true;
    }

}
