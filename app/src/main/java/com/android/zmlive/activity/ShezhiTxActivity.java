package com.android.zmlive.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ShezhiTxActivity extends BaseActivity implements View.OnClickListener {
    private String soundon,vibrateon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhitx_activity);
        soundon= Futile.getValue(context,"soundon");
        vibrateon=Futile.getValue(context,"vibrateon");
        getRemind();
        findview();
    }

    private TextView shezhitx_shengyin, shezhitx_zhendong, shezhitx_gfxx, shezhitx_tskg, shezhitx_kbtx;

    private void findview() {
        findViewById(R.id.sztxback).setOnClickListener(this);
        shezhitx_shengyin = (TextView) findViewById(R.id.shezhitx_shengyin);
        shezhitx_shengyin.setOnClickListener(this);
        shezhitx_zhendong = (TextView) findViewById(R.id.shezhitx_zhendong);
        shezhitx_zhendong.setOnClickListener(this);
        shezhitx_gfxx = (TextView) findViewById(R.id.shezhitx_gfxx);
        shezhitx_gfxx.setOnClickListener(this);
        shezhitx_tskg = (TextView) findViewById(R.id.shezhitx_tskg);
        shezhitx_tskg.setOnClickListener(this);
        shezhitx_kbtx = (TextView) findViewById(R.id.shezhitx_kbtx);
        shezhitx_kbtx.setOnClickListener(this);
        if(soundon.equals("off")){
            shezhitx_shengyin.setSelected(false);
        }else{
            shezhitx_shengyin.setSelected(true);
        }
        if(vibrateon.equals("off")){
            shezhitx_zhendong.setSelected(false);
        }else{
            shezhitx_zhendong.setSelected(true);
        }
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sztxback:
                oversend();
                finish();
                break;
            case R.id.shezhitx_shengyin://声音提醒
                if (shezhitx_shengyin.isSelected()) {
                    shezhitx_shengyin.setSelected(false);
                    Futile.saveValue(context,"soundon","off");
                } else {
                    Futile.saveValue(context,"soundon","on");
                    shezhitx_shengyin.setSelected(true);
                }
                break;
            case R.id.shezhitx_zhendong://震动提醒
                if (shezhitx_zhendong.isSelected()) {
                    Futile.saveValue(context,"vibrateon","off");
                    shezhitx_zhendong.setSelected(false);
                } else {
                    Futile.saveValue(context,"vibrateon","on");
                    shezhitx_zhendong.setSelected(true);
                }

                break;
            case R.id.shezhitx_gfxx://官方消息
                if (shezhitx_gfxx.isSelected()) {
                    shezhitx_gfxx.setSelected(false);
                } else {
                    shezhitx_gfxx.setSelected(true);
                }

                break;
            case R.id.shezhitx_tskg://推送开关
                if (shezhitx_tskg.isSelected()) {
                    shezhitx_tskg.setSelected(false);
                } else {
                    shezhitx_tskg.setSelected(true);
                }

                break;
            case R.id.shezhitx_kbtx://开播提醒
                if (shezhitx_kbtx.isSelected()) {
                    shezhitx_kbtx.setSelected(false);
                } else {
                    shezhitx_kbtx.setSelected(true);
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        oversend();
        return;
    }

    private void oversend() {
        if (shezhitx_kbtx.isSelected() && shezhitx_gfxx.isSelected()) {
            type = "0";
        } else if (!shezhitx_kbtx.isSelected() && !shezhitx_gfxx.isSelected()) {
            type = "3";
        } else if (!shezhitx_kbtx.isSelected() && shezhitx_gfxx.isSelected()) {
            type = "2";
        } else if (shezhitx_kbtx.isSelected() && !shezhitx_gfxx.isSelected()) {
            type = "1";
        }
        if(!oldtype.equals(type)){
            remindSet();
        }
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

    /***
     * 提醒设置
     */
    private String type = "";

    private void remindSet() {
        showloading();
        xhttp = new Xhttp(URLManager.remindSet);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("type", type);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

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
     * 提醒设置状态
     */
    private String oldtype = "";
    private void getRemind() {
        showloading();
        xhttp = new Xhttp(URLManager.getRemind);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    oldtype =obj.getString("pushStatus");

                    if(oldtype.equals("0")){
                        shezhitx_kbtx.setSelected(true);
                        shezhitx_gfxx.setSelected(true);
                    }else if(oldtype.equals("3")){
                        shezhitx_kbtx.setSelected(false);
                        shezhitx_gfxx.setSelected(false);
                    }else if(oldtype.equals("2")){
                        shezhitx_kbtx.setSelected(false);
                        shezhitx_gfxx.setSelected(true);
                    }else if(oldtype.equals("1")){
                        shezhitx_kbtx.setSelected(true);
                        shezhitx_gfxx.setSelected(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    shezhitx_kbtx.setSelected(true);
                    shezhitx_gfxx.setSelected(true);
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
