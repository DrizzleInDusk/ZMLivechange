package com.android.zmlive.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.android.zmlive.tool.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HongliGlActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.hongligl_activity);
        balance();
        findview();
    }

    private ListView hlgl_listview;
    private TextView hlgl_shouyitixian, shouyitixian_line, hlgl_tixianjilu, tixianjilu_line, hlgl_yue, hlgl_tixian, hlgl_zwsj;
    private LinearLayout hlgl_sytx, hlgl_txjl;

    private void findview() {
        findViewById(R.id.hlglback).setOnClickListener(this);
        hlgl_listview = (ListView) findViewById(R.id.hlgl_listview);
        hlgl_zwsj = (TextView) findViewById(R.id.hlgl_zwsj);
        hlgl_sytx = (LinearLayout) findViewById(R.id.hlgl_sytx);
        hlgl_txjl = (LinearLayout) findViewById(R.id.hlgl_txjl);

        shouyitixian_line = (TextView) findViewById(R.id.shouyitixian_line);
        hlgl_shouyitixian = (TextView) findViewById(R.id.hlgl_shouyitixian);
        hlgl_shouyitixian.setOnClickListener(this);
        tixianjilu_line = (TextView) findViewById(R.id.tixianjilu_line);
        hlgl_tixianjilu = (TextView) findViewById(R.id.hlgl_tixianjilu);
        hlgl_tixianjilu.setOnClickListener(this);
        hlgl_yue = (TextView) findViewById(R.id.hlgl_yue);
        hlgl_tixian = (TextView) findViewById(R.id.hlgl_tixian);
        hlgl_tixian.setOnClickListener(this);
        hlgl_shouyitixian.performClick();

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hlgl_shouyitixian://收益提现
                viewvis(hlgl_shouyitixian, shouyitixian_line, hlgl_sytx);
                profit();
                break;
            case R.id.hlgl_tixianjilu://提现记录
                viewvis(hlgl_tixianjilu, tixianjilu_line, hlgl_txjl);
                tmList();
                break;
            case R.id.hlgl_tixian://提现绑定
                ActivityJumpControl.getInstance(activity).gotoTixianBdActivity(mode, totle);
                break;
            case R.id.hlglback:
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
    private ArrayList<TextView> tvlist = new ArrayList<>();
    private ArrayList<TextView> tvlinelist = new ArrayList<>();
    private ArrayList<LinearLayout> lllist = new ArrayList<>();

    private void viewvis(TextView tv, TextView tvline, LinearLayout ll) {
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setTextColor(Color.parseColor("#000000"));
            tvlinelist.remove(i).setBackgroundColor(Color.parseColor("#ffffff"));
            lllist.remove(i).setVisibility(View.GONE);
        }
        tv.setTextColor(Color.parseColor("#f93a6e"));
        tvline.setBackgroundColor(Color.parseColor("#f93a6e"));
        ll.setVisibility(View.VISIBLE);
        tvlist.add(tv);
        lllist.add(ll);
        tvlinelist.add(tvline);
    }


    /***
     * 获取用户余额和收益
     */
    private String profit = "0";

    private void balance() {
        xhttp = new Xhttp(URLManager.balance);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    profit = obj.getString("profit");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    /***
     * 收益额
     */
    private HongliglAdapter adapter;
    private String totle;
    private ArrayList<UserMes> usermeslist;

    private void profit() {
        showloading();
        xhttp = new Xhttp(URLManager.profit);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    totle = obj.getString("totle");
                    if (Integer.parseInt(profit) != Integer.parseInt(totle)) {
                        totle = profit;
                    }
                    hlgl_yue.setText(totle);
                    try {
                        JSONArray ggList = obj.getJSONArray("ggList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < ggList.length(); i++) {
                            JSONObject gg = ggList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setDiamond(gg.getString("diamond"));
                            usetmes.setTime(gg.getString("time"));
                            usetmes.setId(gg.getString("uid"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new HongliglAdapter(context, usermeslist, "profit");
                        hlgl_listview.setVisibility(View.VISIBLE);
                        hlgl_zwsj.setVisibility(View.GONE);
                        hlgl_listview.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        zwsj(obj.getString("meg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    zwsj("");
                }
                dismissloading();
            }

            @Override
            public void onError() {
                zwsj("");
                dismissloading();
            }
        });
    }

    private void zwsj(String title) {
        hlgl_zwsj.setVisibility(View.VISIBLE);
        hlgl_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        hlgl_zwsj.setText(title);
    }

    /***
     * 提现记录
     */
    private String mode = "sy";

    private void tmList() {
        showloading();
        xhttp = new Xhttp(URLManager.tmList);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("mode", mode);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray ggList = obj.getJSONArray("tmList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < ggList.length(); i++) {
                            JSONObject gg = ggList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setMoney(gg.getString("money"));
                            usetmes.setTime(gg.getString("time"));
                            usetmes.setStatus(gg.getString("status"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new HongliglAdapter(context, usermeslist, "tmList");
                        hlgl_listview.setVisibility(View.VISIBLE);
                        hlgl_zwsj.setVisibility(View.GONE);
                        hlgl_listview.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        zwsj(obj.getString("meg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    zwsj("");
                }
                dismissloading();
            }

            @Override
            public void onError() {
                zwsj("");
                dismissloading();
            }
        });
    }

    private class HongliglAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private String Tag;
        private LayoutInflater minflater;

        public HongliglAdapter(Context context, ArrayList<UserMes> usermeslist, String Tag) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.usermeslist = usermeslist;
            this.context = context;
            this.Tag = Tag;
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
                convertView = minflater.inflate(R.layout.hongligl_activity_item, null);
                holder = new ViewHolder();
                holder.item_one = (TextView) convertView
                        .findViewById(R.id.hlgl_item_one);
                holder.item_two = (TextView) convertView
                        .findViewById(R.id.hlgl_item_two);
                holder.item_three = (TextView) convertView
                        .findViewById(R.id.hlgl_item_three);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);
            String one = "";
            String two = "";
            String three = "";
            if (Tag.equals("profit")) {
                one = usermes.getId();
                two = usermes.getDiamond();
                three = TimeUtils.datetoDate(usermes.getTime(), "yyyy-MM-dd", "yyyy.MM.dd");

            } else {
                one = TimeUtils.datetoDate(usermes.getTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd");
                two = usermes.getMoney();
                String sta = usermes.getStatus();
                if (sta.equals("0")) {
                    three = "待审";
                } else if (sta.equals("2")) {
                    three = "到账";
                }else {
                    three = "拒绝";
                }
            }
            holder.item_one.setText(one);
            holder.item_two.setText("￥ " + two);
            holder.item_three.setText(three);
            return convertView;
        }

        class ViewHolder {
            private TextView item_one, item_two, item_three;
        }
    }

}
