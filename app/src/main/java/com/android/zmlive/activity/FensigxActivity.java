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
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FensigxActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.fensigx_activity);
        findview();
        fsgxb_slb.performClick();
    }

    private TextView fsgxb_slb, slb_line, fsgxb_fxb, fxb_line, fsgxb_zwsj;
    private ListView fsgxb_listview;

    private void findview() {
        findViewById(R.id.fsgxbback).setOnClickListener(this);
        fsgxb_listview = (ListView) findViewById(R.id.fsgxb_listview);
        fsgxb_zwsj = (TextView) findViewById(R.id.fsgxb_zwsj);
        slb_line = (TextView) findViewById(R.id.slb_line);
        fsgxb_slb = (TextView) findViewById(R.id.fsgxb_slb);
        fsgxb_slb.setOnClickListener(this);
        fxb_line = (TextView) findViewById(R.id.fxb_line);
        fsgxb_fxb = (TextView) findViewById(R.id.fsgxb_fxb);
        fsgxb_fxb.setOnClickListener(this);

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fsgxb_slb:
                viewvis(fsgxb_slb, slb_line);
                ggRankList();
                break;
            case R.id.fsgxb_fxb:
                viewvis(fsgxb_fxb, fxb_line);
                fxRankList();
                break;
            case R.id.fsgxbback:
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
     * 送礼榜
     */
    private ArrayList<UserMes> usermeslist;
    private FensigxAdapter adapter;

    private void ggRankList() {
        showloading();
        xhttp = new Xhttp(URLManager.ggRankList);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray ggList = obj.getJSONArray("ggRankList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < ggList.length(); i++) {
                            JSONObject gg = ggList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setDiamond(gg.getString("diamond"));
                            usetmes.setNickname(gg.getString("nickName"));
                            usetmes.setId(gg.getString("id"));
                            usetmes.setHead(gg.getString("head"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new FensigxAdapter(context, usermeslist, "ggRankList");
                        fsgxb_listview.setVisibility(View.VISIBLE);
                        fsgxb_zwsj.setVisibility(View.GONE);
                        fsgxb_listview.setAdapter(adapter);
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
     * 分享榜
     */
    private void fxRankList() {
        showloading();
        xhttp = new Xhttp(URLManager.fxRankList);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray ggList = obj.getJSONArray("fxRankList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < ggList.length(); i++) {
                            JSONObject gg = ggList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setDiamond(gg.getString("spend"));
                            usetmes.setNickname(gg.getString("nickName"));
                            usetmes.setId(gg.getString("id"));
                            usetmes.setHead(gg.getString("head"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new FensigxAdapter(context, usermeslist, "fxRankList");
                        fsgxb_listview.setVisibility(View.VISIBLE);
                        fsgxb_zwsj.setVisibility(View.GONE);
                        fsgxb_listview.setAdapter(adapter);
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
        fsgxb_zwsj.setVisibility(View.VISIBLE);
        fsgxb_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        fsgxb_zwsj.setText(title);
    }

    private class FensigxAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private String Tag;
        private LayoutInflater minflater;

        public FensigxAdapter(Context context, ArrayList<UserMes> usermeslist, String Tag) {
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
                convertView = minflater.inflate(R.layout.fensigx_activity_item, null);
                holder = new ViewHolder();
                holder.item_one = (TextView) convertView
                        .findViewById(R.id.fsgxb_item_one);
                holder.item_two = (TextView) convertView
                        .findViewById(R.id.fsgxb_item_two);
                holder.item_head = (RoundedImageView) convertView
                        .findViewById(R.id.fsgxb_item_head);
                holder.fsgxb_item_ll = (LinearLayout) convertView
                        .findViewById(R.id.fsgxb_item_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);
            final String id = usermes.getId();
            String one = "";
            String two = "";
            String head = "";
            if (Tag.equals("ggRankList")) {
                String nickname = usermes.getNickname();
                String diamond = usermes.getDiamond();
                head = URLManager.head + usermes.getHead();
                one = nickname;
                two = diamond + "钻";

            } else if (Tag.equals("fxRankList")) {
                String nickname = usermes.getNickname();
                String diamond = usermes.getDiamond();
                head = URLManager.head + usermes.getHead();
                one = nickname;
                two = diamond + "次";
            }
            holder.item_one.setText(one);
            holder.item_two.setText(two);
            Picasso.with(context).load(head).error(R.mipmap.fw658).into(holder.item_head);
            holder.fsgxb_item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView item_one, item_two;
            private RoundedImageView item_head;
            private LinearLayout fsgxb_item_ll;
        }
    }
}
