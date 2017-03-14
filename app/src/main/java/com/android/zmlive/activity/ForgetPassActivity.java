package com.android.zmlive.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPassActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.forgetpass_activity);
        SMSSDK.initSDK(this, URLManager.MOBAPPKEY, URLManager.MOBSECRET);
        findview();
    }

    private TextView fogetpass_mobile, fogetpass_authcode, fogetpass_password, fogetpass_getauthcode,fogetpass_guishu;

    private void findview() {
        findViewById(R.id.fogetpassback).setOnClickListener(this);
        findViewById(R.id.fogetpass_yes).setOnClickListener(this);
        fogetpass_getauthcode = (TextView) findViewById(R.id.fogetpass_getauthcode);
        fogetpass_getauthcode.setOnClickListener(this);
        fogetpass_guishu = (TextView) findViewById(R.id.fogetpass_guishu);
        fogetpass_guishu.setOnClickListener(this);
        fogetpass_mobile = (TextView) findViewById(R.id.fogetpass_mobile);
        fogetpass_authcode = (TextView) findViewById(R.id.fogetpass_authcode);
        fogetpass_password = (TextView) findViewById(R.id.fogetpass_password);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private String password = "", authcode = "", mobile = "";

    private String place;
    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fogetpassback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.fogetpass_guishu:
                String gs=fogetpass_guishu.getText().toString();
                if(gs.equals("台湾")){
                    fogetpass_guishu.setText("大陆");
                }else{
                    fogetpass_guishu.setText("台湾");
                }
//                popguishu();
                break;
            case R.id.fogetpass_getauthcode:
                mobile = fogetpass_mobile.getText().toString().trim();
                String  guishu = fogetpass_guishu.getText().toString();
                if (mobile.length() == 0) {
                    showMessage(context.getResources().getString(R.string.mobilehint));
                    return;
                }
                if (mobile.length() != 11&&mobile.length() != 10) {
                    showMessage(getResources().getString(R.string.nullmobile));
                    return;
                }
                if (guishu.equals("台湾")) {
                    place = "09";
                } else {
                    place = "86";
                }
                SMSSDK.getVerificationCode(place, mobile);
//                sendCode(place);
                break;
            case R.id.fogetpass_yes:
                showKeyboard(false);
                mobile = fogetpass_mobile.getText().toString().trim();
                authcode = fogetpass_authcode.getText().toString().trim();
                password = fogetpass_password.getText().toString().trim();
                if (mobile.length() == 0) {
                    showMessage(context.getResources().getString(R.string.mobilehint));
                    return;
                }
                if (authcode.length() == 0) {
                    showMessage(context.getResources().getString(R.string.authcodehint));
                    return;
                }
                if (password.length() == 0) {
                    showMessage(context.getResources().getString(R.string.registpasshint));
                    return;
                }
                if(password.getBytes().length != password.length()){
                    showMessage("输入密码有误！");
                    return;
                }
                SMSSDK.submitVerificationCode(place, mobile, authcode);
//                forgetPassword();
                break;
//            case R.id.dialog_dl:        //选择归属地大路
//                fogetpass_guishu.setText("+86");
//                mPopupWindow.dismiss();
//                break;
//            case R.id.dialog_tw:        //选择归属地台湾
//                fogetpass_guishu.setText("+09");
//                mPopupWindow.dismiss();
//                break;
        }
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
        mPopupWindow.showAsDropDown(fogetpass_guishu, 0, 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
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
                    forgetPassword();
//                    register();
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
    private TimeCount time;

    /***
     * 获取验证码
     */
    private void sendCode( String place) {
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
     * 忘记密码
     */
    private void forgetPassword() {
        showloading();
        xhttp = new Xhttp(URLManager.forgetPassword);
        xhttp.add("mobile", mobile);
        xhttp.add("password", password);
        xhttp.add("yzCode", authcode);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mesg = obj.getString("meg");
                    showMessage(mesg);
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

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            fogetpass_getauthcode.setText(context.getResources().getString(R.string.authcodeyet));
            fogetpass_getauthcode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            fogetpass_getauthcode.setClickable(false);
            fogetpass_getauthcode.setText(millisUntilFinished / 1000 + context.getResources().getString(R.string.authhint));
        }
    }
}
