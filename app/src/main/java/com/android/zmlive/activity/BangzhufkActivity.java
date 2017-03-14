package com.android.zmlive.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.UserMes;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BangzhufkActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.bangzhufk_activity);
        findview();
        helpList();
    }
    private ListView bzfk_listview;
    private TextView bzfk_zwsj;

    private void findview() {
        findViewById(R.id.bzfkback).setOnClickListener(this);
        findViewById(R.id.bzfk_yjfk).setOnClickListener(this);
        bzfk_listview = (ListView) findViewById(R.id.bzfk_listview);
        bzfk_zwsj = (TextView) findViewById(R.id.bzfk_zwsj);

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bzfk_yjfk://意见反馈
                ActivityJumpControl.getInstance(activity).gotoYijianfkActivity();
                break;
            case R.id.bzfkback:
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
    /***
     * 帮助问答
     */
    private BangzhufkAdapter adapter;
    private ArrayList<UserMes> usermeslist;

    private void helpList() {
        showloading();
        xhttp = new Xhttp(URLManager.helpList);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray helpList = obj.getJSONArray("helpList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < helpList.length(); i++) {
                            JSONObject gg = helpList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setAnswer(gg.getString("answer"));
                            usetmes.setQuestion(gg.getString("question"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new BangzhufkAdapter(context, usermeslist);
                        bzfk_listview.setVisibility(View.VISIBLE);
                        bzfk_zwsj.setVisibility(View.GONE);
                        bzfk_listview.setAdapter(adapter);
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
        bzfk_zwsj.setVisibility(View.VISIBLE);
        bzfk_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        bzfk_zwsj.setText(title);
    }

    private class BangzhufkAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public BangzhufkAdapter(Context context, ArrayList<UserMes> usermeslist) {
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
                convertView = minflater.inflate(R.layout.bangzhufk_activity_item, null);
                holder = new ViewHolder();
                holder.item_one = (TextView) convertView
                        .findViewById(R.id.bzfk_item_one);
                holder.item_two = (TextView) convertView
                        .findViewById(R.id.bzfk_item_two);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

            String question = usermes.getQuestion();
            String answer = usermes.getAnswer();

            holder.item_one.setText(question);
            holder.item_two.setText(answer);

            return convertView;
        }

        class ViewHolder {
            private TextView item_one, item_two;
        }
    }
}
