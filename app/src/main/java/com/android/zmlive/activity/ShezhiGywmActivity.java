package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.utils.BaseActivity;

public class ShezhiGywmActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhigywm_activity);
        findview();
    }

    private void findview() {
        findViewById(R.id.gywm_back).setOnClickListener(this);
        findViewById(R.id.gywm_shgy).setOnClickListener(this);
        findViewById(R.id.gywm_yszc).setOnClickListener(this);
        findViewById(R.id.gywm_fwtk).setOnClickListener(this);
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
            case R.id.gywm_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.gywm_shgy://社会公约
                ActivityJumpControl.getInstance(activity).gotoShezhishgyActivity();
                break;
            case R.id.gywm_yszc://隐私政策
                ActivityJumpControl.getInstance(activity).gotoShezhiyszcActivity();
                break;
            case R.id.gywm_fwtk://服务条款
                ActivityJumpControl.getInstance(activity).gotoShezhifwtkActivity();
                break;
        }
    }


}
