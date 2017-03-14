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

public class ShezhifwtkActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhifwtk_activity);
        findview();
        appAbout();
    }

    private TextView fwtk_tv;

    private void findview() {
        findViewById(R.id.fwtk_back).setOnClickListener(this);
        fwtk_tv = (TextView) findViewById(R.id.fwtk_tv);
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
            case R.id.fwtk_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    /***
     * 服务条款
     */
    private void appAbout() {
        showloading();
        xhttp = new Xhttp(URLManager.appAbout);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                    String clause = obj.getJSONObject("about").getString("clause");
                    String replace;
                    try {
                        replace = clause.replace("\\n", "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                        replace = clause;
                    }
                    fwtk_tv.setText(replace);
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
