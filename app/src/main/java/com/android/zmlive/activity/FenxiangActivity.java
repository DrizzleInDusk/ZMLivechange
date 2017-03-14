package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

public class FenxiangActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.fenxiang_activity);
        findview();
    }
    private ImageView fx_neirong ;
    private void findview() {
        findViewById(R.id.fx_back).setOnClickListener(this);
        fx_neirong= (ImageView) findViewById(R.id.fx_neirong);
        fx_neirong.setOnClickListener(this);
        Picasso.with(context).load(URLManager.ads+MyData.Dapic).into(fx_neirong);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fx_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.fx_neirong:
                if (canfenx) {
                    canfenx = false;
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canfenx = true;
                        }
                    }, 2000);
                    dialoshare();
                }
                break;
        }
    }
    private AlertDialog dialog;
    /**
     * dialog 分享
     */
    private void dialoshare() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_fenxiang);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth()*1); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialog.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.LINE)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.FACEBOOK)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_qx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private boolean canfenx = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.showDLog("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                showMessage(platform + " 收藏成功啦");
            } else {
                showMessage(platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            showMessage(platform + " 分享失败啦");
            if (t != null) {
                LogUtil.showDLog("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            showMessage(platform + " 分享取消了");
        }
    };
    /***
     * 开启直播
     */

    private void buildliveroom() {
        showloading();
        xhttp = new Xhttp(URLManager.creatRoom);
        xhttp.add("password", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissloading();
            }

            @Override
            public void onError() {

                dismissloading();
            }
        });
    }

}
