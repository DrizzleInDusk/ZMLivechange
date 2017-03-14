package com.android.zmlive.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class XiezhuzhiboActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xiezhuzhibo_activity);
        findview();
        mSwipeLayout.setRefreshing(true);
        onRefresh();
    }

    private SwipeRefreshLayout mSwipeLayout;
    private ListView xzzb_listview;
    private TextView xzzb_zwsj;

    private void findview() {
        findViewById(R.id.xzzbback).setOnClickListener(this);
        xzzb_listview = (ListView) findViewById(R.id.xzzb_listview);
        xzzb_zwsj = (TextView) findViewById(R.id.xzzb_zwsj);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xzzbback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    @Override
    public void onRefresh() {
        applyList();
    }

    /***
     * 申请直播的列表
     */
    private ArrayList<UserMes> usermeslist;
    private XiezhuzhiboAdapter adapter;

    private void applyList() {
        xhttp = new Xhttp(URLManager.applyList);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray apList = obj.getJSONArray("apList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < apList.length(); i++) {
                            JSONObject gg = apList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setCid(gg.getString("cid"));
                            usetmes.setCname(gg.getString("cname"));
                            usetmes.setHead(gg.getString("head"));
                            usetmes.setNickname(gg.getString("nickName"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new XiezhuzhiboAdapter(context, usermeslist);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 停止刷新
                                mSwipeLayout.setRefreshing(false);
                                xzzb_zwsj.setVisibility(View.GONE);
                                xzzb_listview.setVisibility(View.VISIBLE);
                                xzzb_listview.setAdapter(adapter);
                            }
                        }, 1000); // 1秒后发送消息，停止刷新
                    } catch (Exception e) {
                        e.printStackTrace();
                        final String mess = obj.getString("meg");
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

    private void zwsj(String title) {
        xzzb_zwsj.setVisibility(View.VISIBLE);
        xzzb_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        xzzb_zwsj.setText(title);
    }

    /***
     * 同意主播进行直播
     */
    private void agree(String cid) {
        showloading();
        xhttp = new Xhttp(URLManager.agree);
        xhttp.add("cid", cid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        applyList();
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

    private class XiezhuzhiboAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public XiezhuzhiboAdapter(Context context, ArrayList<UserMes> usermeslist) {
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
                convertView = minflater.inflate(R.layout.xiezhuzhibo_activity_item, null);
                holder = new ViewHolder();
                holder.xzzb_touxiang = (RoundedImageView) convertView
                        .findViewById(R.id.xzzb_touxiang);
                holder.xzzb_name = (TextView) convertView
                        .findViewById(R.id.xzzb_name);
                holder.xzzb_roomname = (TextView) convertView
                        .findViewById(R.id.xzzb_roomname);
                holder.xzzb_tongyi = (TextView) convertView
                        .findViewById(R.id.xzzb_tongyi);
                holder.xzzb_ll = (LinearLayout) convertView
                        .findViewById(R.id.xzzb_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

            final String cid = usermes.getCid();
            String head = usermes.getHead();
            String nickName = usermes.getNickname();
            String cname = usermes.getCname();
            Picasso.with(context).load(URLManager.head + head).error(R.mipmap.fw658).into(holder.xzzb_touxiang);
            holder.xzzb_name.setText(nickName);
            holder.xzzb_roomname.setText(cname);
            holder.xzzb_tongyi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agree(cid);
                }
            });
            holder.xzzb_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView xzzb_name, xzzb_roomname, xzzb_tongyi;
            private LinearLayout xzzb_ll;
            private RoundedImageView xzzb_touxiang;
        }
    }
}
