package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

public class BuildLiveActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.buildlive_activity);

        cameraPermissions(activity);
        findview();
        hasbuild = true;
    }
    // CAMERA Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE};
    /**
     * grant permissions
     * @param activity
     */
    public static void cameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        int permission1 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        int permission2 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED||permission1 != PackageManager.PERMISSION_GRANTED
                ||permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    private boolean hasbuild;

    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.buildlive_back://
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.buildlive_mi://添加密码
                dialogsendmess();
                break;
            case R.id.buildlive_ti://选择话题
                startActivityForResult(new Intent(this, XuanzehuatiActivity.class), 10086);
                break;
            case R.id.buildlive_loc://选择定位城市
                startActivityForResult(new Intent(this, AddAreaActivity.class), 0x01);
                break;
            case R.id.buildlive_startlive://开始直播選擇城市
                cname = buildlive_title.getText().toString().trim();
                city = buildlive_loc.getText().toString().trim();
                if (city.equals("选择城市") || city.equals("")) {
                    showMessage(context.getResources().getString(R.string.nocity));
                    return;
                }
                if (cname.equals("")) {
                    showMessage(context.getResources().getString(R.string.nolivetitle));
                    return;
                }
                if (cname.length() > 20) {
                    showMessage("标题不能超过20字");
                    return;
                }
                if (hasbuild) {
                    hasbuild = false;
                    buildliveroom();
                }
                break;
            case R.id.buildlive_qq://QQ分享
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.buildlive_wx://微信分享
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.buildlive_line://line分享
                new ShareAction(activity).setPlatform(SHARE_MEDIA.LINE)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.buildlive_fb://facebook分享
                new ShareAction(activity).setPlatform(SHARE_MEDIA.FACEBOOK)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                break;
            case R.id.dialog_cancel://添加密码 取消
                password = "";
                dialog.dismiss();
                break;
            case R.id.dialog_confirm://添加密码 确认
                password = dialog_livemima.getText().toString().trim();
                if (password.equals("")) {
                    showMessage("请输入直播间密码");
                    return;
                }
                if (password.getBytes().length != password.length()) {
                    showMessage("直播间密码有误");
                    return;
                }
                if (password.length() > 8) {
                    showMessage("直播间密码太长");
                    return;
                }
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
    private EditText dialog_livemima;

    private void dialogsendmess() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_shezhilivemima, null);
        dialog.setView(view);
        dialog.show();
        dialog_livemima = (EditText) view.findViewById(R.id.dialog_livemima);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
    }

    private RoundedImageView buildlive_head;
    private LinearLayout buildlive_layout;
    private TextView buildlive_loc,buildlive_ti;
    private EditText buildlive_title;

    private void findview() {
        findViewById(R.id.buildlive_back).setOnClickListener(this);
        buildlive_head = (RoundedImageView) findViewById(R.id.buildlive_head);
        buildlive_loc = (TextView) findViewById(R.id.buildlive_loc);
        buildlive_loc.setOnClickListener(this);
        buildlive_title = (EditText) findViewById(R.id.buildlive_title);
        findViewById(R.id.buildlive_mi).setOnClickListener(this);
        buildlive_ti= (TextView) findViewById(R.id.buildlive_ti);
        buildlive_ti.setOnClickListener(this);
        findViewById(R.id.buildlive_qq).setOnClickListener(this);
        findViewById(R.id.buildlive_wx).setOnClickListener(this);
        findViewById(R.id.buildlive_fb).setOnClickListener(this);
        findViewById(R.id.buildlive_line).setOnClickListener(this);
        findViewById(R.id.buildlive_startlive).setOnClickListener(this);
        if(MyData.LOCCITY.equals("")){
            buildlive_loc.setText(MyData.CITY);
        }else{
            buildlive_loc.setText(MyData.LOCCITY);
        }
        Picasso.with(context).load((URLManager.head + MyData.HEAD)).into(buildlive_head);
    }

    private String mAddress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0x01: {
                    mAddress = data.getStringExtra("address");
                    buildlive_loc.setText(mAddress);
                }
                break;
                case 10086: {
                    topic = data.getStringExtra("huati");
                    buildlive_ti.setText(topic);
                }
                break;
            }
        }
    }
    private boolean canfenx = true;

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.showDLog("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                showMessage(platform + " 收藏成功啦");
            } else {
                showMessage(platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            showMessage(platform + " 分享失败啦");
            if (t != null) {
                LogUtil.showDLog("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            showMessage(platform + " 分享取消了");
        }
    };

    /***
     * 开启直播
     */
    private String password = "", topic = "", cname = "", city = "";

    private void buildliveroom() {
        showloading();
        xhttp = new Xhttp(URLManager.creatRoom);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("cname", cname);
        xhttp.add("city", city);
        xhttp.add("password", password);
        xhttp.add("topic", topic);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    hasbuild = true;
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mesg = obj.getString("meg");
                    showMessage(mesg);
                    if (status.equals("1")) {
                        if (!mesg.contains("需要守护或者场管同意才能开启直播") && !mesg.contains("请等待守护或者场管同意")) {
                            String pushUrl = obj.getString("pushUrl");
                            MyData.CID = obj.getString("cid");
                            MyData.ROOMID = obj.getString("roomid");
                            ActivityJumpControl.getInstance(BuildLiveActivity.this).gotoLiveActivity(pushUrl, MyData.ROOMID);
                        }
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismissloading();
            }

            @Override
            public void onError() {
                hasbuild = true;
                dismissloading();
            }
        });
    }

}
