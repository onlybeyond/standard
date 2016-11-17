package com.only.coreksdk.network;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by only on 16/10/19.
 * Email: onlybeyond99@gmail.com
 * 上传或者下载文件的实体类
 */

public class FileBean implements Parcelable {

    public int failCount=0;//失败的次数
    public String filePath="";//存放位置
    public String fileUrl="";//网络url
    public String fileName="";
    public FileStatus status=FileStatus.DEFAULT;//文件的状态
    public double progress;//上传或者下载进度
    public FileType fileType=FileType.DEFAULT;
    public String error="";
    public String sign="";//文件标识
    public  FilePossession filePossession=FilePossession.PUBLIC_FILE;//文件是否存在公共目录


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.failCount);
        dest.writeString(this.filePath);
        dest.writeString(this.fileUrl);
        dest.writeString(this.fileName);
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeDouble(this.progress);
        dest.writeInt(this.fileType == null ? -1 : this.fileType.ordinal());
        dest.writeString(this.error);
        dest.writeString(this.sign);
        dest.writeInt(this.filePossession == null ? -1 : this.filePossession.ordinal());
    }

    public FileBean() {
    }

    protected FileBean(Parcel in) {
        this.failCount = in.readInt();
        this.filePath = in.readString();
        this.fileUrl = in.readString();
        this.fileName = in.readString();
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : FileStatus.values()[tmpStatus];
        this.progress = in.readDouble();
        int tmpFileType = in.readInt();
        this.fileType = tmpFileType == -1 ? null : FileType.values()[tmpFileType];
        this.error = in.readString();
        this.sign = in.readString();
        int tmpFilePossession = in.readInt();
        this.filePossession = tmpFilePossession == -1 ? null : FilePossession.values()[tmpFilePossession];
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        @Override
        public FileBean createFromParcel(Parcel source) {
            return new FileBean(source);
        }

        @Override
        public FileBean[] newArray(int size) {
            return new FileBean[size];
        }
    };
}
