package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.LiveerMess;
import com.android.zmlive.permission.MPermission;
import com.android.zmlive.permission.annotation.OnMPermissionDenied;
import com.android.zmlive.permission.annotation.OnMPermissionGranted;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SousuoActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.sousuo_activity);
        findview();
        appSearchList();
    }

    private TextView sousuo_et, sousuo_totop;
    private ListView sousuo_listview;
    private GridView ss_gridview;
    private TextView sousuoback, ss_zwsj, ss_tjtitle;
    private ScrollView sousuo_scroll;
    private int scrollY = 0;// 标记上次滑动位置
    private View scrochildView;

    private void findview() {
        sousuo_scroll = (ScrollView) findViewById(R.id.sousuo_scroll);
        sousuo_totop = (TextView) findViewById(R.id.sousuo_totop);
        sousuo_totop.setOnClickListener(this);
        sousuo_listview = (ListView) findViewById(R.id.sousuo_listview);
        ss_gridview = (GridView) findViewById(R.id.ss_gridview);
        ss_zwsj = (TextView) findViewById(R.id.ss_zwsj);
        ss_tjtitle = (TextView) findViewById(R.id.ss_tjtitle);
        sousuoback = (TextView) findViewById(R.id.sousuoback);
        sousuoback.setOnClickListener(this);
        sousuo_et = (TextView) findViewById(R.id.sousuo_et);
        sousuo_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                souword = s.toString();
                if (souword.length() > 0) {
                    sousuoback.setText(getResources().getString(R.string.sousuo));
                } else {
                    sousuoback.setText(getResources().getString(R.string.quxiao));
                }
            }
        });

        if (scrochildView == null) {
            scrochildView = sousuo_scroll.getChildAt(0);
        }
        sousuo_scroll.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            /**
             * ScrollView 停止
             *
             * @param view
             */
            private void handleStop(Object view) {
                LogUtil.showILog(Tag, "handleStop");
                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();
                doOnBorderListener();
            }
        });
    }

    private String Tag = "sousuoFragment";

    /**
     * ScrollView 的顶部，底部判断：
     * http://blog.csdn.net/qq_21376985
     * <p/>
     * 其中getChildAt表示得到ScrollView的child View， 因为ScrollView只允许一个child
     * view，所以scrochildView.getMeasuredHeight()表示得到子View的高度,
     * getScrollY()表示得到y轴的滚动距离，getHeight()为scrollView的高度。
     * 当getScrollY()达到最大时加上scrollView的高度就的就等于它内容的高度了啊~
     *
     * @param
     */
    private void doOnBorderListener() {
        // 底部判断
        if (scrochildView != null
                && scrochildView.getMeasuredHeight() <= sousuo_scroll.getScrollY()
                + sousuo_scroll.getHeight()&&scrollY!=0) {
            sousuo_totop.setVisibility(View.VISIBLE);
            LogUtil.showILog(Tag, "bottom");
        }
        // 顶部判断
        else if (sousuo_scroll.getScrollY() == 0) {
            sousuo_totop.setVisibility(View.GONE);
            LogUtil.showILog(Tag, "top");
        } else if (sousuo_scroll.getScrollY() > 130) {
            if (sousuo_totop.getVisibility() == View.GONE) {
                sousuo_totop.setVisibility(View.VISIBLE);
            }
            LogUtil.showILog(Tag, "test");
        }

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.sousuoback:
                String ssb = sousuoback.getText().toString();
                if (ssb.equals(getResources().getString(R.string.quxiao))) {
                    ActivityJumpControl.getInstance(activity).popActivity(activity);
                    finish();
                } else {
                    String sskwd = sousuo_et.getText().toString();
                    searchNameCard(sskwd);
                }
                break;
            case R.id.sousuo_totop:
                sousuo_scroll.post(new Runnable() {
                    @Override
                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
//                        scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
                        sousuo_scroll.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                sousuo_totop.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    /***
     * 选择话题
     */
    private HtAdapter htadapter;
    private ArrayList<String> htlist;

    private void appSearchList() {
        ss_gridview.setVisibility(View.GONE);
        ss_zwsj.setVisibility(View.GONE);
        showloading();
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
                        htadapter = new HtAdapter(context, htlist);
                        ss_gridview.setVisibility(View.VISIBLE);
                        ss_zwsj.setVisibility(View.GONE);
                        ss_gridview.setAdapter(htadapter);
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
                dismissloading();
                zwsj("");
            }
        });
    }

    /***
     * 搜索
     */
    private String souword = "";
    private SouAdapter adapter;
    private ArrayList<LiveerMess> usermeslist;

    private void searchNameCard(String souword) {
        ss_gridview.setVisibility(View.GONE);
        sousuo_listview.setVisibility(View.GONE);
        ss_tjtitle.setVisibility(View.GONE);
        ss_zwsj.setVisibility(View.GONE);
        showloading();
        xhttp = new Xhttp(URLManager.searchNameCard);
        xhttp.add("keyword", souword);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray ncList = obj.getJSONArray("ncList");
                        usermeslist = new ArrayList<>();
                        MainActivity.liveerlist = new ArrayList<>();
                        for (int i = 0; i < ncList.length(); i++) {
                            JSONObject nc = ncList.getJSONObject(i);
                            LiveerMess livver = new LiveerMess();
                            String status= nc.getString("status");
                            livver.setCid(nc.getString("cid"));
                            livver.setCity(nc.getString("city"));
                            livver.setHead(nc.getString("head"));
                            livver.setName(nc.getString("nickname"));
                            livver.setSex(nc.getString("sex"));
                            livver.setNumber(nc.getString("number"));
                            livver.setId(nc.getString("uid"));
                            livver.setSign(status);
                            livver.setProfit(nc.getString("profit"));
                            livver.setPraise(nc.getString("praise"));
                            livver.setPassword(nc.getString("passState"));
                            usermeslist.add(livver);
                            if (status.equals("1")) {
                                MainActivity.liveerlist.add(livver);
                            }
                        }
                        adapter = new SouAdapter(context, usermeslist);
                        sousuo_listview.setVisibility(View.VISIBLE);
                        ss_tjtitle.setVisibility(View.VISIBLE);
                        sousuo_listview.setAdapter(adapter);
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
        ss_zwsj.setVisibility(View.VISIBLE);
        sousuo_listview.setVisibility(View.GONE);
        ss_gridview.setVisibility(View.GONE);
        ss_tjtitle.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        ss_zwsj.setText(title);
    }

    /***
     * 进入直播
     */
    private void joinRoom(final String cid, final String profit, String password) {
        showloading();
        xhttp = new Xhttp(URLManager.joinRoom);
        xhttp.add("cid", cid);
        if (!password.equals("")) {
            xhttp.add("password", password);
        }
        xhttp.add("gid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    hasjion = true;
                    if (status.equals("1")) {
                        String rtmpPullUrl = obj.getString("rtmpPullUrl");
                        String roomid = obj.getString("roomid");
                        MainActivity.roompath = rtmpPullUrl;
                        MainActivity.joinroomid = roomid;
                        MainActivity.position = pos;
                        MainActivity.ucid = ucid;
                        MyData.CID = cid;
                        MainActivity.liveername = liveer;
                        MainActivity.head = mhead;
                        MyData.LEVERPROFIT = profit;
                        requestBasicPermission(); // 申请APP基本权限
                    } else {
                        String mess = obj.getString("meg");
                        showMessage(mess);
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

    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    /**
     * 基本权限管理
     */
    public void requestBasicPermission() {
        MPermission.with((Activity) context)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.READ_PHONE_STATE)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        if (MainActivity.roompath.equals("")) {
            showMessage(context.getResources().getString(R.string.livenull));
            return;
        }
        ActivityJumpControl.getInstance(activity).gotoAudienceActivity(MainActivity.roompath, MainActivity.joinroomid,ucid,MainActivity.head,MainActivity.liveername);
//        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        showMessage(context.getResources().getString(R.string.nullpermiss));
    }
    private boolean hasjion = true;
    private int pos;
    private String ucid;
    private String liveer;
    private String mhead;

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
                    sousuo_et.setText(name);
                    searchNameCard(name);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView huati_item;
        }

    }

    private class SouAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater minflater;
        private ArrayList<LiveerMess> liveerList;

        public SouAdapter(Context context, ArrayList<LiveerMess> liveerList) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.liveerList = liveerList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return liveerList.size();
        }

        @Override
        public Object getItem(int position) {
            return liveerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.sousuo_activity_item, null);
                holder = new ViewHolder();
                holder.sousuo_touxiang = (RoundedImageView) convertView
                        .findViewById(R.id.sousuo_touxiang);
                holder.sousuo_address = (TextView) convertView
                        .findViewById(R.id.sousuo_address);
                holder.sousuo_name = (TextView) convertView
                        .findViewById(R.id.sousuo_name);
                holder.sousuo_sex = (ImageView) convertView
                        .findViewById(R.id.sousuo_sex);
                holder.sousuo_time = (TextView) convertView
                        .findViewById(R.id.sousuo_time);
                holder.sousuo_xihuan = (TextView) convertView
                        .findViewById(R.id.sousuo_xihuan);
                holder.sousuo_onlinepeo = (TextView) convertView
                        .findViewById(R.id.sousuo_onlinepeo);
                holder.sousuo_img = (ImageView) convertView
                        .findViewById(R.id.sousuo_img);

                holder.sousuo_itemll = (LinearLayout) convertView
                        .findViewById(R.id.sousuo_itemll);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LiveerMess liveerMess = liveerList.get(position);

            final String cid = liveerMess.getCid();
            final String profit = liveerMess.getProfit();
            final String password = liveerMess.getPassword();
            String address = liveerMess.getCity();
            String head = liveerMess.getHead();
            String praise = liveerMess.getPraise();
            final String name = liveerMess.getName();
            String sex = liveerMess.getSex();
            String number = liveerMess.getNumber();
            final String id = liveerMess.getId();
            String status = liveerMess.getSign();
            head = URLManager.head + head;
            Picasso.with(context).load(head).error(R.mipmap.backgrimg).into(holder.sousuo_img);
            Picasso.with(context).load(head).into(holder.sousuo_touxiang);

            holder.sousuo_address.setText(address);
            holder.sousuo_name.setText(name);
            holder.sousuo_xihuan.setText(praise);
            holder.sousuo_onlinepeo.setText(number + " 人在看");
            if (sex.equals("男")) {
                holder.sousuo_sex.setImageResource(R.mipmap.nanwhite);
            } else {
                holder.sousuo_sex.setImageResource(R.mipmap.nvwhite);
            }

            if (status.equals("1")) {
                holder.sousuo_time.setText(context.getResources().getString(R.string.zhibozhong));
                final String finalHead = head;
                holder.sousuo_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = position;
                        ucid = id;
                        liveer = name;
                        mhead = finalHead;
                        if (!password.equals("pass-null")) {
                            inpasw(cid, profit, password);
                        } else {
                            if (hasjion) {
                                hasjion = false;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        joinRoom(cid, profit, "");
                                    }
                                }, 600);
                            }
                        }
                    }
                });
            } else {
                holder.sousuo_time.setText(context.getResources().getString(R.string.nobozhong));
                holder.sousuo_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogmess();
                    }
                });
            }
            holder.sousuo_itemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id, "2");
                }
            });
            return convertView;
        }

        class ViewHolder {
            private RoundedImageView sousuo_touxiang;
            private ImageView sousuo_img, sousuo_sex;
            private LinearLayout sousuo_itemll;
            private TextView sousuo_name, sousuo_address, sousuo_time, sousuo_xihuan, sousuo_onlinepeo;
        }
    }

    /**
     * dialog 消息提示
     */
    private void dialogmess() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_mess);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        TextView dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
        TextView  dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
        dialog_mess_title.setText("消息提示");
        dialog_mess_con.setText(getResources().getString(R.string.nolivehint));

        TextView dialog_mess_quxiao= (TextView) dialog.findViewById(R.id.dialog_mess_quxiao);
        dialog_mess_quxiao.setVisibility(View.INVISIBLE);
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private AlertDialog dialog;
    private EditText content;

    private void inpasw(final String cid, final String profit, final String password) {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_inpaswo, null);
        dialog.setView(view);
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        content = (EditText) dialog.findViewById(R.id.dialog_content);
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ps = content.getText().toString().trim();
                joinRoom(cid, profit, ps);
                dialog.dismiss();

            }
        });
    }
}
