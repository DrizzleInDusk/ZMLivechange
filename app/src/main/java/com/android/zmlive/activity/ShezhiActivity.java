package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;

public class ShezhiActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shezhi_activity);
        verifyCAMERAPermissions(activity);
        findview();
        setsize();
    }

    private TextView shezhi_hcsize;

    private void findview() {
        shezhi_hcsize = (TextView) findViewById(R.id.shezhi_hcsize);
        findViewById(R.id.shezhiback).setOnClickListener(this);
        findViewById(R.id.shezhi_tixingshezhi).setOnClickListener(this);
        findViewById(R.id.shezhi_heimingdan).setOnClickListener(this);
        findViewById(R.id.shezhi_lianxiwomen).setOnClickListener(this);
        findViewById(R.id.shezhi_guanyuwomen).setOnClickListener(this);
        findViewById(R.id.shezhi_qingchuhuancun).setOnClickListener(this);
        findViewById(R.id.shezhi_banbenxinxi).setOnClickListener(this);
        findViewById(R.id.shezhi_mimaguanli).setOnClickListener(this);
        findViewById(R.id.shezhi_tuichudenglu).setOnClickListener(this);
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shezhiback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.shezhi_tixingshezhi://设置提醒
                if(MyData.IDENTITY.equals("")){
                    ActivityJumpControl.getInstance(activity).gotoShezhiTxActivity();
                }else{
                    showMessage(context.getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.shezhi_heimingdan://黑名单
                ActivityJumpControl.getInstance(activity).gotoShezhiHmdActivity();
                break;
            case R.id.shezhi_lianxiwomen://联系我们
                ActivityJumpControl.getInstance(activity).gotoShezhiLxwmActivity();
                break;
            case R.id.shezhi_guanyuwomen://关于我们
                ActivityJumpControl.getInstance(activity).gotoShezhiGywmActivity();
                break;
            case R.id.shezhi_qingchuhuancun://清除缓存
                islogout = false;
                dialogmess();
                break;
            case R.id.shezhi_banbenxinxi://版本信息

                UpdateKey.API_TOKEN = "7db64c9820a1e9057b189301b278178c";
                UpdateKey.APP_ID = "5819aeffca87a80576000e2f";
                //下载方式:
                UpdateKey.DialogOrNotification = UpdateKey.WITH_DIALOG;//通过Dialog来进行下载
                //UpdateKey.DialogOrNotification=UpdateKey.WITH_NOTIFITION;//通过通知栏来进行下载(默认)
                UpdateFunGO.manualStart(this);
                break;
            case R.id.shezhi_mimaguanli://密码管理
                ActivityJumpControl.getInstance(activity).gotoShezhiMmglActivity();
                break;
            case R.id.shezhi_tuichudenglu://退出登录
                islogout = true;
                dialogmess();
                break;
            case R.id.dialog_mess_quxiao://dialog 取消
                dialog.dismiss();
                break;
            case R.id.dialog_mess_yes://dialog 确认
                if (islogout) {
                    NIMClient.getService(AuthService.class).logout();
                    Futile.romveValue(context, "psw", "1");
                    Futile.romveValue(context, "account", "1");
                    Futile.romveValue(context, "token", "1");
                    Futile.saveValue(context,"soundon","");
                    Futile.saveValue(context,"vibrateon","");
                    ActivityJumpControl.getInstance(activity).gotoChooseLoginActivity();
                    ActivityJumpControl.getInstance(activity).popAllActivity();
                } else {
                    Futile.clearAllCache(context);
                    setsize();
                }
                dialog.dismiss();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    private AlertDialog dialog;
    /**
     * dialog 消息提示
     */
    private TextView dialog_mess_title, dialog_mess_con;
    private boolean islogout = false;

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
        dialog_mess_title.setText("消息提示");
        if (islogout) {
            dialog_mess_con.setText("是否退出朕在赏?");
        } else {
            dialog_mess_con.setText("是否要清除缓存?");
        }
        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(this);
    }

    private String cacheSize;

    private void setsize() {
        try {
            cacheSize = Futile.getTotalCacheSize(context);
        } catch (Exception e) {
            e.printStackTrace();
            cacheSize = "00.00KB";
        }

        shezhi_hcsize.setText(cacheSize);
    }

    DialogInterface.OnClickListener listenner = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == AlertDialog.BUTTON_POSITIVE) {
                Futile.clearAllCache(context);
                setsize();
            } else if (i == AlertDialog.BUTTON_NEGATIVE) {

            }
        }
    };

    /**
     * 新增
     */
    // CAMERA Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CLEAR_APP_CACHE, Manifest.permission.GET_ACCOUNTS_PRIVILEGED};

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyCAMERAPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CLEAR_APP_CACHE);
        int permission1 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.GET_ACCOUNTS_PRIVILEGED);

        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /***
     * 开启直播
     */
    private String password = "";

    private void buildliveroom() {

        showloading();
        xhttp = new Xhttp(URLManager.creatRoom);
        xhttp.add("password", password);
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
}
