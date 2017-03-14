package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ShezhiLxwmActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhilxwm_activity);
        findview();
        appAbout();
    }

    private TextView lxwm_tel, lxwm_email;

    private void findview() {
        findViewById(R.id.lxwm_back).setOnClickListener(this);
        lxwm_tel = (TextView) findViewById(R.id.lxwm_tel);
        lxwm_email = (TextView) findViewById(R.id.lxwm_email);
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
            case R.id.lxwm_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    /***
     * 联系我们
     */
    private void appAbout() {
        showloading();
        xhttp = new Xhttp(URLManager.appAbout);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    lxwm_email.setText(obj.getJSONObject("about").getString("mailbox"));
                    lxwm_tel.setText(obj.getJSONObject("about").getString("telephone"));
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
