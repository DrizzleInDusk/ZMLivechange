package com.android.zmlive.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
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

public class YaoqingmabdActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.yaoqingmabd_activity);
        findview();
        authCode();
    }

    private TextView yqmbd_et;
    private ImageView yqmbd_deleteet;
    private TextView yqmyt,yqmbd_yes;
    private LinearLayout yqmbd_ll;

    private void findview() {
        findViewById(R.id.yqmbd_back).setOnClickListener(this);
        yqmbd_ll= (LinearLayout) findViewById(R.id.yqmbd_ll);
        yqmbd_yes= (TextView) findViewById(R.id.yqmbd_yes);
        yqmbd_yes.setOnClickListener(this);
        yqmyt = (TextView) findViewById(R.id.yqmyt);
        yqmbd_deleteet = (ImageView) findViewById(R.id.yqmbd_deleteet);
        yqmbd_deleteet.setOnClickListener(this);
        yqmbd_et = (TextView) findViewById(R.id.yqmbd_et);
        yqmbd_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                yqmbd = s.toString();
                if (yqmbd.length() > 0) {
                    yqmbd_deleteet.setVisibility(View.VISIBLE);
                } else {
                    yqmbd_deleteet.setVisibility(View.INVISIBLE);
                }
            }
        });
        yqmbd_ll.setVisibility(View.GONE);
    }

    private String yqmbd = "";

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.yqmbd_deleteet:
                yqmbd_et.setText("");
                break;
            case R.id.yqmbd_yes:
                if (yqmbd.length() > 0) {
                    codeSupplement();
                } else {
                    showMessage(getResources().getString(R.string.yqmbdhint));
                }
                break;
            case R.id.yqmbd_back:
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
    /***
     * 邀请码补登
     */
    private void codeSupplement() {
        showloading();
        xhttp = new Xhttp(URLManager.codeSupplement);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("code", yqmbd);
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

    /***
     * 邀请码补登
     */
    private void authCode() {
        showloading();
        xhttp = new Xhttp(URLManager.authCode);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    if (status.equals("0")) {
                        String mess = obj.getString("meg");
                        String code = obj.getString("code");
                        showMessage(mess);
                        yqmbd_et.setVisibility(View.GONE);
                        yqmbd_deleteet.setVisibility(View.GONE);
                        yqmbd_ll.setVisibility(View.GONE);
                        yqmyt.setVisibility(View.VISIBLE);
                        yqmbd_yes.setEnabled(false);
                        yqmyt.setText("填写的邀请码："+code);
                    }else{
                        yqmbd_et.setVisibility(View.VISIBLE);
                        yqmbd_ll.setVisibility(View.VISIBLE);
                        yqmbd_deleteet.setVisibility(View.GONE);
                        yqmyt.setVisibility(View.GONE);
                        yqmbd_yes.setEnabled(true);
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
