package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class CaiwuGlActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.caiwuguanli_activity);
        findview();
        balance();
    }
    private TextView cwgl_bzye;
    private void findview() {
        findViewById(R.id.cwglback).setOnClickListener(this);
        findViewById(R.id.cwgl_szmx).setOnClickListener(this);
        cwgl_bzye= (TextView) findViewById(R.id.cwgl_bzye);
        findViewById(R.id.cwgl_fsgxb).setOnClickListener(this);
        findViewById(R.id.cwgl_tx).setOnClickListener(this);
        findViewById(R.id.cwgl_chongzhi).setOnClickListener(this);
    }

    /***
     * 点击事件
     */
    private String mode="ye";
    private String totle="0";
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cwgl_szmx://收支明细
                ActivityJumpControl.getInstance(activity).gotoShouzhimxActivity();
                break;
            case R.id.cwgl_fsgxb://粉丝贡献榜
                ActivityJumpControl.getInstance(activity).gotoFensigxActivity();
                break;
            case R.id.cwgl_tx://提现
                ActivityJumpControl.getInstance(activity).gotoTixianBdActivity(mode,totle);
                break;
            case R.id.cwgl_chongzhi://充值
                ActivityJumpControl.getInstance(activity).gotoChongzhiActivity(totle);
                break;
            case R.id.cwglback:
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

    /***
     * 获取用户余额和收益
     */
    private void balance() {
        xhttp = new Xhttp(URLManager.balance);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    totle = obj.getString("diamond");
                    cwgl_bzye.setText(totle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });
    }
}
