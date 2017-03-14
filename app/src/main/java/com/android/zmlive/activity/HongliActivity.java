package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.UserMes;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HongliActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.hongli_activity);
        findview();
    }

    private TextView hl_wodeyqm, hl_yjdy, hl_ejdy, hl_sjdy, yjdy_line, ejdy_line, sjdy_line, hl_zwsj, hl_point;
    //    private TextView hl_jlbz,;
    private ListView hl_listview;
    private ImageView hl_wodeyqmfx;

    private void findview() {
        findViewById(R.id.hlback).setOnClickListener(this);
        findViewById(R.id.guize).setOnClickListener(this);
        findViewById(R.id.hl_wodexiaoxi).setOnClickListener(this);
        hl_wodeyqm = (TextView) findViewById(R.id.hl_wodeyqm);
        hl_wodeyqmfx = (ImageView) findViewById(R.id.hl_wodeyqmfx);
        hl_wodeyqmfx.setOnClickListener(this);
        hl_point = (TextView) findViewById(R.id.hl_point);
        hl_wodeyqm.setOnClickListener(this);
        findViewById(R.id.hl_hongliguanli).setOnClickListener(this);
        findViewById(R.id.hl_caiwuguanli).setOnClickListener(this);

        hl_listview = (ListView) findViewById(R.id.hl_listview);
        hl_zwsj = (TextView) findViewById(R.id.hl_zwsj);
//        hl_jlbz = (TextView) findViewById(R.id.hl_jlbz);//奖励播钻
        myDividend();

        yjdy_line = (TextView) findViewById(R.id.yjdy_line);
        hl_yjdy = (TextView) findViewById(R.id.hl_yjdy);//一级队员
        hl_yjdy.setOnClickListener(this);
        ejdy_line = (TextView) findViewById(R.id.ejdy_line);
        hl_ejdy = (TextView) findViewById(R.id.hl_ejdy);//二级队员
        hl_ejdy.setOnClickListener(this);
        sjdy_line = (TextView) findViewById(R.id.sjdy_line);
        hl_sjdy = (TextView) findViewById(R.id.hl_sjdy);//三级队员
        hl_sjdy.setOnClickListener(this);
        hl_yjdy.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        if (unreadNum > 0) {
            hl_point.setVisibility(View.VISIBLE);
        } else {
            hl_point.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hl_wodexiaoxi://我的消息
                ActivityJumpControl.getInstance(activity).gotoWodemessActivity();
                break;
            case R.id.hl_wodeyqmfx://我的邀请码
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
            case R.id.hl_wodeyqm://我的邀请码
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(yqm);
                showMessage("邀请码已复制到剪切板");
                break;
            case R.id.hl_hongliguanli://红利管理
                ActivityJumpControl.getInstance(activity).gotoHongliGlActivity();
                break;
            case R.id.hl_caiwuguanli://财务管理
                ActivityJumpControl.getInstance(activity).gotoCaiwuGlActivity();
                break;
            case R.id.hl_yjdy://一级队员
                viewsek(hl_yjdy, yjdy_line);
                memberList("1");
                break;
            case R.id.hl_ejdy://二级队员
                viewsek(hl_ejdy, ejdy_line);
                memberList("2");
                break;
            case R.id.hl_sjdy://三级队员
                viewsek(hl_sjdy, sjdy_line);
                memberList("3");
                break;
            case R.id.guize://规则
                dialogshow();
                break;
            case R.id.dialog_mess_quxiao://规则
            case R.id.dialog_mess_yes://规则
                dialog.dismiss();
                break;
            case R.id.hlback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }
    private AlertDialog dialog = null;
    /**
     * dialog 规则
     */
    private void dialogshow() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_guiz);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(this);
    }

    /**
     * dialog 分享
     */
    private void dialoshare() {
        dialog = new AlertDialog.Builder(context, R.style.myDialog_style).create();
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
        p.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialog.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr1) + yqm + getResources().getString(R.string.fxnr2))
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
                        .withText(getResources().getString(R.string.fxnr1) + yqm + getResources().getString(R.string.fxnr2))
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
                        .withText(getResources().getString(R.string.fxnr1) + yqm + getResources().getString(R.string.fxnr2))
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
                        .withText(getResources().getString(R.string.fxnr1) + yqm + getResources().getString(R.string.fxnr2))
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

    private ArrayList<TextView> tvlist = new ArrayList<>();
    private ArrayList<TextView> tvlinelist = new ArrayList<>();

    private void viewsek(TextView tv, TextView tvline) {
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setTextColor(Color.parseColor("#000000"));
            tvlinelist.remove(i).setVisibility(View.INVISIBLE);
        }
        tv.setTextColor(Color.parseColor("#f93a6e"));
        tvline.setVisibility(View.VISIBLE);
        tvlist.add(tv);
        tvlinelist.add(tvline);
    }

    /***
     * 进入我的红利
     */
    private String yqm = "";

    private void myDividend() {
        showloading();
        xhttp = new Xhttp(URLManager.myDividend);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        yqm = obj.getString("code");
                        hl_wodeyqm.setText("邀请码   " + yqm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    try {
//                        hl_jlbz.setText(obj.getString("profit"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    try {
                        hl_yjdy.setText(getResources().getString(R.string.yjdy) + " " + obj.getString("count1"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        hl_yjdy.setText(getResources().getString(R.string.yjdy) + " 0");
                    }
                    try {
                        hl_ejdy.setText(getResources().getString(R.string.ejdy) + " " + obj.getString("count2"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        hl_ejdy.setText(getResources().getString(R.string.ejdy) + " 0");
                    }
                    try {
                        hl_sjdy.setText(getResources().getString(R.string.sjdy) + " " + obj.getString("count3"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        hl_sjdy.setText(getResources().getString(R.string.sjdy) + " 0");
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
     * 我的团队
     */
    private ArrayList<UserMes> usermeslist;
    private HongliAdapter adapter;

    private void memberList(final String type) {
        hl_zwsj.setVisibility(View.GONE);
        hl_listview.setVisibility(View.GONE);
        xhttp = new Xhttp(URLManager.memberList);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("type", type);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray userList = obj.getJSONArray("userList" + type);
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < userList.length(); i++) {
                            JSONObject tduser = userList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setId(tduser.getString("id"));
                            usetmes.setNickname(tduser.getString("nickName"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new HongliAdapter(context, usermeslist);
                        hl_zwsj.setVisibility(View.GONE);
                        hl_listview.setVisibility(View.VISIBLE);
                        hl_listview.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        zwsj(obj.getString("meg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    zwsj("");
                }
            }

            @Override
            public void onError() {
                zwsj("");
            }
        });
    }

    private void zwsj(String title) {
        hl_zwsj.setVisibility(View.VISIBLE);
        hl_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        hl_zwsj.setText(title);
    }

    private class HongliAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public HongliAdapter(Context context, ArrayList<UserMes> usermeslist) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.usermeslist = usermeslist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return usermeslist.size();
        }

        @Override
        public Object getItem(int position) {
            return usermeslist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.hongli_activity_item, null);
                holder = new ViewHolder();
                holder.item_nickname = (TextView) convertView
                        .findViewById(R.id.hl_item_nickname);
                holder.item_id = (TextView) convertView
                        .findViewById(R.id.hl_item_id);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);
            String id = usermes.getId();
            String nickname = usermes.getNickname();
            holder.item_nickname.setText(nickname);
            holder.item_id.setText(id);
            return convertView;
        }

        class ViewHolder {
            private TextView item_id, item_nickname;
        }
    }
}
