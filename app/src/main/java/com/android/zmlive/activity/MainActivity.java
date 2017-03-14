package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.zmlive.MyCache;
import com.android.zmlive.R;
import com.android.zmlive.entity.LiveerMess;
import com.android.zmlive.fragment.HomeFragment;
import com.android.zmlive.fragment.MyownFragment;
import com.android.zmlive.permission.MPermission;
import com.android.zmlive.permission.annotation.OnMPermissionDenied;
import com.android.zmlive.permission.annotation.OnMPermissionGranted;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.StatusBarUtil;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    public final static String STREAM_START = "MSG_START_LIVESTREAMING_FINISHED";
    public final static String STREAM_FINISH = "MSG_STOP_LIVESTREAMING_FINISHED";
    public AMapLocationClient mLocationClient = null;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
//                    getDistrict
                    try {
                        String address = amapLocation.getAddress();
                        String district =amapLocation.getDistrict();
                        String city = amapLocation.getCity();
                        String province = amapLocation.getProvince();
                        if (district != null && !district.equals("") && !district.equals("null")) {
                            if (district.indexOf("市") != -1) {
                                district = district.substring(0, district.indexOf("市"));
                            }
                            MyData.LOCCITY = district;
                        } else if (city != null && !city.equals("") && !city.equals("null")) {
                            MyData.LOCCITY = city;
                        } else {
                            MyData.LOCCITY = province;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String address = amapLocation.getAddress();
                        MyData.LOCCITY = address;
                    }
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    LogUtil.showELog("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
    private LinearLayout mainact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.main_activity);
        mainact = (LinearLayout) findViewById(R.id.mainact);
        mtype = "";
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
    }

    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                kickOut(code);
            }
        }
    };

    private void kickOut(StatusCode code) {
        if (code == StatusCode.PWD_ERROR) {
            LogUtil.showELog("Auth", "user password error");
        } else {
            LogUtil.showELog("Auth", "Kicked!");
        }
        onLogout();
    }

    // 注销
    private void onLogout() {
        // 清理缓存&注销监听&清除状态
        if (MainAudienceActivity.onaudience) {
            MainAudienceActivity.finaudience();
        } else if (MainLiveActivity.onlive) {
            MainLiveActivity.finlive();
        }
        showMessage(getResources().getString(R.string.logout));
        NimUIKit.clearCache();
        LoginSyncDataStatusObserver.getInstance().reset();
        showKeyboard(false);
        gotochoss();
    }

    private String mtype = "";
    private int statuestype = 0;

    public void setStatuestype(int statuestype) {
        this.statuestype = statuestype;
    }

    public void setmainact(int color) {
        mainact.setBackgroundColor(color);
    }

    public int getStatuestype() {
        return statuestype;
    }

    public static boolean isonmain = false;

    @Override
    protected void onResume() {
        super.onResume();
        setStatuestype( StatusBarUtil.StatusBarLightMode(activity));
        setmainact( Color.parseColor("#ffffff"));
        isonmain = true;
        UpdateFunGO.onResume(this);
        if (mtype.equals("")) {
            if (MyData.IDENTITY.equals("")) {
                String mbe = Futile.getValue(getApplicationContext(), "mbe");
                String psw = Futile.getValue(getApplicationContext(), "psw");
                if (!TextUtils.isEmpty(mbe) && !TextUtils.isEmpty(psw)) {
                    MyData.MEB = mbe;
                    MyData.MEBL = mbe.substring(0, 3) + getxx(mbe.length() - 7) + mbe.substring(mbe.length() - 4, mbe.length());
                    login(mbe, psw);
                    mtype = "1";
                } else {
                    gotochoss();
                }
            } else {
                showloading();
//                doLogin(MyData.ACCID, MyData.TOKEN);
                setsuccessview();
                registerObservers(true);
                dismissloading();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatusBarUtil.StatusBarDarkMode(activity, statuestype);
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

    private void gotochoss() {
        Futile.romveValue(context, "psw", "1");
        ActivityJumpControl.getInstance(activity).gotoChooseLoginActivity();
        ActivityJumpControl.getInstance(activity).popAllActivity();
    }

    private void setsuccessview() {
        UpdateKey.API_TOKEN = "cdd69801155276c262636b7ed381c988";
        UpdateKey.APP_ID = "58c64372959d692ee60001e9";
        //下载方式:
        UpdateKey.DialogOrNotification = UpdateKey.WITH_DIALOG;//通过Dialog来进行下载
//        UpdateKey.DialogOrNotification=UpdateKey.WITH_NOTIFITION;//通过通知栏来进行下载(默认)
        UpdateFunGO.init(this);
        appShare();

        findview();
        registerBroadcast();
        gotoFragment(1);
        joinroomid = "";
        roompath = "";
        ucid = "";
        liveername = "";
        head = "";
        liveerlist = null;
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(new AMapLocationClientOption().setOnceLocation(true));
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
//启动定位
        mLocationClient.startLocation();
    }

    private RelativeLayout main_tab1, main_tab2, main_tab3;
    private ImageView main_tab1_img, main_tab3_img;

    private void findview() {
        main_tab1_img = (ImageView) findViewById(R.id.main_tab1_img);
        main_tab3_img = (ImageView) findViewById(R.id.main_tab3_img);
        main_tab1 = (RelativeLayout) findViewById(R.id.main_tab1);
        main_tab1.setOnClickListener(this);
        main_tab2 = (RelativeLayout) findViewById(R.id.main_tab2);
        main_tab2.setOnClickListener(this);
        main_tab3 = (RelativeLayout) findViewById(R.id.main_tab3);
        main_tab3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tab1:
                gotoFragment(1);
                break;
            case R.id.main_tab2:
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance((Activity) context).gotoBuildLiveActivity();
                } else {
                    showMessage(getResources().getString(R.string.nulluserhint));
                    ActivityJumpControl.getInstance((Activity) context).gotoChooseLoginActivity();
                    finish();
                }
                break;
            case R.id.main_tab3:
                gotoFragment(2);
                break;
        }
    }

    private Fragment homeFragment;
    private Fragment myownFragment;
    // fragment管理者
    private FragmentManager fragmentManager;
    // 开启一个Fragment事务
    private FragmentTransaction transaction;

    public void gotoFragment(int tag) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if (tag == 1) {
            homeFragment = new HomeFragment();
            transaction.replace(R.id.main_framelayout, homeFragment);
            transaction.commitAllowingStateLoss();
            main_tab1_img.setImageResource(R.mipmap.shouye);
            main_tab3_img.setImageResource(R.mipmap.gerenzhongxin1);
            visandgone(main_tab1);
        } else if (tag == 2) {
            myownFragment = new MyownFragment();
            transaction.replace(R.id.main_framelayout, myownFragment);
            transaction.commitAllowingStateLoss();
            main_tab1_img.setImageResource(R.mipmap.shouye1);
            main_tab3_img.setImageResource(R.mipmap.gerenzhongxin);
            visandgone(main_tab3);
        }

    }

    private ArrayList<RelativeLayout> list = new ArrayList<>();

    private void visandgone(RelativeLayout rl) {
        for (int i = 0; i < list.size(); i++) {
            RelativeLayout r = list.get(i);
//            r.setBackgroundColor(Color.parseColor("#7E3AB3"));
            r.setEnabled(true);
            list.remove(i);
        }
        rl.setEnabled(false);
//        rl.setBackgroundColor(Color.parseColor("#9854CF"));
        list.add(rl);
    }

    public void removelist() {
//        main_tab1.setBackgroundColor(Color.parseColor("#7E3AB3"));
        main_tab1.setEnabled(true);
        main_tab1_img.setImageResource(R.mipmap.shouye1);
        list.clear();
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                showMessage(getResources().getString(R.string.againhint));
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                // 退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                // mDefaultHandler.uncaughtException(thread, ex);
                System.exit(1);
                return true;
            }
        }
        return false;
    }

    private StreamObserver streamObserver;

    private void registerBroadcast() {
        streamObserver = new StreamObserver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(STREAM_FINISH);
        filter.addAction(STREAM_START);
        LocalBroadcastManager.getInstance(this).registerReceiver(streamObserver, filter);
    }

    private void unRegisterBroadcast() {
        if (streamObserver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(streamObserver);
            streamObserver = null;
        }
    }

    private class StreamObserver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (STREAM_FINISH.equals(intent.getAction())) {
                dialogmess();
            } else if (STREAM_START.equals(intent.getAction())) {
            }
        }
    }

    private AlertDialog dialog;

    /**
     * dialog 消息提示
     */
    private void dialogmess() {
        try {
            dialog = new AlertDialog.Builder(context).create();
            dialog.show();
            dialog.setContentView(R.layout.dialog_mess);
            TextView dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
            TextView dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
            dialog_mess_title.setText("直播消息提示！");
            dialog_mess_con.setText("" + getResources().getString(R.string.living_finished));
            dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        isonmain = false;
        registerObservers(false);
        unRegisterBroadcast();
        super.onDestroy();
    }

    public static ArrayList<LiveerMess> liveerlist;
    public static int position;
    public static String roompath = "";
    public static String joinroomid = "";
    public static String ucid = "";
    public static String liveername = "";
    public static String head = "";
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;


    /**
     * 基本权限管理
     */
    public void requestBasicPermission() {
        MPermission.with((Activity) context)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.READ_PHONE_STATE)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        if (roompath.equals("")) {
            showMessage(context.getResources().getString(R.string.livenull));
            return;
        }
        ActivityJumpControl.getInstance(activity).gotoAudienceActivity(roompath, joinroomid, ucid, head, liveername);
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        showMessage(context.getResources().getString(R.string.nullpermiss));
    }

    /***
     * 直播间的分享图片
     */
    private void appShare() {
        xhttp = new Xhttp(URLManager.appShare);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject about = obj.getJSONObject("about");
                    MyData.Dapic = about.getString("mailbox");
                    MyData.Xiaopic = about.getString("telephone");
                } catch (JSONException e) {
                    e.printStackTrace();
                    MyData.Dapic = "";
                    MyData.Xiaopic = "";
                }
            }

            @Override
            public void onError() {
                MyData.Dapic = "";
                MyData.Xiaopic = "";
            }
        });
    }

    /***
     * 登陆
     */
    private void login(String mobile, String password) {

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
                    if (status.equals("1")) {
                        JSONObject user = obj.getJSONObject("userInfo");
//                        String accid = obj.getString("accid");
//                        String token = accid;
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
//                        accid = accid.toLowerCase();
//                        doLogin(accid, token);
                        dismissloading();
                        setsuccessview();
                        registerObservers(true);
                    } else {
                        gotochoss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissloading();
                    gotochoss();
                }
            }

            @Override
            public void onError() {
                dismissloading();
                gotochoss();
            }
        });
    }

    public void doLogin(final String account, final String token) {
        LoginInfo info = new LoginInfo(account, token); // config...
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                dismissloading();
                Futile.saveValue(context, "account", account);
                Futile.saveValue(context, "token", token);
                MyData.ACCOUNT = account;
                MyCache.setAccount(account);
                setsuccessview();
                registerObservers(true);
            }

            @Override
            public void onFailed(int code) {
                dismissloading();
                if (code == 302 || code == 404) {
                    showMessage(getResources().getString(R.string.login_failed));
                } else {
                    showMessage(getResources().getString(R.string.loginexc));
                }
                gotochoss();
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
                dismissloading();
                showMessage(getResources().getString(R.string.login_exception));
                gotochoss();
            }
            // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
        };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }
}
