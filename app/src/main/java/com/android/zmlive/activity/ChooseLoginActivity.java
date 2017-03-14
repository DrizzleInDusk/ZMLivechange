package com.android.zmlive.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.android.zmlive.R;
import com.android.zmlive.permission.MPermission;
import com.android.zmlive.permission.annotation.OnMPermissionDenied;
import com.android.zmlive.permission.annotation.OnMPermissionGranted;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.utils.BaseActivity;

public class ChooseLoginActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooselogin_activity);
        chooseLoginActivity = this;
        findview();
        requestBasicPermission();
    }

    private static ChooseLoginActivity chooseLoginActivity;

    public static void closechoose() {
        chooseLoginActivity.finish();
    }

    /**
     * 基本权限管理
     */
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;

    private void requestBasicPermission() {
        MPermission.with(activity)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        showMessage(context.getResources().getString(R.string.nullpermiss));
    }

    private void findview() {
        findViewById(R.id.gotologin_tv).setOnClickListener(this);
        //findViewById(R.id.gotologincode_tv).setOnClickListener(this);
        findViewById(R.id.gotomobileregist_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gotologin_tv:
                ActivityJumpControl.getInstance(activity).gotoLoginActivity();
                break;
//            case R.id.gotologincode_tv:
//                ActivityJumpControl.getInstance(activity).gotoLoginCodeActivity();
//                break;
            case R.id.gotomobileregist_tv:
                ActivityJumpControl.getInstance(activity).gotoRegistActivity();
                break;
        }
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
                ActivityJumpControl.getInstance(activity).popAllActivity();
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

}
