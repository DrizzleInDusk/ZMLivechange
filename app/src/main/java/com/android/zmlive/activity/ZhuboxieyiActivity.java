package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.utils.BaseActivity;

public class ZhuboxieyiActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.zhuboxieyi_activity);
        findview();
    }

    private void findview() {
        findViewById(R.id.zbxyback).setOnClickListener(this);
        findViewById(R.id.zbxy_tv).setOnClickListener(this);

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zbxyback:
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
