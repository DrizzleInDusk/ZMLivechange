package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

public class TixianBdActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.tixianbd_activity);
        mode = getIntent().getStringExtra("mode");
        totle = getIntent().getStringExtra("totle");
        findview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        totm();
        joinPM();
    }

    private String mode, totle;
    private TextView txzhbd_zfb, txzhbd_yhk, txbd_tv, txzhbd_ljbd, txbd_zhtv;
    private EditText txbd_zhet, txbd_zsxmet;
    private LinearLayout txbd_ll, txbd_topll;
    private ImageView txbd_img;

    private void findview() {
        findViewById(R.id.txzhbdback).setOnClickListener(this);
        findViewById(R.id.txzhbd_bd).setOnClickListener(this);
        txzhbd_ljbd = (TextView) findViewById(R.id.txzhbd_ljbd);
        txzhbd_ljbd.setOnClickListener(this);
        txzhbd_zfb = (TextView) findViewById(R.id.txzhbd_zfb);
        txzhbd_zfb.setOnClickListener(this);
        txzhbd_yhk = (TextView) findViewById(R.id.txzhbd_yhk);
        txzhbd_yhk.setOnClickListener(this);

        txbd_topll = (LinearLayout) findViewById(R.id.txbd_topll);
        txbd_ll = (LinearLayout) findViewById(R.id.txbd_ll);
        txbd_img = (ImageView) findViewById(R.id.txbd_img);
        txbd_tv = (TextView) findViewById(R.id.txbd_tv);
        txbd_zhtv = (TextView) findViewById(R.id.txbd_zhtv);
        txbd_zhet = (EditText) findViewById(R.id.txbd_zhet);
        txbd_zsxmet = (EditText) findViewById(R.id.txbd_zsxmet);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    /***
     * 点击事件
     */
    private String issel = "1";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txzhbd_zfb://绑定支付宝
                if (alipay.equals("")) {
                    txzhbd_ljbd.setText(getResources().getString(R.string.ljbd));
                } else {
                    txzhbd_ljbd.setText(getResources().getString(R.string.ljtx));
                }
                issel = "1";
                type = "alipay";
                txzhbd_zfb.setSelected(true);
                txzhbd_yhk.setSelected(false);
                break;
            case R.id.txzhbd_yhk://绑定银行卡
                if (bank.equals("")) {
                    txzhbd_ljbd.setText(getResources().getString(R.string.ljbd));
                } else {
                    txzhbd_ljbd.setText(getResources().getString(R.string.ljtx));
                }
                issel = "2";
                type = "bank";
                txzhbd_zfb.setSelected(false);
                txzhbd_yhk.setSelected(true);
                break;
            case R.id.txzhbd_ljbd://立即绑定
                if (issel.equals("1")) {
                    if (alipay.equals("")) {
                        txbd_img.setImageResource(R.mipmap.zhifubao);
                        txbd_tv.setText(getResources().getString(R.string.bdhint) + getResources().getString(R.string.zfb) + getResources().getString(R.string.bdhint2));
                        txbd_zhet.setHint(getResources().getString(R.string.qingshuru) + getResources().getString(R.string.zfb) + getResources().getString(R.string.zh));
                        txbd_zhtv.setText(getResources().getString(R.string.zfb) + getResources().getString(R.string.zh));
                        txbd_zhet.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
                    } else {
                        if(MyData.TRPASS.equals("")){
                            dialogmess();
                        }else{
                            ActivityJumpControl.getInstance(activity).gotoTixianActivity(mode, "alipay", totle,alipay);
                            finish();
                        }
                        return;
                    }
                } else if (issel.equals("2")) {
                    if (bank.equals("")) {
                        txbd_img.setImageResource(R.mipmap.yinhang);
                        txbd_tv.setText(getResources().getString(R.string.bdhint) + getResources().getString(R.string.yhk) + getResources().getString(R.string.bdhint2));
                        txbd_zhet.setHint(getResources().getString(R.string.qingshuru) + getResources().getString(R.string.yhk) + getResources().getString(R.string.zh));
                        txbd_zhtv.setText(getResources().getString(R.string.yhk) + getResources().getString(R.string.zh));
                        txbd_zhet.setInputType(InputType.TYPE_CLASS_PHONE);
                    } else {
                        if(MyData.TRPASS.equals("")){
                            dialogmess();
                        }else{
                            ActivityJumpControl.getInstance(activity).gotoTixianActivity(mode, "bank", totle,bank);
                            finish();
                        }
                        return;
                    }
                }
                txbd_zsxmet.setHint(getResources().getString(R.string.qingshuru) + getResources().getString(R.string.zhenshixingming));
                txbd_ll.setVisibility(View.VISIBLE);
                txbd_topll.setVisibility(View.GONE);
                issel = "3";
                break;
            case R.id.txzhbd_bd://绑定
                String account = txbd_zhet.getText().toString().trim();
                String pname = txbd_zsxmet.getText().toString().trim();
                if(account.equals("")){
                    showMessage(getResources().getString(R.string.qingshuru)  + getResources().getString(R.string.zh));
                    return;
                }
                if(pname.equals("")){
                    showMessage(getResources().getString(R.string.qingshuru) + getResources().getString(R.string.zhenshixingming));
                    return;
                }
                if(account.getBytes().length != account.length()){
                    showMessage(getResources().getString(R.string.zher));
                    return;
                }
                if(account.length() >25){
                    showMessage(getResources().getString(R.string.zher));
                    return;
                }
                if (issel.equals("1")) {
                    if(account.length() <10){
                        showMessage(getResources().getString(R.string.zher));
                        return;
                    }
                }else {
                    if(account.length() <6){
                        showMessage(getResources().getString(R.string.zher));
                        return;
                    }
                }
                if (!account.equals("") && !pname.equals("")) {
                    binding(account, pname);
                }
                break;
            case R.id.txzhbdback:
                if (issel.equals("3")) {
                    txbd_ll.setVisibility(View.INVISIBLE);
                    txbd_topll.setVisibility(View.VISIBLE);
                    txbd_zhet.setText("");
                    txbd_zsxmet.setText("");
                    txzhbd_zfb.performClick();
                    showKeyboard(false);
                } else {
                    ActivityJumpControl.getInstance(activity).popActivity(activity);
                    finish();
                }
                break;
            case R.id.dialog_mess_yes:
                ActivityJumpControl.getInstance(activity).gotoShezhijymmActivity();
                dialog.dismiss();
                break;
            case R.id.dialog_mess_quxiao:
                dialog.dismiss();
                break;
        }
    }

    private AlertDialog dialog;
    /**
     * dialog 消息提示
     */
    private TextView dialog_mess_title, dialog_mess_con,dialog_mess_yes;

    private void dialogmess() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_mess);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
        dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
        dialog_mess_title.setText("消息提示！");
        dialog_mess_con.setText("还没有设置交易密码！请先设置");
        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(this);
        dialog_mess_yes= (TextView) dialog.findViewById(R.id.dialog_mess_yes);
        dialog_mess_yes.setText("去设置");
        dialog_mess_yes.setOnClickListener(this);
    }
    /***
     * 判断有没有支付密码
     */
    private void joinPM() {
        showloading();
        xhttp = new Xhttp(URLManager.joinPM);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    MyData.TRPASS = obj.getString("trPass");
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
     * 提现帐号绑定
     */
    private String type;

    private void binding(String account, String pname) {
        showloading();
        xhttp = new Xhttp(URLManager.binding);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("type", type);
        xhttp.add("account", account);
        xhttp.add("pname", pname);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String mess= obj.getString("meg");
                    showMessage(mess);
                    if(mess.contains("成功")){
                        ActivityJumpControl.getInstance(activity).gotoTixianBdActivity(mode,totle);
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
     * 进入提现页面
     */
    private String bank = "", alipay = "";

    private void totm() {
        showloading();
        xhttp = new Xhttp(URLManager.totm);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    bank = obj.getString("bank");
                    alipay = obj.getString("alipay");
                    txzhbd_zfb.performClick();
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
