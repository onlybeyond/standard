package com.only.coreksdk.network.uploadFile;


import com.only.coreksdk.network.FileBean;

/**
 * Created by only on 16/10/18.
 * Email: onlybeyond99@gmail.com
 */

public interface UploadListener {
    void uploadFinish(FileBean fileBean);
    void uploadProgress(FileBean fileBean);
}
