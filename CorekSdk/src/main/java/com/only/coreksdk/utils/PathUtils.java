package com.only.coreksdk.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import static com.only.coreksdk.utils.LogUtils.*;

/**
 * Created by only on 16/10/19.
 * Email: onlybeyond99@gmail.com
 */

public class PathUtils {
    private static  String TAG=makeLogTag(PathUtils.class);
    private static String PATH_IMAGE_FILE = "image";
    private static String PATH_FILE="file";




    //外部图片文件存储路径(当没有存储卡时使用的是内部存储)
    public static String getPrivateImagePath(Context context) {
        File file=null;
        if(SDCardUtils.isSDCardEnable()){
          file=context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else {
           file= new File(getPrivatePath(context)+PATH_IMAGE_FILE);
        }
        if(!file.exists()){
            file.mkdir();
        }
        return file.getAbsolutePath();

    }
   //外部私有文件存储路径(当没有存储卡时使用的是内部存储)
    public static String getPrivateFilePath(Context context){
        File file=null;
        if(SDCardUtils.isSDCardEnable()) {
            file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        }else {
            file=new File(context.getFilesDir()+PATH_FILE);
        }
        if(!file.exists()){
            file.mkdir();
        }
        LOGD(TAG,"---private file path"+file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * 外部公共文件路径
     * @return
     */
    public static String getPublicFilePath(){
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+ "demo");
        if(!file.exists()){
            file.mkdir();
        }
        return   file.getAbsolutePath();
    }

    /**
     * 外部公共图片路径
     * @return
     */
    public static String getPublicImageFilePath(){
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+File.separator+"demo"+"/");
        if(!file.exists()){
            file.mkdir();
        }
        return   file.getAbsolutePath();
    }



    private static String getPrivatePath(Context context){
        return context.getFilesDir().getAbsolutePath();

    }




}
