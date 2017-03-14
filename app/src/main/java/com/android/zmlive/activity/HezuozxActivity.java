package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class HezuozxActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.hezuozx_activity);
        findview();
    }

    private EditText hzzx_ndch, hzzx_nszdq, hzzx_ndlxfs, hzzx_content;

    private void findview() {
        findViewById(R.id.hzzxback).setOnClickListener(this);
        findViewById(R.id.hzzx_tijiao).setOnClickListener(this);
        hzzx_ndch = (EditText) findViewById(R.id.hzzx_ndch);
        hzzx_nszdq = (EditText) findViewById(R.id.hzzx_nszdq);
        hzzx_ndlxfs = (EditText) findViewById(R.id.hzzx_ndlxfs);
        hzzx_content = (EditText) findViewById(R.id.hzzx_content);

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
            case R.id.hzzx_tijiao://提交
                String name = hzzx_ndch.getText().toString().trim();
                String address = hzzx_nszdq.getText().toString().trim();
                String tel = hzzx_ndlxfs.getText().toString().trim();
                String con = hzzx_content.getText().toString().trim();
                if (name.equals("")) {
                    showMessage(getResources().getString(R.string.ndch) + getResources().getString(R.string.bnwk));
                    return;
                }
                if (address.equals("")) {
                    showMessage(getResources().getString(R.string.nszdq) + getResources().getString(R.string.bnwk));
                    return;
                }
                if (tel.equals("")) {
                    showMessage(getResources().getString(R.string.ndlxfs) + getResources().getString(R.string.bnwk));
                    return;
                }
                if (con.equals("")) {
                    showMessage(getResources().getString(R.string.mshzsy));
                    return;
                }
                cooperation(name,address,tel,con);
                break;
            case R.id.hzzxback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    /***
     * 合作中心
     */
    private void cooperation(String name, String areas, String phone, String description) {
        showloading();
        xhttp = new Xhttp(URLManager.cooperation);
        xhttp.add("name", name);
        xhttp.add("areas", areas);
        xhttp.add("phone", phone);
        xhttp.add("description", description);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
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
