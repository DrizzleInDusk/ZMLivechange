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

public class ShezhijymmActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhijymm_activity);
        findview();
    }

    private TextView jymm_toptv,jymm_oldpsw, jymm_newpsw, jymm_aginenewpsw;
    private LinearLayout jymm_oldpswll;
    private void findview() {
        findViewById(R.id.jymm_back).setOnClickListener(this);
        jymm_oldpswll = (LinearLayout) findViewById(R.id.jymm_oldpswll);
        jymm_toptv = (TextView) findViewById(R.id.jymm_toptv);
        jymm_oldpsw = (TextView) findViewById(R.id.jymm_oldpsw);
        jymm_newpsw = (TextView) findViewById(R.id.jymm_newpsw);
        jymm_aginenewpsw = (TextView) findViewById(R.id.jymm_aginenewpsw);
        findViewById(R.id.jymm_wancheng).setOnClickListener(this);
        if(MyData.TRPASS.equals("")){
            jymm_toptv.setText(getResources().getString(R.string.szjymm));
            jymm_oldpswll.setVisibility(View.GONE);
        }else{
            jymm_toptv.setText(getResources().getString(R.string.xgjymm));
            jymm_oldpswll.setVisibility(View.VISIBLE);
        }
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
            case R.id.jymm_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.jymm_wancheng://完成
                String newpsw = jymm_newpsw.getText().toString().trim();
                String aginenewpsw = jymm_aginenewpsw.getText().toString().trim();
                if (newpsw.length()<6) {
                    showMessage("交易密码不能少于6位");
                    return;
                }
                if (newpsw.length()>10) {
                    showMessage("交易密码不能多于10位");
                    return;
                }
                if (!newpsw.equals(aginenewpsw)) {
                    showMessage(getResources().getString(R.string.pswbuxiangtong));
                    return;
                }
                if(MyData.TRPASS.equals("")){
                    setTRPassword(newpsw, aginenewpsw);
                }else{
                    String oldpsw = jymm_oldpsw.getText().toString().trim();
                    if (oldpsw.equals("")) {
                        showMessage(getResources().getString(R.string.oldpswhint));
                        return;
                    }
                    if (!oldpsw.equals(MyData.TRPASS)) {
                        showMessage(getResources().getString(R.string.oldpswer));
                        return;
                    }
                    editPassword(oldpsw, newpsw, aginenewpsw);
                }
                break;
        }
    }

    /***
     * 修改支付密码
     */
    private void editPassword(String opass, String npass, String cpass) {
        showloading();
        xhttp = new Xhttp(URLManager.editPassword);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("opass", opass);
        xhttp.add("npass", npass);
        xhttp.add("cpass", cpass);
        xhttp.add("type", "tr");
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    showMessage(obj.getString("meg"));
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

    /***
     * 设置支付密码
     */
    private void setTRPassword(String npass, String cpass) {
        showloading();
        xhttp = new Xhttp(URLManager.setTRPassword);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("npass", npass);
        xhttp.add("cpass", cpass);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    showMessage(obj.getString("meg"));
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
