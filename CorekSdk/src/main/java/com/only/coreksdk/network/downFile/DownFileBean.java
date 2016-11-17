package com.only.coreksdk.network.downFile;



import com.only.coreksdk.network.FileBean;

import java.util.List;

/**
 * Created by only on 16/10/22.
 * Email: onlybeyond99@gmail.com
 */

public class DownFileBean {

    public String tag="";//每一次下载的标识,默认用第一个文件的url作为标识
    public int sendingCount=0;//正在发送的个数
    public int failCount=0;//失败的个数;
    public int successCount=0;//成功的个数;
    public boolean isSuccess=false;//整体是否成功
    public String error="";
    public double progress;
    public DownMode downMode;
    public List<FileBean>  fileBeenList; //当不返回详细信息时为空
}
