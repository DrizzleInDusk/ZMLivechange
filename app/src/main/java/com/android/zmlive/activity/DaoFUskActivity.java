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

public class DaoFUskActivity extends BaseActivity implements View.OnClickListener {
    private String czmon="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.daofushoukuan_activity);
        czmon=getIntent().getStringExtra("czmon");
        findview();
    }

    private TextView dffk_skdz, dffk_qksj,dffk_czje, dffk_xmnc, dffk_lldh;

    private void findview() {
        findViewById(R.id.dfskback).setOnClickListener(this);
        findViewById(R.id.dffk_qrsc).setOnClickListener(this);
        findViewById(R.id.dffk_fh).setOnClickListener(this);
        dffk_skdz = (TextView) findViewById(R.id.dffk_skdz);//地址
        dffk_qksj = (TextView) findViewById(R.id.dffk_qksj);//金额
        dffk_czje = (TextView) findViewById(R.id.dffk_czje);//金额
        dffk_xmnc = (TextView) findViewById(R.id.dffk_xmnc);//姓名昵称
        dffk_lldh = (TextView) findViewById(R.id.dffk_lldh);//联络电话
        dffk_czje.setHint(czmon);
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dffk_qrsc://确认
                String dz = dffk_skdz.getText().toString().trim();
                if (dz.length() == 0) {
                    showMessage(getResources().getString(R.string.qsrskdz));
                    return;
                }
                String sj = dffk_qksj.getText().toString().trim();
                if (sj.length() == 0) {
                    showMessage(getResources().getString(R.string.qsrsj));
                    return;
                }
                String je = dffk_czje.getText().toString().trim();
                if (je.equals("")) {
                    je=czmon;
                }
                String nc = dffk_xmnc.getText().toString().trim();
                if (nc.length() == 0) {
                    showMessage(getResources().getString(R.string.qsrxmnc));
                    return;
                }
                String dh = dffk_lldh.getText().toString().trim();
                if (dh.length() == 0) {
                    showMessage(getResources().getString(R.string.qsrlldh));
                    return;
                }
                theDoor(dz,sj,je,nc,dh);
                break;
            case R.id.dffk_fh://取消
                finish();
                break;
            case R.id.dfskback:
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
     * 到府收款
     */
    private void theDoor(String place,String stime,String money,String name,String mobile) {
        showloading();
        xhttp = new Xhttp(URLManager.theDoor);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("place", place);
        xhttp.add("stime", stime);
        xhttp.add("money", money);
        xhttp.add("name", name);
        xhttp.add("mobile", mobile);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status=obj.getString("status");
                    String meg=obj.getString("meg");
                    showMessage(meg);
                    if(status.equals("1")){
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
