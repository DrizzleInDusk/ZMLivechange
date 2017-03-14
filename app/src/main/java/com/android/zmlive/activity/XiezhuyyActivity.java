package com.android.zmlive.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
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

public class XiezhuyyActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xiezhuyy_activity);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        findview();
        mSwipeLayout.setRefreshing(true);
        onRefresh();
    }

    private SwipeRefreshLayout mSwipeLayout;
    private ListView xzyy_listview;
    private TextView xzyy_zwsj;

    private void findview() {
        findViewById(R.id.xzyyback).setOnClickListener(this);
        xzyy_listview = (ListView) findViewById(R.id.xzyy_listview);
        xzyy_zwsj = (TextView) findViewById(R.id.xzyy_zwsj);

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
            case R.id.dialog_cancel:
                mbid="";
                dialog.dismiss();
                break;
            case R.id.dialog_confirm:
               String content= dialog_content.getText().toString();
                if(content.equals("")){
                    showMessage("请输入回复内容！");
                    return;
                }
                reply(mbid,content);
                dialog.dismiss();
                break;
            case R.id.xzyyback:
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
    private AlertDialog dialog;
    private EditText dialog_content;
    private String mbid="";

    private void dialogsendmess(String name, String zbname, String time, String place, String bz, String bid) {
        mbid = bid;
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_sendmess, null);
        dialog.setView(view);
        dialog.show();
        TextView dialog_name = (TextView) view.findViewById(R.id.dialog_name);
        TextView dialog_zbname = (TextView) view.findViewById(R.id.dialog_zbname);
        TextView dialog_time = (TextView) view.findViewById(R.id.dialog_time);
        TextView dialog_place = (TextView) view.findViewById(R.id.dialog_place);
        TextView dialog_bz = (TextView) view.findViewById(R.id.dialog_bz);
        dialog_name.setText("用户：" + name);
        dialog_zbname.setText("预约主播：" + zbname);
        dialog_time.setText("时间：" + time);
        dialog_place.setText("地点：" + place);
        dialog_bz.setText("打赏播钻：" + bz + "钻");
        dialog_content = (EditText) view.findViewById(R.id.dialog_content);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showKeyboard(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        yyList();
    }

    /***
     * 主播列表信息
     */
    private ArrayList<UserMes> usermeslist;
    private XiezhuyyAdapter adapter;

    private void yyList() {
        xhttp = new Xhttp(URLManager.yyList);
        xhttp.add("gid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray yyList = obj.getJSONArray("yyList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < yyList.length(); i++) {
                            JSONObject gg = yyList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setBtime(gg.getString("btime"));
                            usetmes.setMoney(gg.getString("money"));
                            usetmes.setPlace(gg.getString("place"));
                            usetmes.setUhead(gg.getString("uhead"));
                            usetmes.setUid(gg.getString("uid"));
                            usetmes.setBid(gg.getString("bid"));
                            usetmes.setUname(gg.getString("uname"));
                            usetmes.setHead(gg.getString("zbhead"));
                            usetmes.setId(gg.getString("zbid"));
                            usetmes.setNickname(gg.getString("zbname"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new XiezhuyyAdapter(context, usermeslist);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 停止刷新
                                mSwipeLayout.setRefreshing(false);
                                xzyy_zwsj.setVisibility(View.GONE);
                                xzyy_listview.setVisibility(View.VISIBLE);
                                xzyy_listview.setAdapter(adapter);
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

    /***
     * 预约主播的回复
     */
    private void reply(String bid, String content) {
        xhttp = new Xhttp(URLManager.reply);
        xhttp.add("bid", bid);
        xhttp.add("content", content);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                   String status= obj.getString("status");
                   String mess= obj.getString("meg");
                    showMessage(mess);
                    if(status.equals("1")){
                        yyList();
                    }
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
        xzyy_zwsj.setVisibility(View.VISIBLE);
        xzyy_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        xzyy_zwsj.setText(title);
    }

    private class XiezhuyyAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public XiezhuyyAdapter(Context context, ArrayList<UserMes> usermeslist) {
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
                convertView = minflater.inflate(R.layout.xiezhuyy_activity_item, null);
                holder = new ViewHolder();
                holder.xzyy_touxiang = (RoundedImageView) convertView
                        .findViewById(R.id.xzyy_touxiang);
                holder.xzyy_zbtouxiang = (RoundedImageView) convertView
                        .findViewById(R.id.xzyy_zbtouxiang);
                holder.xzyy_name = (TextView) convertView
                        .findViewById(R.id.xzyy_name);
                holder.xzyy_zbname = (TextView) convertView
                        .findViewById(R.id.xzyy_zbname);
                holder.xzyy_ll = (LinearLayout) convertView
                        .findViewById(R.id.xzyy_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

            final String id = usermes.getId();
            final String uid = usermes.getUid();
            final String bid = usermes.getBid();
            String uhead = usermes.getUhead();
            String head = usermes.getHead();
            final String uname = usermes.getUname();
            final String name = usermes.getNickname();
            final String btime = usermes.getBtime();
            final String place = usermes.getPlace();
            final String money = usermes.getMoney();
            Picasso.with(context).load(URLManager.head + uhead).error(R.mipmap.fw658).into(holder.xzyy_touxiang);
            Picasso.with(context).load(URLManager.head + head).error(R.mipmap.fw658).into(holder.xzyy_zbtouxiang);
            holder.xzyy_name.setText(uname);
            holder.xzyy_zbname.setText(name);
            holder.xzyy_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogsendmess(uname, name, btime, place, money, bid);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView xzyy_name, xzyy_zbname;
            private LinearLayout xzyy_ll;
            private RoundedImageView xzyy_touxiang, xzyy_zbtouxiang;
        }
    }

}
