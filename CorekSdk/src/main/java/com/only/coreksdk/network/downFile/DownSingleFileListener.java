package com.only.coreksdk.network.downFile;

import com.only.coreksdk.network.FileBean;
import com.only.coreksdk.network.FileStatus;

/**
 * Created by only on 16/10/19.
 * Email: onlybeyond99@gmail.com
 */
public interface DownSingleFileListener {
    void downSingleFileProgress(FileBean fileBean);

    void downSingleFileComplete(FileBean fileBean);
}

