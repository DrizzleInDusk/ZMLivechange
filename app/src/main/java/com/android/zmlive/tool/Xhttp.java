package com.android.zmlive.tool;

import android.util.Log;

import com.android.zmlive.tool.utils.NetworkUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.io.File;
import java.util.List;

/**
 * Created by Kkan on 2016/9/27.
 */

public class Xhttp {
    XhttpCallBack xhttpCallBack;
    private RequestParams params;

    public Xhttp(String url) {
        params = new RequestParams(url);
    }

    public void add(String key, String value) {
        params.addBodyParameter(key, value);
    }

    public void add(String key, File file) {
        params.setMultipart(true);
        params.addBodyParameter(key, file);
    }

    /**
     * 上传 file 集合
     *
     * @param key
     * @param filelist
     */
//    public void add(String key, List<File> filelist, String type) {
    public void add(String key, List<File> filelist) {
        params.setMultipart(true);
        params.addBodyParameter(key, filelist, null);
    }

    public void add(String key, File[] filelist) {
        params.setMultipart(true);
        params.addBodyParameter(key, filelist, null);
    }

    public void post(XhttpCallBack xhttpCallBack) {
        Log.w("Xhttp>>params>>>>>", params + "");
        this.xhttpCallBack = xhttpCallBack;
        if (!NetworkUtil.isNetAvailable(x.app())) {
            Futile.showMessage(x.app(), "无法链接到网络");
            Error();
            return;
        }
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("Xhttp>>result>>>>>", result);
                Success(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("Xhttp>>result>>>>>", ex.getMessage());
                Error();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    public void get(XhttpCallBack xhttpCallBack) {
        this.xhttpCallBack = xhttpCallBack;
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Success(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Error();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void Success(String result) {
        xhttpCallBack.onSuccess(result);
    }

    public void Error() {
        xhttpCallBack.onError();
    }
}
