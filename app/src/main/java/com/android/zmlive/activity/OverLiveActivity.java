package com.android.zmlive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class OverLiveActivity extends BaseActivity implements View.OnClickListener{
    private String onlinepeo,onlinefx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.overlive_activity);
        onlinepeo=getIntent().getStringExtra("onlinepeo");
        onlinefx=getIntent().getStringExtra("onlinefx");
        findview();
    }
    private TextView ol_onlinepeo,ol_onlinefx;
    private void findview() {
        findViewById(R.id.fanhuishouye).setOnClickListener(this);
        findViewById(R.id.ol_qq).setOnClickListener(this);
        findViewById(R.id.ol_wx).setOnClickListener(this);
        findViewById(R.id.ol_line).setOnClickListener(this);
        findViewById(R.id.ol_fb).setOnClickListener(this);
        ol_onlinepeo= (TextView) findViewById(R.id.ol_onlinepeo);
        ol_onlinefx= (TextView) findViewById(R.id.ol_onlinefx);
        ol_onlinepeo.setText(onlinepeo+"人");
        ol_onlinefx.setText(onlinefx+"颗");
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ol_qq://分享到QQ
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.ol_wx://分享到WX
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.ol_line://分享到LINE
                new ShareAction(activity).setPlatform(SHARE_MEDIA.LINE)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.ol_fb://分享到feacbook
                new ShareAction(activity).setPlatform(SHARE_MEDIA.FACEBOOK)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.fanhuishouye:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.showDLog("plat","platform"+platform);
            if(platform.name().equals("WEIXIN_FAVORITE")){
                showMessage(platform + " 收藏成功啦");
            }else{
                showMessage(platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showMessage(platform + " 分享失败啦");
            if(t!=null){
                LogUtil.showDLog("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showMessage(platform + " 分享取消了");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
