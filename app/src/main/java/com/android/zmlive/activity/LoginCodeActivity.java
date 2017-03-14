package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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

public class LoginCodeActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.logincode_activity);
        findview();
    }

    private TextView logincode_yqm;
    private LinearLayout logincode_xieyi;
    private void findview() {
        logincode_yqm = (TextView) findViewById(R.id.logincode_yqm);
        findViewById(R.id.logincodeback).setOnClickListener(this);
        findViewById(R.id.logincode_tv).setOnClickListener(this);
        logincode_xieyi = (LinearLayout) findViewById(R.id.logincode_xieyi);
        logincode_xieyi.setOnClickListener(this);
        logincode_xieyi.setSelected(true);
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.logincodeback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.logincode_tv:
                code = logincode_yqm.getText().toString().trim();
                if (code.length() == 0) {
                    showMessage(context.getResources().getString(R.string.logincodehint));
                    return;
                }
                if (!logincode_xieyi.isSelected()) {
                    showMessage(context.getResources().getString(R.string.noxieyi));
                    return;
                }
                loginByCode();
                break;
            case R.id.logincode_xieyi:
                if (logincode_xieyi.isSelected()) {
                    logincode_xieyi.setSelected(false);
                } else {
                    ActivityJumpControl.getInstance(activity).gotoRexieyiActivity();
                    logincode_xieyi.setSelected(true);
                }
                break;
        }
    }

    /***
     * 邀请码登录
     */
    private String code = "";

    private void loginByCode() {
        showloading();
        xhttp = new Xhttp(URLManager.loginByCode);
        xhttp.add("code", code);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mesg = obj.getString("meg");
                    showMessage(mesg);
                    if (status.equals("1")) {
                        MyData.IDENTITY=obj.getString("identity");
                        MyData.USERID="0";
                        MyData.HEAD="default_head";
                        MyData.NICKNAME="游客";
                        MyData.CODE= code.toLowerCase();;
                        MyData.CONCERN="0";
                        MyData.PRAISE="0";
                        MyData.FANS="0";
                        MyData.LEVEL="0";
                        MyData.ACCID=obj.getString("accid");
                        MyData.TOKEN=obj.getString("token");
                        ActivityJumpControl.getInstance(activity).gotoMainActivity();
                        ActivityJumpControl.getInstance(activity).popAllActivity();
                        ChooseLoginActivity.closechoose();
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
