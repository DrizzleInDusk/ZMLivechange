package com.android.zmlive.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class XuanzhezhifuActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = XuanzhezhifuActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PAYMENT = 1;

//    // PayPal configuration
//    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
//            .environment(URLManager.PAYPAL_ENVIRONMENT).clientId(
//                    URLManager.PAYPAL_CLIENT_ID);
    private String czmon="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xuanzhezhifu_activity);
//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
//        startService(intent);
        czmon=getIntent().getStringExtra("czmon");
        findview();
    }

    private ImageView chongzhi_paypal, chongzhi_daofu;

    private void findview() {
        findViewById(R.id.chongzhiback).setOnClickListener(this);
        findViewById(R.id.chongzhi_next).setOnClickListener(this);
        chongzhi_paypal = (ImageView) findViewById(R.id.chongzhi_paypal);
        chongzhi_paypal.setOnClickListener(this);
        chongzhi_daofu = (ImageView) findViewById(R.id.chongzhi_daofu);
        chongzhi_daofu.setOnClickListener(this);
        chongzhi_paypal.performClick();
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chongzhi_paypal:
                chongzhi_paypal.setSelected(true);
                chongzhi_daofu.setSelected(false);
                break;
            case R.id.chongzhi_daofu:
                chongzhi_paypal.setSelected(false);
                chongzhi_daofu.setSelected(true);
                break;
            case R.id.chongzhi_next:
                if(chongzhi_paypal.isSelected()){
                }else{
                    ActivityJumpControl.getInstance(activity).gotoDaoFUskActivity(czmon);
                }
                break;
            case R.id.chongzhiback:
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
     * 核实付款
     */
    private void paypal(String orders, String money) {
        showloading();
        xhttp = new Xhttp(URLManager.paypal);
        xhttp.add("orders", orders);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("money", money);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        ActivityJumpControl.getInstance(activity).popActivity(activity);
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
