package com.only.coreksdk.network.downFile;


/**
 * Created by only on 16/10/22.
 * Email: onlybeyond99@gmail.com
 */

public interface DownFileListener {
    void downFileListenerProcess(DownFileBean downFileBean);
    void downFileListenerFinish(DownFileBean downFileBean);
}
