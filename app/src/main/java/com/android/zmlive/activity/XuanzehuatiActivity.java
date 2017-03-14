package com.android.zmlive.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class XuanzehuatiActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private boolean isrefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xuanzehuati_activity);
        findview();
        appSearchList();
    }

    private GridView xzht_gridview;
    private TextView xzht_zwsj, xzht_cancel, xzht_et;
    private SwipeRefreshLayout mSwipeLayout;

    private void findview() {
        xzht_gridview = (GridView) findViewById(R.id.xzht_gridview);
        xzht_zwsj = (TextView) findViewById(R.id.xzht_zwsj);
        xzht_et = (TextView) findViewById(R.id.xzht_et);
        xzht_cancel = (TextView) findViewById(R.id.xzht_cancel);
        xzht_cancel.setOnClickListener(this);
        xzht_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() != 0) {
                    xzht_cancel.setText(getResources().getString(R.string.yes1));
                } else {
                    xzht_cancel.setText(getResources().getString(R.string.quxiao));
                }
            }
        });
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.xzht_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(100);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xzht_cancel:
                String cancel = xzht_cancel.getText().toString().trim();
                if (cancel.equals(getResources().getString(R.string.yes1))) {
                    String name = xzht_et.getText().toString().trim();
                    Intent intent = new Intent();
                    intent.putExtra("huati", name);
                    setResult(RESULT_OK, intent);
                }
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }


    @Override
    public void onRefresh() {
        appSearchList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止刷新
                mSwipeLayout.setRefreshing(false);
            }
        }, 1000); // 1秒后发送消息，停止刷新
    }

    /***
     * 选择话题
     */
    private HtAdapter adapter;
    private ArrayList<String> htlist;

    private void appSearchList() {
        xzht_gridview.setVisibility(View.GONE);
        xzht_zwsj.setVisibility(View.GONE);
        if (isrefresh) {
            showloading();
        }
        xhttp = new Xhttp(URLManager.appSearchList);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray searchList = obj.getJSONArray("searchList");
                        htlist = new ArrayList<>();
                        for (int i = 0; i < searchList.length(); i++) {
                            String gg = searchList.getString(i);
                            htlist.add(gg);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new HtAdapter(context, htlist);
                                xzht_gridview.setVisibility(View.VISIBLE);
                                xzht_zwsj.setVisibility(View.GONE);
                                xzht_gridview.setAdapter(adapter);
                                // 停止刷新
                                mSwipeLayout.setRefreshing(false);
                            }
                        }, 1000); // 1秒后发送消息，停止刷新
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
                if (isrefresh) {
                    dismissloading();
                    isrefresh = true;
                }
            }

            @Override
            public void onError() {
                mSwipeLayout.setRefreshing(false);
                if (isrefresh) {
                    dismissloading();
                    isrefresh = true;
                }
                zwsj("");
            }
        });
    }

    private void zwsj(String title) {
        xzht_zwsj.setVisibility(View.VISIBLE);
        xzht_gridview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        xzht_zwsj.setText(title);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private class HtAdapter extends BaseAdapter {
        private Context context;
        private int itemat = -1;
        private LayoutInflater minflater;
        private ArrayList<String> lwlist;

        public HtAdapter(Context context, ArrayList<String> lwlist) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.lwlist = lwlist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return lwlist.size();
        }

        @Override
        public Object getItem(int position) {
            return lwlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.xuanzehuati_activity_item, null);
                holder = new ViewHolder();
                holder.huati_item = (TextView) convertView
                        .findViewById(R.id.huati_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final String name = lwlist.get(position);
            holder.huati_item.setText(name);
            holder.huati_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("huati", name);
                    setResult(RESULT_OK, intent);
                    ActivityJumpControl.getInstance(activity).popActivity(activity);
                    finish();
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView huati_item;
        }

    }
}
