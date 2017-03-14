package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.UserMes;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShipinzhongxinActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shipinzx_activity);
        findview();
        spzx_ansj.performClick();
    }

    private SwipeRefreshLayout mSwipeLayout;
    private GridView spzx_gridview;
    private TextView spzx_ansj, spzx_ansj_tv, spzx_anrd, spzx_anrd_tv, spzx_zwsj;

    private void findview() {
        findViewById(R.id.spzx_back).setOnClickListener(this);
        spzx_ansj_tv = (TextView) findViewById(R.id.spzx_ansj_tv);
        spzx_ansj = (TextView) findViewById(R.id.spzx_ansj);
        spzx_ansj.setOnClickListener(this);
        spzx_anrd_tv = (TextView) findViewById(R.id.spzx_anrd_tv);
        spzx_anrd = (TextView) findViewById(R.id.spzx_anrd);
        spzx_anrd.setOnClickListener(this);
        spzx_zwsj = (TextView) findViewById(R.id.spzx_zwsj);
        spzx_gridview = (GridView) findViewById(R.id.spzx_gridview);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.spzx_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
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
            case R.id.spzx_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.spzx_ansj://按时间
                spzx_ansj.setTextColor(Color.parseColor("#9854CF"));
                spzx_anrd.setTextColor(Color.parseColor("#000000"));
                spzx_ansj_tv.setVisibility(View.VISIBLE);
                spzx_anrd_tv.setVisibility(View.GONE);
                setenable(true);
                type="time";
                mSwipeLayout.setRefreshing(true);
                onRefresh();
                break;
            case R.id.spzx_anrd://按热度
                spzx_ansj.setTextColor(Color.parseColor("#000000"));
                spzx_anrd.setTextColor(Color.parseColor("#9854CF"));
                spzx_ansj_tv.setVisibility(View.GONE);
                spzx_anrd_tv.setVisibility(View.VISIBLE);
                setenable(false);
                type="num";
                mSwipeLayout.setRefreshing(true);
                onRefresh();
                break;
        }
    }

    private void setenable(boolean boo) {
        spzx_ansj.setEnabled(!boo);
        spzx_anrd.setEnabled(boo);
    }

    /***
     * 视频中心
     */
    private ArrayList<UserMes> usermeslist;
    private ShipinzxAdapter adapter;
    private String type;
    private void appVideoList() {
        xhttp = new Xhttp(URLManager.appVideoList);
        xhttp.add("type", type);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray videoList = obj.getJSONArray("videoList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < videoList.length(); i++) {
                            JSONObject gg = videoList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setCover(gg.getString("cover"));
                            usetmes.setId(gg.getString("id"));
                            usetmes.setNickname(gg.getString("name"));
                            usetmes.setNumber(gg.getString("number"));
                            usetmes.setPath(gg.getString("path"));
                            usetmes.setTime(gg.getString("time"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new ShipinzxAdapter(context, usermeslist);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 停止刷新
                                mSwipeLayout.setRefreshing(false);
                                spzx_zwsj.setVisibility(View.GONE);
                                spzx_gridview.setVisibility(View.VISIBLE);
                                spzx_gridview.setAdapter(adapter);
                            }
                        }, 1000); // 1秒后发送消息，停止刷新
                    } catch (Exception e) {
                        e.printStackTrace();
                        final String mess=obj.getString("meg");
                        mSwipeLayout.setRefreshing(false);
                        zwsj(mess);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 停止刷新
                            mSwipeLayout.setRefreshing(false);
                            zwsj("");
                        }
                    }, 1000); // 1秒后发送消息，停止刷新
                }
            }

            @Override
            public void onError() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        mSwipeLayout.setRefreshing(false);
                        zwsj("");
                    }
                }, 1000); // 1秒后发送消息，停止刷新
            }
        });
    }

    /***
     * 点击视频
     */
    private void updateNum(String vid) {
        xhttp = new Xhttp(URLManager.updateNum);
        xhttp.add("vid",vid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    private void zwsj(String title) {
        spzx_zwsj.setVisibility(View.VISIBLE);
        spzx_gridview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        spzx_zwsj.setText(title);
    }
    private void viewgone() {
        spzx_zwsj.setVisibility(View.GONE);
        spzx_gridview.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        viewgone();
        appVideoList();
    }

    private class ShipinzxAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public ShipinzxAdapter(Context context, ArrayList<UserMes> usermeslist) {
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
                convertView = minflater.inflate(R.layout.shipinzx_activity_item, null);
                holder = new ViewHolder();
                holder.zhibo_play = (TextView) convertView
                        .findViewById(R.id.zhibo_play);
                holder.zhibo_fenxiang = (TextView) convertView
                        .findViewById(R.id.zhibo_fenxiang);
                holder.zhibo_name = (TextView) convertView
                        .findViewById(R.id.spzhibo_name);
                holder.zhibo_img = (ImageView) convertView
                        .findViewById(R.id.zhibo_img);
                holder.zhibo_rl = (RelativeLayout) convertView
                        .findViewById(R.id.zhibo_rl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

            final String id = usermes.getId();
            String cover = usermes.getCover();
            final String name = usermes.getNickname();
            String number = usermes.getNumber();
            String path = usermes.getPath();
            String time = usermes.getTime();
            cover = URLManager.cover + cover;
            path = URLManager.video + path;
            Picasso.with(context).load(cover).error(R.mipmap.avatar_def).into(holder.zhibo_img);
            final String finalPath = path;
            holder.zhibo_name.setText(name);
            holder.zhibo_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalPath == null || finalPath.equals("")) {
                        /**
                         * 简单检测播放源的合法性,不合法不播放
                         */
                        showMessage("地址有误，请重试！");
                    } else {
//                        ActivityJumpControl.getInstance(activity).gotoVideoViewPlayingActivity(Uri.parse(finalPath));
                        ActivityJumpControl.getInstance(activity).gotoShipinbofangActivity(finalPath,name);
                        updateNum(id);
                    }
                }
            });
            holder.zhibo_fenxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                }
            });
            holder.zhibo_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }

        class ViewHolder {
            private TextView zhibo_play, zhibo_fenxiang,zhibo_name;
            private ImageView zhibo_img;
            private RelativeLayout zhibo_rl;
        }
    }
    private AlertDialog dialog;
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
}
