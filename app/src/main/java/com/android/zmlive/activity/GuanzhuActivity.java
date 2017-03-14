package com.android.zmlive.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuanzhuActivity extends BaseActivity implements View.OnClickListener ,SwipeRefreshLayout.OnRefreshListener {
    private String tag = "concern";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.guanzhu_activity);
        findview();
        tag = getIntent().getStringExtra("tag");
        if (tag.equals("fans")) {
            gz_gzwd.performClick();
        } else {
            gz_wgzd.performClick();
        }

    }

    private TextView gz_wgzd, gz_wgzd_tv, gz_gzwd, gz_gzwd_tv;
    private TextView gz_bianji, gz_quanxuan, gz_shanchu;
    private LinearLayout gz_ll;
    private ListView gz_listview;
    private TextView gz_zwsj;

    private SwipeRefreshLayout mSwipeLayout;
    private void findview() {
        gz_listview = (ListView) findViewById(R.id.gz_listview);
        gz_zwsj = (TextView) findViewById(R.id.gz_zwsj);
        gz_ll = (LinearLayout) findViewById(R.id.gz_ll);

        findViewById(R.id.gz_back).setOnClickListener(this);
        gz_bianji = (TextView) findViewById(R.id.gz_bianji);
        gz_bianji.setOnClickListener(this);

        gz_wgzd_tv = (TextView) findViewById(R.id.gz_wgzd_tv);
        gz_wgzd = (TextView) findViewById(R.id.gz_wgzd);
        gz_wgzd.setOnClickListener(this);
        gz_gzwd_tv = (TextView) findViewById(R.id.gz_gzwd_tv);
        gz_gzwd = (TextView) findViewById(R.id.gz_gzwd);
        gz_gzwd.setOnClickListener(this);

        gz_quanxuan = (TextView) findViewById(R.id.gz_quanxuan);
        gz_quanxuan.setOnClickListener(this);
        gz_shanchu = (TextView) findViewById(R.id.gz_shanchu);
        gz_shanchu.setOnClickListener(this);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        if(ismyfans){
            myFans();
        }else {
            myConcern();
        }
    }
    /***
     * 点击事件
     */
    private  boolean ismyfans=false;
    private  boolean isdianji=false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gz_wgzd://我关注的
                ismyfans=false;
                isdianji=true;
                gz_wgzd.setTextColor(Color.parseColor("#9854CF"));
                gz_gzwd.setTextColor(Color.parseColor("#000000"));
                gz_wgzd_tv.setVisibility(View.VISIBLE);
                gz_gzwd_tv.setVisibility(View.GONE);
                myConcern();
                setenable(true);
                if (gz_bianji.getVisibility() != View.VISIBLE) {
                    gz_bianji.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.gz_gzwd://关注我的
                ismyfans=true;
                isdianji=true;
                gz_wgzd.setTextColor(Color.parseColor("#000000"));
                gz_gzwd.setTextColor(Color.parseColor("#9854CF"));
                gz_wgzd_tv.setVisibility(View.GONE);
                gz_gzwd_tv.setVisibility(View.VISIBLE);
                myFans();
                setenable(false);
                if (gz_bianji.getVisibility() != View.INVISIBLE) {
                    gz_bianji.setVisibility(View.INVISIBLE);
                    gz_ll.setVisibility(View.GONE);
                    gz_bianji.setSelected(false);
                }
                break;
            case R.id.gz_bianji://编辑
                if (gz_bianji.isSelected()) {
                    gz_ll.setVisibility(View.GONE);
                    gz_bianji.setSelected(false);
                    if(adapter!=null){
                        adapter.setxzvis(false);
                    }
                } else {
                    gz_bianji.setSelected(true);
                    gz_ll.setVisibility(View.VISIBLE);
                    if (gz_listview.getChildCount() > 0) {
                        gz_quanxuan.setEnabled(true);
                        gz_shanchu.setEnabled(true);
                        gz_quanxuan.setTextColor(Color.parseColor("#000000"));
                        gz_shanchu.setTextColor(Color.parseColor("#000000"));
                    } else {
                        gz_quanxuan.setTextColor(Color.parseColor("#838383"));
                        gz_shanchu.setTextColor(Color.parseColor("#838383"));
                        gz_quanxuan.setEnabled(false);
                        gz_shanchu.setEnabled(false);
                    }
                    if(adapter!=null){
                        adapter.setxzvis(true);
                    }
                }
                break;
            case R.id.gz_quanxuan://全选
                if (seladd) {
                    gz_quanxuan.setText("全选");
                    seladd = false;
                    adapter.setallsel();
                    idmeps.clear();
                } else {
                    gz_quanxuan.setText("全不选");
                    seladd = true;
                    adapter.setallsel();
                    for (int i = 0; i < usermeslist.size(); i++) {
                        String pid = usermeslist.get(i).getPid();
                        idmeps.put(pid, pid);
                    }
                }
                break;
            case R.id.gz_shanchu://删除
                for (String key : idmeps.keySet()) {
                    if (str.equals("")) {
                        str = key;
                    } else {
                        str = str + "," + key;
                    }
                }
                if (str.length() == 0) {
                    showMessage(getResources().getString(R.string.delguanzhuhint));
                    return;
                }
                delMyConcern();
                break;
            case R.id.gz_back:
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
    private void setenable(boolean boo) {
        gz_wgzd.setEnabled(!boo);
        gz_gzwd.setEnabled(boo);
    }

    /***
     * 我关注的
     */
    private GuanzhuAdapter adapter;
    private ArrayList<UserMes> usermeslist;

    private void myConcern() {
        gz_listview.setVisibility(View.GONE);
        gz_zwsj.setVisibility(View.GONE);
        if(isdianji){
            showloading();
        }
        xhttp = new Xhttp(URLManager.myConcern);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray userList = obj.getJSONArray("userList");
                        usermeslist = new ArrayList<>();
                        idmeps = new HashMap<>();
                        for (int i = 0; i < userList.length(); i++) {
                            JSONObject gg = userList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setHead(gg.getString("head"));
                            usetmes.setId(gg.getString("id"));
                            usetmes.setPid(gg.getString("pid"));
                            usetmes.setLevel(gg.getString("level"));
                            usetmes.setNickname(gg.getString("nickName"));
                            usetmes.setSex(gg.getString("sex"));
                            usermeslist.add(usetmes);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new GuanzhuAdapter(context, usermeslist);
                                gz_listview.setVisibility(View.VISIBLE);
                                gz_zwsj.setVisibility(View.GONE);
                                gz_listview.setAdapter(adapter);
                                // 停止刷新
                                mSwipeLayout.setRefreshing(false);
                            }
                        }, 500); // 0.5秒后发送消息，停止刷新
                    } catch (Exception e) {
                        e.printStackTrace();
                        zwsj(obj.getString("meg"));
                        mSwipeLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    zwsj("");
                    mSwipeLayout.setRefreshing(false);
                }

                if(isdianji){
                    dismissloading();
                    isdianji=false;
                }
            }

            @Override
            public void onError() {
                mSwipeLayout.setRefreshing(false);
                zwsj("");
                if(isdianji){
                    dismissloading();
                    isdianji=false;
                }
            }
        });
    }

    private void zwsj(String title) {
        gz_zwsj.setVisibility(View.VISIBLE);
        gz_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        gz_zwsj.setText(title);
    }

    /***
     * 关注我的
     */
    private void myFans() {
        gz_listview.setVisibility(View.GONE);
        gz_zwsj.setVisibility(View.GONE);
        if(isdianji){
            showloading();
        }
        xhttp = new Xhttp(URLManager.myFans);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray userList = obj.getJSONArray("userList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < userList.length(); i++) {
                            JSONObject gg = userList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setHead(gg.getString("head"));
                            usetmes.setId(gg.getString("id"));
                            usetmes.setLevel(gg.getString("level"));
                            usetmes.setNickname(gg.getString("nickName"));
                            usetmes.setSex(gg.getString("sex"));
                            usermeslist.add(usetmes);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new GuanzhuAdapter(context, usermeslist);
                                gz_listview.setVisibility(View.VISIBLE);
                                gz_zwsj.setVisibility(View.GONE);
                                gz_listview.setAdapter(adapter);
                                // 停止刷新
                                mSwipeLayout.setRefreshing(false);
                            }
                        }, 500); // 0.5秒后发送消息，停止刷新
                    } catch (Exception e) {
                        e.printStackTrace();
                        mSwipeLayout.setRefreshing(false);
                        zwsj(obj.getString("meg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSwipeLayout.setRefreshing(false);
                    zwsj("");
                }
                if(isdianji){
                    dismissloading();
                    isdianji=false;
                }
            }

            @Override
            public void onError() {
                zwsj("");
                mSwipeLayout.setRefreshing(false);
                if(isdianji){
                    dismissloading();
                    isdianji=false;
                }
            }
        });
    }

    /***
     * 删除我的关注
     */
    private String str = "";

    private void delMyConcern() {
        showloading();
        xhttp = new Xhttp(URLManager.delMyConcern);
        xhttp.add("str", str);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        myConcern();
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

    private Map<String, String> idmeps;
    private boolean seladd = false;

    private class GuanzhuAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;
        private boolean vis = false;

        public GuanzhuAdapter(Context context, ArrayList<UserMes> usermeslist) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.usermeslist = usermeslist;
            this.context = context;
            this.vis = false;
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
                convertView = minflater.inflate(R.layout.guanzhu_activity_item, null);
                holder = new ViewHolder();
                holder.gz_head = (RoundedImageView) convertView
                        .findViewById(R.id.gz_head);
                holder.gz_level = (TextView) convertView
                        .findViewById(R.id.gz_level);
                holder.gz_name = (TextView) convertView
                        .findViewById(R.id.gz_name);
                holder.gz_sex = (ImageView) convertView
                        .findViewById(R.id.gz_sex);
                holder.gz_xuanzhong = (ImageView) convertView
                        .findViewById(R.id.gz_xuanzhong);
                holder.gz_ll = (LinearLayout) convertView
                        .findViewById(R.id.gz_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

             final String id = usermes.getId();
            final String pid = usermes.getPid();
            String head = usermes.getHead();
            String level = usermes.getLevel();
            String sex = usermes.getSex();
            String nickname = usermes.getNickname();

            holder.gz_level.setText(level);
            holder.gz_name.setText(nickname);
            Picasso.with(context).load(URLManager.head + head).error(R.mipmap.fw658).into(holder.gz_head);
            if (sex.equals("男")) {
                holder.gz_sex.setImageResource(R.mipmap.nans);
            } else {
                holder.gz_sex.setImageResource(R.mipmap.nvs);
            }
            holder.gz_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id, "2");
                }
            });
            holder.gz_xuanzhong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        v.setSelected(false);
                        idmeps.remove(pid);
                        if (idmeps.size() == 0) {//
                            gz_quanxuan.setText("全选");
                            seladd = false;
                        }
                    } else {
                        v.setSelected(true);
                        idmeps.put(pid, pid);
                        if (idmeps.size() == usermeslist.size()) {
                            gz_quanxuan.setText("全不选");
                            seladd = true;
                        }
                    }
                }
            });
            setsel(holder);
            return convertView;
        }

        public void setallsel() {
            notifyDataSetChanged();
        }

        public void setxzvis(boolean vis) {
            this.vis = vis;
            notifyDataSetChanged();
        }

        private void setsel(ViewHolder holder) {
            if (seladd) {
                holder.gz_xuanzhong.setSelected(seladd);
            } else {
                holder.gz_xuanzhong.setSelected(seladd);
            }
            if (!vis) {
                holder.gz_xuanzhong.setVisibility(View.GONE);
            } else {
                holder.gz_xuanzhong.setVisibility(View.VISIBLE);
            }
        }


        class ViewHolder {
            private LinearLayout gz_ll;
            private TextView gz_level, gz_name;
            private RoundedImageView gz_head;
            private ImageView gz_xuanzhong, gz_sex;
        }
    }
}
