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

public class ShezhidlmmActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhidlmm_activity);
        findview();
    }

    private TextView dlmm_oldpsw, dlmm_newpsw, dlmm_aginenewpsw;

    private void findview() {
        findViewById(R.id.dlmm_back).setOnClickListener(this);
        dlmm_oldpsw = (TextView) findViewById(R.id.dlmm_oldpsw);
        dlmm_newpsw = (TextView) findViewById(R.id.dlmm_newpsw);
        dlmm_aginenewpsw = (TextView) findViewById(R.id.dlmm_aginenewpsw);
        findViewById(R.id.dlmm_wancheng).setOnClickListener(this);
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
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.dlmm_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.dlmm_wancheng://完成
                String oldpsw = dlmm_oldpsw.getText().toString().trim();
                String newpsw = dlmm_newpsw.getText().toString().trim();
                String aginenewpsw = dlmm_aginenewpsw.getText().toString().trim();
                if (oldpsw.equals("")) {
                    showMessage(getResources().getString(R.string.oldpswhint));
                    return;
                }
                if (!newpsw.equals(aginenewpsw)) {
                    showMessage(getResources().getString(R.string.pswbuxiangtong));
                    return;
                }
                editPassword(oldpsw,newpsw,aginenewpsw);
                break;
        }
    }

    /***
     * 修改登录密码
     */
    private void editPassword(String opass, String npass, String cpass) {
        showloading();
        xhttp = new Xhttp(URLManager.editPassword);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("opass", opass);
        xhttp.add("npass", npass);
        xhttp.add("cpass", cpass);
        xhttp.add("type", "lo");
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    showMessage(obj.getString("meg"));
                    if(status.equals("1")){
//                        ActivityJumpControl.getInstance(activity).gotoChooseLoginActivity();
                        finish();
                    }
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
