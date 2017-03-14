package com.android.zmlive.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.android.zmlive.MyCache;
import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.login_activity);
        findview();
    }


    private TextView login_passkey, login_mobile, login_tv;

    private void findview() {
        login_passkey = (TextView) findViewById(R.id.login_passkey);
        login_passkey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString().trim();
                canclick();
            }
        });
        login_mobile = (TextView) findViewById(R.id.login_mobile);
        login_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mobile = s.toString().trim();
                canclick();
            }
        });
        findViewById(R.id.loginback).setOnClickListener(this);
        login_tv = (TextView) findViewById(R.id.login_tv);
        login_tv.setOnClickListener(this);
        findViewById(R.id.fogetpass_tv).setOnClickListener(this);
        String mbe = Futile.getValue(getApplicationContext(), "mbe");
        if (!TextUtils.isEmpty(mbe)) {
            login_mobile.setText(mbe);
            mobile = mbe;
        }
    }

    private String mobile = "", password = "";

    private void canclick() {
        if (password.length() != 0 && mobile.length() != 0) {
            login_tv.setEnabled(true);
        } else {
            login_tv.setEnabled(false);
        }
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.loginback://
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.login_tv://登陆
                showKeyboard(false);
                if (mobile.length() != 11 && mobile.length() != 10) {
                    showMessage(getResources().getString(R.string.nullmobile));
                    return;
                }
                login();
                break;
            case R.id.fogetpass_tv://忘记密码
                ActivityJumpControl.getInstance(activity).gotoForgetPassActivity();
                break;
        }
    }


    /***
     * 登陆
     */
    private void login() {

        showloading();
        xhttp = new Xhttp(URLManager.login);
        xhttp.add("mobile", mobile);
        xhttp.add("password", password);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mesg = obj.getString("meg");
                    showMessage(mesg);
                    System.out.println("doLogin result = "+result);
                    if (status.equals("1")) {
//                        String accid = obj.getString("accid");
//                        String token = accid;
                        JSONObject user = obj.getJSONObject("userInfo");
                        MyData.USERID = user.getString("uid");
                        MyData.AGE = user.getString("age");
                        MyData.COLOUR = user.getString("colour");
                        MyData.CONCERN = user.getString("concern");
                        MyData.FANS = user.getString("fans");
                        MyData.HEAD = user.getString("head");
                        MyData.HEIGHT = user.getString("height");
                        MyData.LEVEL = user.getString("level");
                        MyData.NICKNAME = user.getString("nickname");
                        MyData.PRAISE = user.getString("praise");
                        MyData.SEX = user.getString("sex");
                        MyData.WEIGHT = user.getString("weight");
                        MyData.CITY = user.getString("city");
                        try {
                            MyData.PROFIT = obj.getString("profit");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //accid = accid.toLowerCase();
                        //doLogin(accid, token);
                        dismissloading();
                        Futile.saveValue(context, "mbe", mobile);
                        Futile.saveValue(context, "psw", password);
                        ActivityJumpControl.getInstance(activity).gotoMainActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("doLogin error");
                }
                dismissloading();
            }

            @Override
            public void onError() {
                dismissloading();
            }
        });
    }

    public void doLogin(final String account, final String token) {
        System.out.println("doLogin start");
        LoginInfo info = new LoginInfo(account, token); // config...
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                dismissloading();
                Futile.saveValue(context, "mbe", mobile);
                Futile.saveValue(context, "psw", password);
                Futile.saveValue(context, "account", account);
                Futile.saveValue(context, "token", token);
                MyCache.setAccount(account);
                ActivityJumpControl.getInstance(activity).gotoMainActivity();
                ActivityJumpControl.getInstance(activity).popAllActivity();
                ChooseLoginActivity.closechoose();
            }

            @Override
            public void onFailed(int code) {
                System.out.println("doLogin code = "+code);
                dismissloading();
                if (code == 302 || code == 404) {
                    showMessage(getResources().getString(R.string.login_failed));
                } else {
                    showMessage(getResources().getString(R.string.loginexc));
                }
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
                dismissloading();
                showMessage(getResources().getString(R.string.login_exception));

            }
            // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
        };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }
}
