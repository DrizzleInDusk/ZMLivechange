package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

public class TixianActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        mode = getIntent().getStringExtra("mode");
        type = getIntent().getStringExtra("type");
        totle = getIntent().getStringExtra("totle");
        bankalipay = getIntent().getStringExtra("bankalipay");
        setContentView(R.layout.tixian_activity);
        findview();
    }

    private String type, mode, totle, bankalipay;
    private TextView tx_bzye, tx_txzh, tx_dzrmb, tx_txbzet;

    private void findview() {
        findViewById(R.id.txback).setOnClickListener(this);
        findViewById(R.id.txzh_ljtx).setOnClickListener(this);
        tx_bzye = (TextView) findViewById(R.id.tx_bzye);
        tx_bzye.setText(totle);
        tx_txzh = (TextView) findViewById(R.id.tx_txzh);
        tx_txzh.setOnClickListener(this);
        tx_txbzet = (TextView) findViewById(R.id.tx_txbzet);
        tx_dzrmb = (TextView) findViewById(R.id.tx_dzrmb);
        tx_txbzet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String mon = s.toString();
                tx_dzrmb.setText(mon + "元");
            }
        });
        String txzh = "";
        String xx = "";
        if (type.equals("bank")) {
            try {
                xx = getxx(bankalipay.length() - 7);
                txzh = bankalipay.substring(0, 4);
                txzh = txzh + xx + bankalipay.substring(bankalipay.length() - 3, bankalipay.length());
            } catch (Exception e) {
                e.printStackTrace();
                txzh = bankalipay;
            }
        } else {
            try {
                int we;
                int in;
                if (bankalipay.indexOf("@") != -1) {
                    in=bankalipay.indexOf("@");
                    we=in ;
                    if(we<9){
                        we= we - 4;
                    }else{
                        we= we - 6;
                    }
                } else {
                    in=bankalipay.length();
                    we =in - 7;
                }
                xx = getxx(we);
                if(in>6){
                    txzh = bankalipay.substring(0, 3);
                    txzh = txzh + xx + bankalipay.substring(in-3, bankalipay.length());
                }else{
                    txzh=bankalipay;
                }
            } catch (Exception e) {
                e.printStackTrace();
                txzh = bankalipay;
            }
        }
        tx_txzh.setText(txzh);
    }

    private String getxx(int leng) {
        String xx = "";
        for (int i = 0; i < leng; i++) {
            if (i % 4 == 0) {
                xx = xx + " ";
            }
            xx = xx + "*";
        }
        return xx + " ";
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txzh_ljtx://提现
                String txbz = tx_txbzet.getText().toString();
                if (txbz.equals("")) {
                    showMessage(getResources().getString(R.string.txbzhint));
                    return;
                }
                if (txbz.contains(".")) {
                    showMessage(getResources().getString(R.string.txbzhint));
                    return;
                }
                int tm = Integer.parseInt(txbz);
                int to = Integer.parseInt(totle);
                if (tm < 200) {
                    showMessage(getResources().getString(R.string.txbzhint));
                    return;
                }
                if (tm > to) {
                    showMessage(getResources().getString(R.string.txbzer));
                    return;
                }
                dialogName(txbz);
                break;

            case R.id.tx_txzh:
//                ActivityJumpControl.getInstance(activity).gotoTixianBdActivity(mode, totle);
//                finish();
                break;
            case R.id.txback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.dialog_confirm:
                String mm = content.getText().toString().trim();
                if (mm.equals("")) {
                    showMessage(getResources().getString(R.string.qingshuru)+getResources().getString(R.string.jymm));
                    return;
                }
                atm(money, mm);
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private AlertDialog dialog;

    /**
     * dialog 交易密码
     */
    private EditText content;
    private String money = "";

    private void dialogName(String txbz) {
        money = txbz;
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_jymm, null);
        dialog.setView(view);//取消
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        content = (EditText) view.findViewById(R.id.dialog_jymmet);
        content.setInputType(InputType.TYPE_CLASS_PHONE);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
    }

    /***
     * 提现
     */
    private void atm(String money, String password) {
        showloading();
        xhttp = new Xhttp(URLManager.atm);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("type", type);
        xhttp.add("mode", mode);
        xhttp.add("money", money);
        xhttp.add("password", password);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (mess.contains("成功")) {
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
