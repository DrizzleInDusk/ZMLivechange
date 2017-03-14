package com.android.zmlive.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShouzhimxActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shouzhimx_activity);
        findview();
        viewvis(szmx_czjl, czjl_line);
        czList();
    }

    private ListView szmx_listview;
    private TextView szmx_czjl, szmx_txjl, szmx_sdlw, szmx_sclw, czjl_line, txjl_line, sdlw_line, sclw_line, szmx_zwsj;

    private void findview() {
        szmx_zwsj = (TextView) findViewById(R.id.szmx_zwsj);
        szmx_listview = (ListView) findViewById(R.id.szmx_listview);
        findViewById(R.id.szmxback).setOnClickListener(this);
        czjl_line = (TextView) findViewById(R.id.czjl_line);
        szmx_czjl = (TextView) findViewById(R.id.szmx_czjl);
        szmx_czjl.setOnClickListener(this);
        txjl_line = (TextView) findViewById(R.id.txjl_line);
        szmx_txjl = (TextView) findViewById(R.id.szmx_txjl);
        szmx_txjl.setOnClickListener(this);
        sdlw_line = (TextView) findViewById(R.id.sdlw_line);
        szmx_sdlw = (TextView) findViewById(R.id.szmx_sdlw);
        szmx_sdlw.setOnClickListener(this);
        sclw_line = (TextView) findViewById(R.id.sclw_line);
        szmx_sclw = (TextView) findViewById(R.id.szmx_sclw);
        szmx_sclw.setOnClickListener(this);
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
            case R.id.szmx_czjl://充值记录
                viewvis(szmx_czjl, czjl_line);
                czList();
                break;
            case R.id.szmx_txjl://提现记录
                viewvis(szmx_txjl, txjl_line);
                tmList();
                break;
            case R.id.szmx_sdlw://收到礼物
                viewvis(szmx_sdlw, sdlw_line);
                receivedliwu();
                break;
            case R.id.szmx_sclw://送出礼物
                viewvis(szmx_sclw, sclw_line);
                sendliwu();
                break;
            case R.id.szmxback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    private ArrayList<TextView> tvlist = new ArrayList<>();
    private ArrayList<TextView> tvlinelist = new ArrayList<>();

    private void viewvis(TextView tv, TextView tvline) {
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setTextColor(Color.parseColor("#000000"));
            tvlinelist.remove(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        tv.setTextColor(Color.parseColor("#7E3AB3"));
        tvline.setBackgroundColor(Color.parseColor("#7E3AB3"));
        tvlist.add(tv);
        tvlinelist.add(tvline);
    }

    /***
     * 充值记录
     */
    private CZAdapter adapter1;

    private void czList() {
        showloading();
        xhttp = new Xhttp(URLManager.payRecord);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray ggList = obj.getJSONArray("prRecordList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < ggList.length(); i++) {
                            JSONObject gg = ggList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setMoney(gg.getString("money"));
                            usetmes.setTime(gg.getString("time"));
                            usetmes.setOrders(gg.getString("orders"));
                            usetmes.setStatus(gg.getString("status"));
                            usetmes.setType(gg.getString("type"));
                            usermeslist.add(usetmes);
                        }
                        adapter1 = new CZAdapter(context, usermeslist);
                        szmx_listview.setVisibility(View.VISIBLE);
                        szmx_zwsj.setVisibility(View.GONE);
                        szmx_listview.setAdapter(adapter1);
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

    /***
     * 提现记录
     */
    private String mode = "ye";
    private ArrayList<UserMes> usermeslist;
    private ShouzhimxAdapter adapter;

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
                            usetmes.setType(gg.getString("type"));
                            usetmes.setStatus(gg.getString("status"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new ShouzhimxAdapter(context, usermeslist, "tmList");
                        szmx_listview.setVisibility(View.VISIBLE);
                        szmx_zwsj.setVisibility(View.GONE);
                        szmx_listview.setAdapter(adapter);
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

    /***
     * 收到的礼物
     */
    private void receivedliwu() {
        showloading();
        xhttp = new Xhttp(URLManager.receivedliwu);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray receivedList = obj.getJSONArray("receivedList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < receivedList.length(); i++) {
                            JSONObject gg = receivedList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setNickname(gg.getString("nickName"));
                            usetmes.setTime(gg.getString("time"));
                            usetmes.setDiamond(gg.getString("diamond"));
                            usetmes.setHead(gg.getString("head"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new ShouzhimxAdapter(context, usermeslist, "receivedList");
                        szmx_listview.setVisibility(View.VISIBLE);
                        szmx_zwsj.setVisibility(View.GONE);
                        szmx_listview.setAdapter(adapter);
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

    /***
     * 送出的礼物
     */
    private void sendliwu() {
        showloading();
        xhttp = new Xhttp(URLManager.sendliwu);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray receivedList = obj.getJSONArray("sendList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < receivedList.length(); i++) {
                            JSONObject gg = receivedList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setNickname(gg.getString("nickName"));
                            usetmes.setTime(gg.getString("time"));
                            usetmes.setDiamond(gg.getString("diamond"));
                            usetmes.setHead(gg.getString("head"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new ShouzhimxAdapter(context, usermeslist, "sendList");
                        szmx_listview.setVisibility(View.VISIBLE);
                        szmx_zwsj.setVisibility(View.GONE);
                        szmx_listview.setAdapter(adapter);
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
        szmx_zwsj.setVisibility(View.VISIBLE);
        szmx_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        szmx_zwsj.setText(title);
    }

    private class ShouzhimxAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private String Tag;
        private LayoutInflater minflater;

        public ShouzhimxAdapter(Context context, ArrayList<UserMes> usermeslist, String Tag) {
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
                convertView = minflater.inflate(R.layout.shouzhimx_activity_item, null);
                holder = new ViewHolder();
                holder.item_one = (TextView) convertView
                        .findViewById(R.id.szmx_item_one);
                holder.item_two = (TextView) convertView
                        .findViewById(R.id.szmx_item_two);
                holder.item_three = (TextView) convertView
                        .findViewById(R.id.szmx_item_three);
                holder.item_four = (TextView) convertView
                        .findViewById(R.id.szmx_item_four);
                holder.item_five = (TextView) convertView
                        .findViewById(R.id.szmx_item_five);
                holder.item_six = (TextView) convertView
                        .findViewById(R.id.szmx_item_six);
                holder.item_head = (RoundedImageView) convertView
                        .findViewById(R.id.szmx_item_head);
                holder.item_ll = (LinearLayout) convertView
                        .findViewById(R.id.szmx_item_ll);
                holder.item_ll2 = (LinearLayout) convertView
                        .findViewById(R.id.szmx_item_ll2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);
            String one = "";
            String two = "";
            String three = "";
            String four = "";
            String five = "";
            String six = "";
            String head = "";
            boolean isone = true;
            if (Tag.equals("profit")) {
                holder.item_ll.setVisibility(View.VISIBLE);
                holder.item_ll2.setVisibility(View.GONE);
                isone = true;

            } else if (Tag.equals("tmList")) {
                holder.item_ll.setVisibility(View.VISIBLE);
                holder.item_ll2.setVisibility(View.GONE);
                isone = true;
                String type = usermes.getType();
                if (type.equals("bank")) {
                    one = "提现到银行卡    ￥" + usermes.getMoney();
                } else {
                    one = "提现到支付宝    ￥" + usermes.getMoney();
                }
                two = TimeUtils.datetoDate(usermes.getTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss");
                String sta = usermes.getStatus();
                if (sta.equals("0")) {
                    three = "待审";
                } else if (sta.equals("2")) {
                    three = "成功";
                }else{
                    three = "拒绝";
                }
            } else if (Tag.equals("receivedList")) {
                holder.item_ll.setVisibility(View.GONE);
                holder.item_ll2.setVisibility(View.VISIBLE);
                isone = false;
                four = "收到\t\t" + usermes.getNickname() + "\t\t一份礼物" ;
                five = TimeUtils.datetoDate(usermes.getTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm");
                six = "价格："+usermes.getDiamond();
                head = URLManager.head + usermes.getHead();

            } else if (Tag.equals("sendList")) {
                holder.item_ll.setVisibility(View.GONE);
                holder.item_ll2.setVisibility(View.VISIBLE);
                isone = false;
                four = "送給\t\t" + usermes.getNickname() + "\t\t一份礼物" ;
                five = TimeUtils.datetoDate(usermes.getTime(), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm");
                six = "价格："+usermes.getDiamond();
                head = URLManager.head + usermes.getHead();
            }
            if (isone) {
                holder.item_one.setText(one);
                holder.item_two.setText(two);
                holder.item_three.setText(three);
            } else {
                holder.item_four.setText(four);
                holder.item_five.setText(five);
                holder.item_six.setText(six);
                if (!head.equals("")) {
                    Picasso.with(context).load(head).error(R.mipmap.fw658).into(holder.item_head);
                }
            }
            return convertView;
        }

        class ViewHolder {
            private TextView item_one, item_two, item_three, item_four, item_five, item_six;
            private LinearLayout item_ll, item_ll2;
            private RoundedImageView item_head;
        }
    }

    private class CZAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public CZAdapter(Context context, ArrayList<UserMes> usermeslist) {
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
                convertView = minflater.inflate(R.layout.shouzhimx_activity_item2, null);
                holder = new ViewHolder();
                holder.item_one = (TextView) convertView
                        .findViewById(R.id.szmx_item_one);
                holder.item_two = (TextView) convertView
                        .findViewById(R.id.szmx_item_two);
                holder.item_three = (TextView) convertView
                        .findViewById(R.id.szmx_item_three);
                holder.szmx_item_pay = (ImageView) convertView
                        .findViewById(R.id.szmx_item_pay);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);
            String one = "";
            String two = "";
            String three = "";
            String type = usermes.getType();
            if(type.equals("paypal")){
                holder.szmx_item_pay.setImageResource(R.mipmap.paypals);
            }else{
                holder.szmx_item_pay.setImageResource(R.mipmap.daofus);
            }
            two = TimeUtils.datetoDate(usermes.getTime(), "yyyy-MM-dd HH:mm:ss", "MM.dd HH:mm");
            String orders = usermes.getOrders();
            String status = usermes.getStatus();
            String money = usermes.getMoney();
            if (status.equals("1")) {
                three = "成功";
            } else {
                three = "待审核";
            }
            one="充值 "+money;
            holder.item_one.setText(two +" "+ one);
            holder.item_two.setText("订单：" + orders);
            holder.item_three.setText(three);
            return convertView;
        }

        class ViewHolder {
            private TextView item_one, item_two, item_three;
            private ImageView szmx_item_pay;
        }
    }
}
