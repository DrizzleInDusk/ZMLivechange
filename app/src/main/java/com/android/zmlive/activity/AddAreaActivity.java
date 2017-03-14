package com.android.zmlive.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.Area;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 选择地址
 * Created by Kkan on 2016/7/9.
 */
public class AddAreaActivity extends BaseActivity implements View.OnClickListener ,SwipeRefreshLayout.OnRefreshListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.addarea_activity);
        findview();
        firstarea_rl.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstarea_rl: {
                pid = "0";
                oneaddress="";
                Tag = "1";
                changeview(firstarea_rl, firstarea_tv);
                firstarea_tv.setText(context.getResources().getString(R.string.sheng));
                secondarea_tv.setText(context.getResources().getString(R.string.shi));
                thirdlyarea_tv.setText(context.getResources().getString(R.string.xian));
                secondarea_rl.setEnabled(false);
                thirdlyarea_rl.setEnabled(false);
                getAreas();
            }
            break;
            case R.id.secondarea_rl: {
                Tag = "2";
                if(!oneaddress.equals("")){
                    pid= oneaddress;
                }
                changeview(secondarea_rl, secondarea_tv);
                secondarea_tv.setText(context.getResources().getString(R.string.shi));
                thirdlyarea_tv.setText(context.getResources().getString(R.string.xian));
                thirdlyarea_rl.setEnabled(false);
                getAreas();
            }
            break;
            case R.id.thirdlyarea_rl: {
                Tag = "3";
                changeview(thirdlyarea_rl, thirdlyarea_tv);
                thirdlyarea_tv.setText(context.getResources().getString(R.string.xian));
                getAreas();
            }
            break;
            case R.id.back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

    private RelativeLayout firstarea_rl, secondarea_rl, thirdlyarea_rl;
    private TextView firstarea_tv, secondarea_tv, thirdlyarea_tv;
    private ListView area_viewp;
    private ScrollView area_sc;
    private SwipeRefreshLayout mSwipeLayout;
    private void findview() {
        area_sc = (ScrollView) findViewById(R.id.area_sc);
        area_viewp = (ListView) findViewById(R.id.area_viewp);
        area_viewp.setFocusable(false);

        firstarea_rl = (RelativeLayout) findViewById(R.id.firstarea_rl);
        firstarea_rl.setOnClickListener(this);
        secondarea_rl = (RelativeLayout) findViewById(R.id.secondarea_rl);
        secondarea_rl.setOnClickListener(this);
        thirdlyarea_rl = (RelativeLayout) findViewById(R.id.thirdlyarea_rl);
        thirdlyarea_rl.setOnClickListener(this);

        firstarea_tv = (TextView) findViewById(R.id.firstarea_tv);
        secondarea_tv = (TextView) findViewById(R.id.secondarea_tv);
        thirdlyarea_tv = (TextView) findViewById(R.id.thirdlyarea_tv);

        findViewById(R.id.back).setOnClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.area_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    private ArrayList<RelativeLayout> rlist = new ArrayList<RelativeLayout>();
    private ArrayList<TextView> tlist = new ArrayList<TextView>();

    private void changeview(RelativeLayout rl, TextView tv) {
        for (int i = 0; i < rlist.size(); i++) {
            rlist.remove(i).setSelected(false);
        }
        rl.setSelected(true);
        rlist.add(rl);
        for (int k = 0; k < tlist.size(); k++) {
            tlist.remove(k).setTextColor(Color.parseColor("#333333"));
        }
        tv.setTextColor(Color.parseColor("#9854CF"));
        tlist.add(tv);
    }

    @Override
    public void onRefresh() {
        getAreas();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                mSwipeLayout.setRefreshing(false);
            }
        }, 2000); // 5秒后发送消息，停止刷新
    }

    /***
     * 获取地区列表
     */
    private ArrayList<Area> areaList;
    private String pid = "0";
    private String Tag = "1";
    private AreaAdapter adapter;

    private void getAreas() {
        showloading();
        xhttp=new Xhttp(URLManager.getAreas);
        xhttp.add("pid", pid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONArray areasList = obj.getJSONArray("areasList");
                    areaList = new ArrayList<>();
                    for (int i = 0; i < areasList.length(); i++) {
                        JSONObject areas = areasList.getJSONObject(i);
                        Area area = new Area();
                        area.setAreaName(areas.getString("areaName"));
                        area.setId(areas.getString("id"));
                        areaList.add(area);
                    }
                    adapter = new AreaAdapter(context, areaList);
                    area_viewp.setAdapter(adapter);
                    area_sc.fullScroll(ScrollView.FOCUS_UP);
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
private String oneaddress="";
    /**
     * 地区列表
     */
    public class AreaAdapter extends BaseAdapter {
        private ArrayList<Area> myAddresses;
        private Context context;
        private LayoutInflater minflater;

        public AreaAdapter(Context context, ArrayList<Area> myAddresses) {
            this.minflater = LayoutInflater.from(context);
            this.context = context;
            this.myAddresses = myAddresses;
        }

        @Override
        public int getCount() {
            if (null != myAddresses) {
                return myAddresses.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return myAddresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.addareas_activity_item, null);
                holder = new ViewHolder();
                holder.areaitem_name = (TextView) convertView
                        .findViewById(R.id.areaitem_name);
                holder.areaitem_rl = (RelativeLayout) convertView
                        .findViewById(R.id.areaitem_rl);
                holder.areaitem_rightback = (ImageView) convertView
                        .findViewById(R.id.areaitem_rightgo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final String name = myAddresses.get(position).getAreaName();
            final String address_id = myAddresses.get(position).getId();
            holder.areaitem_name.setText(name);
            holder.areaitem_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Tag.equals("1")) {
                        secondarea_rl.setEnabled(true);
                        pid = address_id;
                        firstarea_tv.setText(name);
                        secondarea_rl.performClick();
                        oneaddress=address_id;
                    } else if (Tag.equals("2")) {
                        thirdlyarea_rl.setEnabled(true);
                        pid = address_id;
                        secondarea_tv.setText(name);
                        thirdlyarea_rl.performClick();
                    } else if (Tag.equals("3")) {
                        Intent intent = new Intent();
                        intent.putExtra("address", name);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
            return convertView;
        }


        class ViewHolder {
            TextView areaitem_name;
            RelativeLayout areaitem_rl;
            ImageView areaitem_rightback;
        }
    }
}
