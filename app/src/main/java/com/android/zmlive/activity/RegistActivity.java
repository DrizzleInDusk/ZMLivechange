package com.android.zmlive.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegistActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.regist_activity);
        SMSSDK.initSDK(this, URLManager.MOBAPPKEY, URLManager.MOBSECRET);
        findview();
    }

    private TextView regist_mobile, regist_authcode, regist_getauthcode, regist_password, regist_diqu, regist_gander, regist_next,regist_age, regist_guishu,regist_oldpassword;
    private LinearLayout logincode_xieyi;
    private void findview() {
        logincode_xieyi = (LinearLayout) findViewById(R.id.logincode_xieyi);
        logincode_xieyi.setOnClickListener(this);
        findViewById(R.id.registback).setOnClickListener(this);
        regist_guishu = (TextView) findViewById(R.id.regist_guishu);
        regist_guishu.setOnClickListener(this);
        regist_diqu = (TextView) findViewById(R.id.regist_diqu);
        regist_diqu.setOnClickListener(this);
        regist_gander = (TextView) findViewById(R.id.regist_gander);
        regist_gander.setOnClickListener(this);
        regist_next = (TextView) findViewById(R.id.regist_next);
        regist_next.setOnClickListener(this);
        regist_getauthcode = (TextView) findViewById(R.id.regist_getauthcode);
        regist_getauthcode.setOnClickListener(this);
        regist_mobile = (TextView) findViewById(R.id.regist_mobile);
        regist_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mobile = s.toString().trim();
                if (mobile.length() != 0) {
                    regist_getauthcode.setEnabled(true);
                } else {
                    regist_getauthcode.setEnabled(false);
                }
                canclick();
            }
        });
        regist_authcode = (TextView) findViewById(R.id.regist_authcode);
        regist_authcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                authcode = s.toString().trim();
                canclick();
            }
        });
        regist_oldpassword = (TextView) findViewById(R.id.regist_oldpassword);
        regist_password = (TextView) findViewById(R.id.regist_password);
        regist_password.addTextChangedListener(new TextWatcher() {
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
        regist_age = (TextView) findViewById(R.id.regist_age);
        regist_age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                age = s.toString().trim();
                canclick();
            }
        });
        logincode_xieyi.setSelected(true);
        SMSSDK.registerEventHandler(eh);
    }

    EventHandler eh = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;

            System.out.println("result-----" + result);
            System.out.println("data-----" + data);

            handler.sendMessage(msg);

        }

    };
    private String mobile = "", password = "", authcode = "", city = "", sex = "", age = "";
    private String place="86";
    private void canclick() {
        if (mobile.length() != 0 && password.length() != 0 && authcode.length() != 0&& age.length() != 0) {
            regist_next.setEnabled(true);
        } else {
            regist_next.setEnabled(false);
        }
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.logincode_xieyi:
                if (logincode_xieyi.isSelected()) {
                    logincode_xieyi.setSelected(false);
                } else {
                    logincode_xieyi.setSelected(true);
                    ActivityJumpControl.getInstance(activity).gotoRexieyiActivity();
                }
                break;
            case R.id.registback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.regist_guishu:
                String gs=regist_guishu.getText().toString();
                if(gs.equals("台湾")){
                    regist_guishu.setText("大陆");
                }else{
                    regist_guishu.setText("台湾");
                }
//                popguishu();
                break;
            case R.id.regist_next:
                String yzm=regist_authcode.getText().toString().trim();
                String nl=regist_age.getText().toString().trim();
                if (mobile.length() != 11&&mobile.length() != 10) {
                    showMessage(getResources().getString(R.string.nullmobile));
                    return;
                }
                if (yzm.length() == 0) {
                    showMessage(context.getResources().getString(R.string.authcodehint));
                    return;
                }
                if (password.length() < 6) {
                    showMessage(context.getResources().getString(R.string.nopswhint));
                    return;
                }
                if (password.length() > 12) {
                    showMessage(context.getResources().getString(R.string.nopswhint));
                    return;
                }
                String nps=regist_oldpassword.getText().toString().trim();
                if(!password.equals(nps)){
                    showMessage(context.getResources().getString(R.string.pswbuxiangtong));
                    return;
                }
                if(password.getBytes().length != password.length()){
                    showMessage("输入密码有误！");
                    return;
                }
                if (nl.length() == 0) {
                    showMessage("请输入年龄");
                    return;
                }
                city = regist_diqu.getText().toString();
                sex = regist_gander.getText().toString();
                if (sex.length() == 0) {
                    showMessage(context.getResources().getString(R.string.noganderhint));
                    return;
                }
                if (city.length() == 0) {
                    showMessage(context.getResources().getString(R.string.registqiduhint));
                    return;
                }
                if (!logincode_xieyi.isSelected()) {
                    showMessage(context.getResources().getString(R.string.noxieyi));
                    return;
                }
                SMSSDK.submitVerificationCode(place, mobile, authcode);
//                register();
                break;
            case R.id.regist_getauthcode:
                String guishu = regist_guishu.getText().toString();
                if (mobile.length() != 11&&mobile.length() != 10) {
                    showMessage(getResources().getString(R.string.nullmobile));
                    return;
                }

                if (guishu.equals("台湾")) {
                    place = "886";
                } else {
                    place = "86";
                }
                SMSSDK.getVerificationCode(place, mobile);
//                sendCode(place);
                break;
            case R.id.regist_diqu:
                startActivityForResult(new Intent(this, AddAreaActivity.class), 0x01);
                break;
            case R.id.regist_gander:
                dialogSex();
                break;
            case R.id.dialog_nan:       //选择性别：男
                sex = "男";
                regist_gander.setText(sex);
                dialog.dismiss();
                break;
            case R.id.dialog_nv:        //选择性别：女
                sex = "女";
                regist_gander.setText(sex);
                dialog.dismiss();
                break;
//            case R.id.dialog_dl:        //选择归属地大路
//                regist_guishu.setText("+86");
//                mPopupWindow.dismiss();
//                break;
//            case R.id.dialog_tw:        //选择归属地台湾
//                regist_guishu.setText("+09");
//                mPopupWindow.dismiss();
//                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    private String mAddress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0x01: {
                    mAddress = data.getStringExtra("address");
                    regist_diqu.setText(mAddress);
                }
                break;
            }
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    register();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//验证码已经发送
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            time = new TimeCount(60000, 1000);
                            time.start();
                        }
                    });
                }
            } else {
                ((Throwable) data).printStackTrace();
                showMessage("验证码错误");
            }

        }

    };

    /***
     * 手机注册
     */
    private void register() {
        showloading();
        xhttp = new Xhttp(URLManager.register);
        xhttp.add("mobile", mobile);
        xhttp.add("password", password);
        xhttp.add("yzCode", authcode);
        xhttp.add("city", city);
        xhttp.add("sex", sex);
        xhttp.add("age", age);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mesg = obj.getString("meg");
                    showMessage(mesg);
                    if (status.equals("1")) {
                        login();
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
                    if (status.equals("1")) {
                        String accid = obj.getString("accid");
                        String token = obj.getString("token");
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
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        accid=accid.toLowerCase();
                        doLogin(accid,token);
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

    public void doLogin(final String account, final String token) {
        LoginInfo info = new LoginInfo(account, token); // config...
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                dismissloading();
                Log.e("LoginInfo param",param.toString());
                Futile.saveValue(context, "mbe", mobile);
                Futile.saveValue(context, "psw", password);
                Futile.saveValue(context,"account",account);
                Futile.saveValue(context,"token",token);
                System.out.println("MyApplication>>account>>>>"+account);
                System.out.println("MyApplication>>token>>>>"+token);
                MyCache.setAccount(account);
                ActivityJumpControl.getInstance(activity).gotoMainActivity();
                ActivityJumpControl.getInstance(activity).popAllActivity();
                ChooseLoginActivity.closechoose();
            }

            @Override
            public void onFailed(int code) {
                dismissloading();
                Log.e("account>>>>",account);
                Log.e("token>>>>>",token);
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
    private TimeCount time;

    /***
     * 获取验证码
     */
    private void sendCode(String place) {

        showloading();
        xhttp = new Xhttp(URLManager.sendCode);
        xhttp.add("mobile", mobile);
        xhttp.add("place", place);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mesg = obj.getString("meg");
                    showMessage(mesg);
                    if (status.equals("1")) {
                        time = new TimeCount(60000, 1000);
                        time.start();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showMessage(getResources().getString(R.string.noauthcode));
                }
                dismissloading();
            }

            @Override
            public void onError() {
                showMessage(getResources().getString(R.string.noauthcode));
                dismissloading();
            }
        });
    }


    private AlertDialog dialog;

    /**
     * dialog 选择性别
     */
    private void dialogSex() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_xingbie);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        dialog.findViewById(R.id.dialog_nan).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_nv).setOnClickListener(this);
    }

    /**
     * pop 选择归属地
     */
    private PopupWindow mPopupWindow;

    private void popguishu() {
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_guishudi, null);
        mView.findViewById(R.id.dialog_dl).setOnClickListener(this);
        mView.findViewById(R.id.dialog_tw).setOnClickListener(this);
        mPopupWindow = new PopupWindow(mView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAsDropDown(regist_guishu, 0, 0);

    }


    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            regist_getauthcode.setText(context.getResources().getString(R.string.authcodeyet));
            regist_getauthcode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            regist_getauthcode.setClickable(false);
            regist_getauthcode.setText(millisUntilFinished / 1000 + context.getResources().getString(R.string.authhint));
        }
    }


}
