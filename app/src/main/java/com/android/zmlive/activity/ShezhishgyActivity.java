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

public class ShezhishgyActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhishgy_activity);
        findview();
        appAbout();
    }

    private TextView shgy_tv;

    private void findview() {
        findViewById(R.id.shgy_back).setOnClickListener(this);
        shgy_tv = (TextView) findViewById(R.id.shgy_tv);

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
            case R.id.shgy_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    /***
     * 社会公约
     */
    private void appAbout() {
        showloading();
        xhttp = new Xhttp(URLManager.appAbout);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String convention = obj.getJSONObject("about").getString("convention");
                    String replace;
                    try {
                        replace = convention.replace("\\n", "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                        replace = convention;
                    }
                    shgy_tv.setText(replace);
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
