package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.MemberOption;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserMesActivity extends BaseActivity implements View.OnClickListener {
    private String yxid = "", type = "1";
    private String ucid = "", roomId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.usermes_activity);
        yxid = getIntent().getStringExtra("yxid");
        type = getIntent().getStringExtra("type");
        roomId = getIntent().getStringExtra("roomId");
        findview();
        userInfo();
    }

    private RoundedImageView usermes_head;
    private ImageView usermes_gander;
    private LinearLayout usermes_ll, usermes_ll2;
    private TextView usermes_name, usermes_level, usermes_id, guanzhunum, zannum, fansnum;
    private TextView usermes_chengshi, usermes_qianming, usermes_shengao, usermes_nianling, usermes_shangwei, usermes_tizhong;
    private RelativeLayout usermes_guanzhu, usermes_dianzan, usermes_fans;
    private LinearLayout usermes_wyguanzhu, usermes_wysixin, usermes_wyjubao;

    private void findview() {
        findViewById(R.id.usermesback).setOnClickListener(this);
        findViewById(R.id.usermesfenxiang).setOnClickListener(this);
        findViewById(R.id.usermes_lahei).setOnClickListener(this);
        findViewById(R.id.usermes_jinyan).setOnClickListener(this);
        usermes_ll = (LinearLayout) findViewById(R.id.usermes_ll);
        usermes_ll2 = (LinearLayout) findViewById(R.id.usermes_ll2);
        usermes_head = (RoundedImageView) findViewById(R.id.usermes_head);
        usermes_gander = (ImageView) findViewById(R.id.usermes_gander);
        usermes_name = (TextView) findViewById(R.id.usermes_name);
        usermes_level = (TextView) findViewById(R.id.usermes_level);
        usermes_id = (TextView) findViewById(R.id.usermes_id);
        guanzhunum = (TextView) findViewById(R.id.guanzhunum);
        zannum = (TextView) findViewById(R.id.zannum);
        fansnum = (TextView) findViewById(R.id.fansnum);
        usermes_chengshi = (TextView) findViewById(R.id.usermes_chengshi);
        usermes_qianming = (TextView) findViewById(R.id.usermes_qianming);
        usermes_shengao = (TextView) findViewById(R.id.usermes_shengao);
        usermes_nianling = (TextView) findViewById(R.id.usermes_nianling);
        usermes_shangwei = (TextView) findViewById(R.id.usermes_shangwei);
        usermes_tizhong = (TextView) findViewById(R.id.usermes_tizhong);

        usermes_guanzhu = (RelativeLayout) findViewById(R.id.usermes_guanzhu);
        usermes_guanzhu.setOnClickListener(this);
        usermes_dianzan = (RelativeLayout) findViewById(R.id.usermes_dianzan);
        usermes_dianzan.setOnClickListener(this);
        usermes_fans = (RelativeLayout) findViewById(R.id.usermes_fans);
        usermes_fans.setOnClickListener(this);
        usermes_wyguanzhu = (LinearLayout) findViewById(R.id.usermes_wyguanzhu);
        usermes_wyguanzhu.setOnClickListener(this);
        usermes_wysixin = (LinearLayout) findViewById(R.id.usermes_wysixin);
        usermes_wysixin.setOnClickListener(this);
        usermes_wyjubao = (LinearLayout) findViewById(R.id.usermes_wyjubao);
        usermes_wyjubao.setOnClickListener(this);

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.usermes_wyguanzhu://我要关注
                if (!MyData.USERID.equals("0")) {
                    if (usermes_wyguanzhu.isSelected()) {
                        showMessage(getResources().getString(R.string.ggglhint));
                    } else {
                        addConcern();
                    }
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.usermes_wysixin://我要私信
                if (!MyData.USERID.equals("0")) {
                    ActivityJumpControl.getInstance(activity).gotoSixinzhuboActivity(ucid);
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.usermes_wyjubao://我要举报
                if (!MyData.USERID.equals("0")) {
                    ActivityJumpControl.getInstance(activity).gotoJubaoActivity(yxid);
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.usermesfenxiang://分享
                if (canfenx) {
                    canfenx = false;
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canfenx = true;
                        }
                    }, 2000);
                    dialoshare();
                }
                break;
            case R.id.usermes_lahei://踢人
                dialogmess(nickname);
                break;
            case R.id.usermes_jinyan://禁言
                dialogjinyan();
                break;
            case R.id.usermesback:
                umactfinish();
                break;
            case R.id.dialog_mess_quxiao:
                dialog.dismiss();
                break;
            case R.id.dialog_mess_yes:
                kickMember();
                dialog.dismiss();
                break;
            case R.id.dialog_cancel:
                dialog.dismiss();
                activity.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                break;
            case R.id.dialog_confirm:
               String content= jinyanet.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    setTempMute(yxid, content, true);
                }
                dialog.dismiss();
                activity.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                break;
        }
    }
    private void umactfinish(){
        ActivityJumpControl.getInstance(activity).popActivity(activity);
        finish();
    }
    /**
     * dialog 分享
     */
    private void dialoshare() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_fenxiang);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth()*1); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialog.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.LINE)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.FACEBOOK)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_qx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private AlertDialog dialog;
    /**
     * dialog 消息提示
     */
    private TextView dialog_mess_title, dialog_mess_con;

    private void dialogmess(String con) {
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
        dialog_mess_con.setText("是否将"+con+"踢出直播间");

        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(this);
    }
    private EditText jinyanet;
    private void dialogjinyan() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_name, null);
        dialog.setView(view);
        dialog.show();
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        jinyanet = (EditText) view.findViewById(R.id.dialog_content);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        dialog_title.setText("设置禁言时间");
        jinyanet.setHint("请输入禁言时间");
        jinyanet.setInputType(InputType.TYPE_CLASS_NUMBER);
    }
    // 踢人
    private void kickMember() {
        Map<String, Object> reason = new HashMap<>();
        reason.put("reason", "就是不爽！");
        System.out.println("kickMember>>>yxid>>>>"+yxid);
        NIMClient.getService(ChatRoomService.class).kickMember(roomId, yxid, reason).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                LogUtil.showELog("UserMesActivity", "kick member onSuccess" );
                umactfinish();
            }
            @Override
            public void onFailed(int code) {
                LogUtil.showELog("UserMesActivity", "kick member failed:" + code);
            }
            @Override
            public void onException(Throwable exception) {
                LogUtil.showELog("UserMesActivity", "kick member exception:" + exception);
            }
        });
    }
    // 设置临时禁言
    private void setTempMute(String account, String content, boolean needNotify) {
        MemberOption option = new MemberOption(roomId, account);
        System.out.println("Long.parseLong(content)>>>>"+Long.parseLong(content));
        System.out.println("setTempMute>>>account>>>>"+account);
        NIMClient.getService(ChatRoomService.class).markChatRoomTempMute(needNotify, Long.parseLong(content), option)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        showMessage("设置临时禁言成功");
                        umactfinish();
                    }

                    @Override
                    public void onFailed(int code) {
                        showMessage("设置临时禁言失败");
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
    }

    private boolean canfenx = true;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

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
     * 删除我的关注
     */

    private void delMyConcern() {
        showloading();
        xhttp = new Xhttp(URLManager.delMyConcern);
        xhttp.add("str", ucid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        usermes_wyguanzhu.setSelected(false);
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
     * 添加关注
     */
    private void addConcern() {
        showloading();
        xhttp = new Xhttp(URLManager.addConcern);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("ucid", ucid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        usermes_wyguanzhu.setSelected(true);
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
     * 获取用户信息
     */
    private String nickname="游客";
    private void userInfo() {
        showloading();
        if (type.equals("1")) {
            xhttp = new Xhttp(URLManager.userInfo);
            xhttp.add("yxid", yxid);
            xhttp.add("cid",  MyData.CID );
        } else {
            xhttp = new Xhttp(URLManager.userInfo2);
            xhttp.add("uid", yxid);
        }
        xhttp.add("gid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    JSONObject userInfo = obj.getJSONObject("userInfo");

                    String head = userInfo.getString("head");
                    Picasso.with(context).load(URLManager.head + head).error(R.mipmap.fw658).into(usermes_head);
                    nickname=userInfo.getString("nickname");
                    usermes_name.setText(userInfo.getString("nickname"));
                    String sex = userInfo.getString("sex");//
                    if (sex.equals("男")) {
                        usermes_gander.setImageResource(R.mipmap.nans);
                    } else {
                        usermes_gander.setImageResource(R.mipmap.nvs);
                    }
                    usermes_level.setText(userInfo.getString("level"));
                    usermes_id.setText("ID  " + userInfo.getString("uid"));
                    String concern = userInfo.getString("concern");
                    String praise = userInfo.getString("praise");
                    String fans = userInfo.getString("fans");
                    String city = userInfo.getString("city");
                    String signature = userInfo.getString("signature");
                    String height = userInfo.getString("height");
                    String age = userInfo.getString("age");
                    String weight = userInfo.getString("weight");
                    String colour = userInfo.getString("colour");
                    String usertype = userInfo.getString("usertype");//1是主播
                    try {
                        String gzStatus = obj.getString("gzStatus");
                        if (gzStatus.equals("1")) {
                            usermes_wyguanzhu.setSelected(true);
                        } else {
                            usermes_wyguanzhu.setSelected(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        usermes_wyguanzhu.setSelected(false);
                    }
                    if (colour.equals("blue")) {
                        usermes_level.setBackgroundResource(R.drawable.round3_lev1);
                    } else if (colour.equals("green")) {
                        usermes_level.setBackgroundResource(R.drawable.round3_lev2);
                    } else if (colour.equals("yellow")) {
                        usermes_level.setBackgroundResource(R.drawable.round3_lev3);
                    } else if (colour.equals("orange")) {
                        usermes_level.setBackgroundResource(R.drawable.round3_lev4);
                    } else if (colour.equals("purple")) {
                        usermes_level.setBackgroundResource(R.drawable.round3_lev5);
                    }
                    if (usertype.equals("1")&&!yxid.equals(MyData.USERID)&&MyData.IDENTITY.equals("")) {
                        usermes_ll.setVisibility(View.VISIBLE);
                    } else {
                        usermes_ll.setVisibility(View.GONE);
                    }
                    if (type.equals("1")) {
                        try {
                            String management = obj.getString("management");
                            if (management.equals("1")) {
                                usermes_ll2.setVisibility(View.VISIBLE);
                            } else {
                                usermes_ll2.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            usermes_ll2.setVisibility(View.GONE);
                        }
                    }
                    if(type.equals("2")){
                        usermes_ll2.setVisibility(View.GONE);
                    }
                    ucid = userInfo.getString("uid");
                    String bust = "";
                    try {
                        bust = userInfo.getString("bust");
                    } catch (Exception e) {
                        e.printStackTrace();
                        bust = "";
                    }
                    if (concern.equals("")) {
                        concern = "0";
                    }
                    if (praise.equals("")) {
                        praise = "0";
                    }
                    if (fans.equals("")) {
                        fans = "0";
                    }
                    if (city.equals("")) {
                        city = "--";
                    }
                    if (signature.equals("")) {
                        signature = "--";
                    }
                    if (height.equals("")) {
                        height = "--";
                    } else {
                        height = height + " cm";
                    }
                    if (age.equals("")) {
                        age = "--";
                    } else {
                        age = age + " 岁";
                    }
                    if (bust.equals("")) {
                        bust = "--";
                    }
                    if (weight.equals("")) {
                        weight = "--";
                    } else {
                        weight = weight + " KG";
                    }
                    guanzhunum.setText(concern);
                    zannum.setText(praise);
                    fansnum.setText(fans);
                    usermes_chengshi.setText(city);
                    usermes_qianming.setText(signature);
                    usermes_shengao.setText(height);
                    usermes_nianling.setText(age);
                    usermes_shangwei.setText(bust);
                    usermes_tizhong.setText(weight);

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
