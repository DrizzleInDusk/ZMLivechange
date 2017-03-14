package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.utils.BaseActivity;

public class XiezhuglActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xiezhugl_activity);
        findview();
    }

    private void findview() {
        findViewById(R.id.xzglback).setOnClickListener(this);
        findViewById(R.id.xzgl_zbsq).setOnClickListener(this);
        findViewById(R.id.xzgl_yylb).setOnClickListener(this);

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xzgl_yylb://预约列表
                ActivityJumpControl.getInstance(activity).gotoXiezhuyyActivity();
                break;
            case R.id.xzgl_zbsq://直播申请
                ActivityJumpControl.getInstance(activity).gotoXiezhuzhiboActivity();
                break;
            case R.id.xzglback:
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

}
