package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ShezhiMmglActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhimmgl_activity);
        findview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        joinPM();
    }

    private void findview() {
        findViewById(R.id.mmgl_back).setOnClickListener(this);
        findViewById(R.id.mmgl_dlmm).setOnClickListener(this);
        findViewById(R.id.mmgl_jymm).setOnClickListener(this);

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
            case R.id.mmgl_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.mmgl_dlmm://登录密码
                ActivityJumpControl.getInstance(activity).gotoShezhidlmmActivity();
                break;
            case R.id.mmgl_jymm://交易密码
                ActivityJumpControl.getInstance(activity).gotoShezhijymmActivity();
                break;
        }
    }


    /***
     * 判断有没有支付密码
     */
    private void joinPM() {
        showloading();
        xhttp = new Xhttp(URLManager.joinPM);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                     MyData.TRPASS = obj.getString("trPass");
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
