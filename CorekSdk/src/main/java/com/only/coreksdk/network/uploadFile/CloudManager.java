package com.only.coreksdk.network.uploadFile;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.only.coreksdk.utils.LogUtils;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.rs.PutPolicy;

import org.json.JSONException;

import java.util.UUID;

import static com.only.coreksdk.utils.LogUtils.*;
/**
 * QINIU 相关行为
 */
public class CloudManager {
    private static String TAG=makeLogTag(CloudManager.class);

    public static final String IMAGE_RESIZE_URL = "?imageView2/0/w/800";
    // 参数
    public static String HOST_NAME_PREFIX = "http://";
    public static String HOST_NAME_SUFFIX = ".7xvipx.com1.z0.glb.clouddn.com";
    public static String ACCESS_KEY = "XPeifa0LTL2pERdqsiMW4ixggs0ucyJhqmD5JxTG";

    // Bucket 名称
    public static String SECRET_KEY = "HDTYSKxJjjtynx5gRcarf9-vzKr1bVsezvyZyxcD";
    public static Mac mac;
    public static String BucketPhoto = "rxjava";
    static CloudManager sInstance;
    Uri uploadUri;
    Context context;

    public static CloudManager getInstance() {
        if (sInstance == null || mac == null)
            throw new IllegalStateException("must call init first");
        return sInstance;
    }

    public static void init(Context context) {
        sInstance = new CloudManager();
        sInstance.context = context;
        mac = new Mac(ACCESS_KEY, SECRET_KEY);
    }

    /**
     * 根据 key 和 type 得到远程地址
     *
     * @param fileKey
     */

    public static String generateRemoteUri(String fileKey) {
        if (fileKey.startsWith("http")) { // 如果传的是URI，则直接显示
            return fileKey;
        }
        return HOST_NAME_PREFIX + BucketPhoto + HOST_NAME_SUFFIX + "/" + fileKey;
    }

    public static String makeResizeImageUrl(String url) {
        if (!TextUtils.isEmpty(url))
            return url + IMAGE_RESIZE_URL;
        return null;
    }

    private String genToken(String bucketName) {
        String token = null;
        PutPolicy putPolicy = new PutPolicy(bucketName);
        putPolicy.expires = 60*60*24*365*1000;              //通过expires设置deadline，解决手机时间错误导致的上传失败
        try {
            token = putPolicy.token(mac);
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    public void uploadImage(String filePath, final UpCompletionHandler upCompletionHandler, final UploadOptions options) {
        LogUtils.LOGD(TAG,"---file paht"+filePath);
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(filePath == null ? "" : filePath, genUniqueKey(), genPhotoToken(), upCompletionHandler, options);
    }

    String genUniqueKey() {
        return String.valueOf(UUID.randomUUID());
    }

    String genPhotoToken() {
        return genToken(BucketPhoto);
    }


    public static String makePreviewUrl(String fileKey) {
        String remoteUri;

        if (fileKey.startsWith("http")) { // 如果传的是URI，则直接显示
            remoteUri = fileKey;
        } else {
            remoteUri = generateRemoteUri(fileKey);
        }
        return makeResizeImageUrl(remoteUri);
    }

    /**
     * 根据 ImageView 生成不同数据
     * 对有?ImageView的url进行过滤
     *
     * @param url
     * @param imageView
     * @return
     */
    public static String makePreviewUrl(String url, ImageView imageView) {
        if (url == null) return null;
        int iQuestionMark = url.indexOf("?");
        if (iQuestionMark != -1) {
            url = url.substring(0, iQuestionMark);
        }

        if (TextUtils.isEmpty(url)) return "";

        int width = imageView.getWidth();
        int height = imageView.getHeight();
        if (width == 0 || height == 0) {
            ViewParent parent = imageView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup parentView = (ViewGroup) parent;
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                parentView.measure(widthMeasureSpec, widthMeasureSpec);
                width = imageView.getMeasuredWidth();
                height = imageView.getMeasuredHeight();
            }
        }
        if (width == 0 || height == 0) {
            return url + IMAGE_RESIZE_URL;
        } else {
            return url + String.format("?imageView/1/w/%s/h/%s", width, height);
        }
    }

    public static String makePreviewUrlUsingImageMogr2(String url, ImageView imageView) {
        if (url == null) return null;
        int iQuestionMark = url.indexOf("?");
        if (iQuestionMark != -1) {
            url = url.substring(0, iQuestionMark);
        }

        if (TextUtils.isEmpty(url)) return "";

        int width = imageView.getWidth();
        int height = imageView.getHeight();
        if (width == 0 || height == 0) {
            ViewParent parent = imageView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup parentView = (ViewGroup) parent;
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                parentView.measure(widthMeasureSpec, widthMeasureSpec);
                width = imageView.getMeasuredWidth();
                height = imageView.getMeasuredHeight();
            }
        }
        if (width == 0 || height == 0) {
            return url + IMAGE_RESIZE_URL;
        } else {
            return url + String.format("?imageMogr2/thumbnail/%sx%s!", width, height);
        }
    }


}
