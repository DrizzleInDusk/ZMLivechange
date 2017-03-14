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

public class XiugaiqmActivity extends BaseActivity implements View.OnClickListener{
    private String signature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xiugaiqm_activity);
        signature=getIntent().getStringExtra("signature");
        findview();
    }
    private TextView gxqm_qm;
    private void findview() {
        findViewById(R.id.gxqm_back).setOnClickListener(this);
        findViewById(R.id.gxqm_wc).setOnClickListener(this);
        gxqm_qm= (TextView) findViewById(R.id.gxqm_qm);
        gxqm_qm.setText(signature);
    }

    /***
     * 点击事件
     */
    private String qianming="";
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.gxqm_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.gxqm_wc:
                qianming= gxqm_qm.getText().toString().trim();
                if(qianming.equals("")){
                    showMessage(getResources().getString(R.string.gxqmnull));
                    return;
                }
                if(!qianming.equals(signature)){
                    updateUserInfo();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

    /***
     * 更新用户信息
     */
    private void updateUserInfo() {
        showloading();
        xhttp = new Xhttp(URLManager.updateUserInfo);
        xhttp.add("id", MyData.USERID);
        xhttp.add("signature",qianming);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissloading();
                XiugaiziliaoActivity.strqm=qianming;
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
            }

            @Override
            public void onError() {
                dismissloading();
            }
        });
    }

}
