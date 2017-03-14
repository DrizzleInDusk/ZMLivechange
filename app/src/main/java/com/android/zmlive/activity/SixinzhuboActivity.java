package com.android.zmlive.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.LwMess;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SixinzhuboActivity extends BaseActivity implements View.OnClickListener {
    private String zbid = "";
    private LwAdapter lwadapter;
    private GridView liwu_gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.sixinzhubo_activity);
        zbid = getIntent().getStringExtra("zbid");
        liwu_gridview = (GridView) findViewById(R.id.liwu_gridview);
        findview();
        getGiftsList("hot");
        changetv(liwuremen_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        balance();
    }

    private EditText sxzb_yysj, sxzb_yydd;
    private TextView sxzb_yue,liwu_zwsj;
    private RelativeLayout audience_liwuremen, audience_liwugds, audience_liwush, audience_liwudd;
    private TextView liwuremen_tv, liwugds_tv, liwush_tv, liwudd_tv;
    private void findview() {
        findViewById(R.id.sxzbback).setOnClickListener(this);
        findViewById(R.id.sxzb_yes).setOnClickListener(this);
        liwu_zwsj = (TextView) findViewById(R.id.liwu_zwsj);
        sxzb_yue = (TextView) findViewById(R.id.sxzb_yue);
        sxzb_yue.setOnClickListener(this);
        sxzb_yysj = (EditText) findViewById(R.id.sxzb_yysj);
        sxzb_yydd = (EditText) findViewById(R.id.sxzb_yydd);
        audience_liwuremen = (RelativeLayout) findViewById(R.id.audience_liwuremen);//礼物热门
        liwuremen_tv = (TextView) findViewById(R.id.liwuremen_tv);//礼物热门
        audience_liwuremen.setOnClickListener(this);
        audience_liwugds = (RelativeLayout) findViewById(R.id.audience_liwugds);//礼物高大上
        liwugds_tv = (TextView) findViewById(R.id.liwugds_tv);//礼物高大上
        audience_liwugds.setOnClickListener(this);
        audience_liwush = (RelativeLayout) findViewById(R.id.audience_liwush);//礼物奢华
        liwush_tv = (TextView) findViewById(R.id.liwush_tv);//礼物奢华
        audience_liwush.setOnClickListener(this);
        audience_liwudd = (RelativeLayout) findViewById(R.id.audience_liwudd);//礼物低调
        liwudd_tv = (TextView) findViewById(R.id.liwudd_tv);//礼物低调
        audience_liwudd.setOnClickListener(this);

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
            case R.id.sxzb_yes:
                String yysj = sxzb_yysj.getText().toString();
                if (yysj.equals("")) {
                    showMessage(getResources().getString(R.string.qingshuru) + getResources().getString(R.string.yysj));
                    return;
                }
                String yydd = sxzb_yydd.getText().toString();
                if (yydd.equals("")) {
                    showMessage(getResources().getString(R.string.qingshuru) + getResources().getString(R.string.yydd));
                    return;
                }
                if (diamond.equals("0")) {
                    showMessage(getResources().getString(R.string.qingxzlw));
                    return;
                }
                try {
                    int d = Integer.parseInt(diamond);
                    if (d > dm) {
                        showMessage(getResources().getString(R.string.nullyue));
                        return;
                    }
                    yyzb(yysj, yydd, diamond);
                } catch (Exception e) {
                    e.printStackTrace();
                    yyzb(yysj, yydd, diamond);
                }
                break;
            case R.id.sxzb_yue:/*播钻充值*/
                String totle= sxzb_yue.getText().toString();
                ActivityJumpControl.getInstance(activity).gotoChongzhiActivity(totle);
                break;
            case R.id.sxzbback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.audience_liwuremen://礼物热门
                changetv(liwuremen_tv);
                getGiftsList("hot");
                break;
            case R.id.audience_liwugds://礼物高大上
                changetv(liwugds_tv);
                getGiftsList("gds");
                break;
            case R.id.audience_liwush://礼物奢华
                changetv(liwush_tv);
                getGiftsList("sh");
                break;
            case R.id.audience_liwudd://礼物低调
                changetv(liwudd_tv);
                getGiftsList("dd");
                break;
        }
    }

    private ArrayList<TextView> tvlist = new ArrayList<>();

    private void changetv(TextView tv) {//aa7E3AB3
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setBackgroundColor(Color.parseColor("#00000000"));
        }
        tv.setBackgroundColor(Color.parseColor("#aa7E3AB3"));
        tvlist.add(tv);
    }
    /***
     * 礼物集合
     */
    private ArrayList<LwMess> lwlist;

    private void getGiftsList(String type) {
        showloading();
        liwu_gridview.setVisibility(View.GONE);
        liwu_zwsj.setVisibility(View.GONE);
        xhttp = new Xhttp(URLManager.getGiftsList);
        xhttp.add("type", type);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                String mess = "";
                try {
                    JSONObject obj = new JSONObject(result);
                    mess = obj.getString("meg");
                    JSONArray giftsList = obj.getJSONArray("giftsList");
                    lwlist = new ArrayList<>();
                    for (int i = 0; i < giftsList.length(); i++) {
                        JSONObject gift = giftsList.getJSONObject(i);
                        lwlist.add(new LwMess(gift.getString("id"), gift.getString("price"), gift.getString("name"), gift.getString("picture")));
                    }
                    liwu_gridview.setVisibility(View.VISIBLE);
                    lwadapter = new LwAdapter(context, lwlist);
                    liwu_gridview.setAdapter(lwadapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    liwu_zwsj.setVisibility(View.VISIBLE);
                    liwu_zwsj.setText(mess);
                }
                dismissloading();
            }

            @Override
            public void onError() {
                liwu_zwsj.setVisibility(View.VISIBLE);
                dismissloading();
            }
        });
    }
    /***
     * 获取用户余额和收益
     */
    private int dm = 0;

    private void balance() {
        xhttp = new Xhttp(URLManager.balance);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    dm = obj.getInt("diamond");
                    sxzb_yue.setText(dm + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    sxzb_yue.setText("0");
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    private class LwAdapter extends BaseAdapter {
        private Context context;
        private int itemat = -1;
        private LayoutInflater minflater;
        private ArrayList<LwMess> lwlist;

        public LwAdapter(Context context, ArrayList<LwMess> lwlist) {
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
                convertView = minflater.inflate(R.layout.sixinzhubo_activity_item, null);
                holder = new ViewHolder();
                holder.liwuitem_img = (ImageView) convertView
                        .findViewById(R.id.liwuitem_img);
                holder.liwuitem_money = (TextView) convertView
                        .findViewById(R.id.liwuitem_money);
                holder.liwuitem_title = (TextView) convertView
                        .findViewById(R.id.liwuitem_title);
                holder.liwuitem = (LinearLayout) convertView
                        .findViewById(R.id.liwuitem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LwMess lwmess = lwlist.get(position);
            String title = lwmess.getTitle();
            final String money = lwmess.getMoney();
            final String img = lwmess.getMipmapid();
            String pic = lwmess.getPic();
            holder.liwuitem_money.setText(money + "钻");
            holder.liwuitem_title.setText(title);
            Picasso.with(context).load(URLManager.gifts+pic).into(holder.liwuitem_img);

            holder.liwuitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diamond = money;
                    itemat = position;
                    giftsid = img;
                    notifyDataSetChanged();
                }
            });
            viewcolor(position, holder);
            return convertView;
        }

        private void viewcolor(int position, ViewHolder holder) {
            if (position == itemat) {
                holder.liwuitem.setBackgroundColor(Color.parseColor("#aadfafdd"));
            } else {
                holder.liwuitem.setBackgroundColor(Color.parseColor("#ffffff"));

            }
        }

        class ViewHolder {
            private ImageView liwuitem_img;
            private LinearLayout liwuitem;
            private TextView liwuitem_money, liwuitem_title;
        }

    }

    /***
     * 私信主播
     */
    private String diamond = "0";
    private String giftsid = "";

    private void yyzb(String btime, String place, String money) {
        showloading();
        xhttp = new Xhttp(URLManager.yyzb);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("btime", btime);
        xhttp.add("place", place);
        xhttp.add("money", money);
        xhttp.add("zbid", zbid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        finish();
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

}
