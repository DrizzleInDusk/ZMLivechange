package com.android.zmlive.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.utils.BaseActivity;

public class ChongzhiActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.chongzhi_activity);
        bzye = getIntent().getStringExtra("bzye");
        findview();
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private String bzye;
    private TextView cz_bzye, cz_txbzet;

    private void findview() {
        findViewById(R.id.czback).setOnClickListener(this);
        cz_bzye = (TextView) findViewById(R.id.cz_bzye);
        cz_bzye.setText(bzye);
        cz_txbzet = (TextView) findViewById(R.id.cz_txbzet);
        findViewById(R.id.cz_chongzhi).setOnClickListener(this);
        findViewById(R.id.cz_5).setOnClickListener(this);
        findViewById(R.id.cz_10).setOnClickListener(this);
        findViewById(R.id.cz_30).setOnClickListener(this);
        findViewById(R.id.cz_60).setOnClickListener(this);
        findViewById(R.id.cz_1b).setOnClickListener(this);
        findViewById(R.id.cz_2q5).setOnClickListener(this);
        findViewById(R.id.cz_1w).setOnClickListener(this);

    }

    /***
     * 点击事件
     */
    private String czmon="0";
    @Override
    public void onClick(View v) {
        czmon="0";
        switch (v.getId()) {
            case R.id.cz_chongzhi: {//充值
                String czje = cz_txbzet.getText().toString().trim();
                if (czje.equals("")) {
                    showMessage(getResources().getString(R.string.qsrje));
                    return;
                }
                int m = Integer.parseInt(czje);
                if (m == 0) {
                    showMessage(getResources().getString(R.string.qsrje));
                    return;
                }
                czmon=czje;
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_5: {//充值5元
                czmon="5";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_10: {//充值10元
                czmon="10";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_30: {//充值30元
                czmon="30";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_60: {//充值60元
                czmon="60";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_1b: {//充值1000元
                czmon="1000";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_2q5: {//充值2500元
                czmon="2500";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.cz_1w: {//充值10000元
                czmon="10000";
                ActivityJumpControl.getInstance(activity).gotoXuanzhezhifuActivity(czmon);
            }
            break;
            case R.id.czback: {
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

}
