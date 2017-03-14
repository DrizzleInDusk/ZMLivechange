package com.android.zmlive.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.AddAreaActivity;
import com.android.zmlive.activity.MainActivity;
import com.android.zmlive.entity.LiveerMess;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.PicassoImageLoader;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseFragment;
import com.android.zmlive.tool.utils.LogUtil;
import com.android.zmlive.view.MySwipeRefreshLayout;
import com.android.zmlive.view.RoundedImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * Created by Kkan on 2016/7/6.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private View contentView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_home, container, false);
            initView(contentView);
            MyData.CITY1 = MyData.LOCCITY;
            home_rl.setVisibility(View.VISIBLE);
            home_framelayout.setVisibility(View.GONE);
        }
        if (contentView != null) {
            return contentView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private boolean istc = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sousuo_rl://搜索
                ActivityJumpControl.getInstance(activity).gotoSousuoActivity();
                break;
            case R.id.zuixin_rl://最新
                istc = false;
                gotoFragment(3);
                break;
            case R.id.tongcheng_rl://同城
                if (istc) {
                    startActivityForResult(new Intent(activity, AddAreaActivity.class), 0x01);
                } else {
                    istc = true;
                    gotoFragment(4);
                }
                break;
            case R.id.guanzhu_rl://关注
                istc = false;
                gotoFragment(5);
                break;
            case R.id.hongli_rl://红利
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoHongliActivity();
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.home_totop://返回顶部
                home_scroll.post(new Runnable() {
                    @Override
                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
//                        scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
                        home_scroll.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                home_totop.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 0x01: {
                    MyData.CITY1 = data.getStringExtra("address");
                }
                break;
            }
        }
    }

    private ListView home_listview;
    private Banner home_banner;
    private FrameLayout home_framelayout;
    private TextView home_zuixin, home_tongcheng, home_guanzhu;
    private TextView home_totop, zuixin_line, tongcheng_line, guanzhu_line, hongli_line, hongli_point;
    private MySwipeRefreshLayout mSwipeLayout;
    private RelativeLayout home_rl;
    private ScrollView home_scroll;
    private int scrollY = 0;// 标记上次滑动位置
    private View scrochildView;
    private TextView dz_jiazai;

    private void initView(View v) {
        home_rl = (RelativeLayout) v.findViewById(R.id.home_rl);
        home_scroll = (ScrollView) v.findViewById(R.id.home_scroll);
        mSwipeLayout = (MySwipeRefreshLayout) v.findViewById(R.id.home_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        //  mSwipeLayout.setOnLoadListener(this);
        dz_jiazai = (TextView) v.findViewById(R.id.dz_jiazai);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setProgressBackgroundColor(android.R.color.white);
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);

        home_framelayout = (FrameLayout) v.findViewById(R.id.home_framelayout);
        home_rl.setVisibility(View.GONE);
        home_framelayout.setVisibility(View.GONE);
        v.findViewById(R.id.sousuo_rl).setOnClickListener(this);
        home_totop = (TextView) v.findViewById(R.id.home_totop);
        home_totop.setOnClickListener(this);
        v.findViewById(R.id.zuixin_rl).setOnClickListener(this);
        home_zuixin = (TextView) v.findViewById(R.id.home_zuixin);
        zuixin_line = (TextView) v.findViewById(R.id.zuixin_line);
        v.findViewById(R.id.tongcheng_rl).setOnClickListener(this);
        home_tongcheng = (TextView) v.findViewById(R.id.home_tongcheng);
        tongcheng_line = (TextView) v.findViewById(R.id.tongcheng_line);
        v.findViewById(R.id.guanzhu_rl).setOnClickListener(this);
        home_guanzhu = (TextView) v.findViewById(R.id.home_guanzhu);
        guanzhu_line = (TextView) v.findViewById(R.id.guanzhu_line);
        v.findViewById(R.id.hongli_rl).setOnClickListener(this);
        hongli_line = (TextView) v.findViewById(R.id.hongli_line);
        hongli_point = (TextView) v.findViewById(R.id.hongli_point);

        home_banner = (Banner) v.findViewById(R.id.home_banner);
        adapter = new LiveerAdapter(getActivity());

        home_listview = (ListView) v.findViewById(R.id.home_listview);
        home_listview.setAdapter(adapter);
        home_listview.setFocusable(false);

        if (scrochildView == null) {
            scrochildView = home_scroll.getChildAt(0);
        }
        home_scroll.setOnTouchListener(new View.OnTouchListener() {
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
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int scrollY = v.getScrollY();
                    int height = v.getHeight();
                    int scrollViewMeasuredHeight = home_scroll.getChildAt(0).getMeasuredHeight();
                    if (scrollY == 0) {
                    }
                    if ((scrollY + height) == scrollViewMeasuredHeight) {
                        dz_jiazai.setVisibility(View.VISIBLE);
                        if (scrollState) {
                            scrollState = false;
                            new CountDownTimer(1000 * 1, 1) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    dz_jiazai.setText("正在加载...");
                                }

                                @Override
                                public void onFinish() {
                                    //   if (PAGE <= PAGE_NUM) {
                                    i++;
                                    indexNameCard();
//                                    } else {
//                                        dz_jiazai.setText("没有更多了");
//                                    }
                                }
                            }.start();
                        }
                    }

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

    private String Tag = "HomeFragment";

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
                && scrochildView.getMeasuredHeight() <= home_scroll.getScrollY()
                + home_scroll.getHeight() && scrollY != 0) {
            home_totop.setVisibility(View.VISIBLE);
            LogUtil.showILog(Tag, "bottom");
        }
        // 顶部判断
        else if (home_scroll.getScrollY() == 0) {
            home_totop.setVisibility(View.GONE);
            LogUtil.showILog(Tag, "top");
        } else if (home_scroll.getScrollY() > 130) {
            if (home_totop.getVisibility() == View.GONE) {
                home_totop.setVisibility(View.VISIBLE);
            }
            LogUtil.showILog(Tag, "test");
        }

    }

    private Fragment zuixinFragment;
    private Fragment tongchengFragment;
    private Fragment guanzhuFragment;
    private Fragment hongliFragment;
    // fragment管理者
    private FragmentManager fragmentManager;
    // 开启一个Fragment事务
    private FragmentTransaction transaction;

    public void gotoFragment(int tag) {
        home_rl.setVisibility(View.GONE);
        home_framelayout.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).removelist();
        fragmentManager = getChildFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if (tag == 3) {
            if (zuixinFragment == null) {
                zuixinFragment = new ZuixinFragment();
            }
            transaction.replace(R.id.home_framelayout, zuixinFragment);
            transaction.commitAllowingStateLoss();
            visandgone(home_zuixin, zuixin_line);
        } else if (tag == 4) {
            if (tongchengFragment == null) {
                tongchengFragment = new TongchengFragment();
            }
            transaction.replace(R.id.home_framelayout, tongchengFragment);
            transaction.commitAllowingStateLoss();
            visandgone(home_tongcheng, tongcheng_line);
        } else if (tag == 5) {
            if (MyData.USERID.equals("")) {
                dialogmess(getResources().getString(R.string.loginhint));
                return;
            }
            if (guanzhuFragment == null) {
                guanzhuFragment = new GuanzhuFragment();
            }
            transaction.replace(R.id.home_framelayout, guanzhuFragment);
            transaction.commitAllowingStateLoss();
            visandgone(home_guanzhu, guanzhu_line);
        }

    }

    private ArrayList<TextView> tvlist = new ArrayList<>();
    private ArrayList<TextView> tvllist = new ArrayList<>();

    private void visandgone(TextView tv, TextView tvl) {
        for (int i = 0; i < tvllist.size(); i++) {
            tvllist.remove(i).setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setTextColor(Color.parseColor("#000000"));
        }
        tv.setTextColor(Color.parseColor("#f93a6e"));
        tvl.setVisibility(View.VISIBLE);
        tvlist.add(tv);
        tvllist.add(tvl);
    }

    @Override
    public void onDestroyView() {
        //移除当前视图，防止重复加载相同视图使得程序闪退
        ((ViewGroup) contentView.getParent()).removeView(contentView);
        super.onDestroyView();
    }

    private boolean isone = true;

    @Override
    public void onResume() {
        super.onResume();
        int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        if (unreadNum > 0) {
            hongli_point.setVisibility(View.VISIBLE);
        } else {
            hongli_point.setVisibility(View.GONE);
        }
        if (isone) {
            mSwipeLayout.setRefreshing(true);
            onRefresh();
            getAdsListByPlace();
            isone = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        home_banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        home_banner.stopAutoPlay();
    }

    @Override
    public void onRefresh() {


        i = 1;
        adapter.delAll();
        indexNameCard();


    }

    /***
     * 首页直播名片列表
     */
    private ArrayList<LiveerMess> liveerlist ;
    private LiveerAdapter adapter;
    private int i = 1;
    private boolean isLoading = false;

    boolean scrollState = true;

    private void indexNameCard() {
        xhttp = new Xhttp(URLManager.indexNameCard);
        xhttp.add("place", "index");
        xhttp.add("page", i + "");
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {

                    scrollState = true;
                    dz_jiazai.setVisibility(View.GONE);
                    JSONObject obj = new JSONObject(result);
                    if (obj.getString("meg").equals("没有更多的数据了！")) {
                        return;
                    }
                    JSONArray ncList = obj.getJSONArray("ncList");
                    liveerlist = new ArrayList<>();
                    MainActivity.liveerlist = new ArrayList<>();
                    for (int i = 0; i < ncList.length(); i++) {
                        JSONObject nc = ncList.getJSONObject(i);
                        LiveerMess livver = new LiveerMess();
                        String status = nc.getString("status");
                        livver.setCid(nc.getString("cid"));
                        livver.setCity(nc.getString("city"));
                        livver.setHead(nc.getString("head"));
//                        livver.setBackgrimg("http://img.pconline.com.cn/images/upload/upc/tx/gamephotolib/1507/07/c3/9428961_1436258881176_medium.jpg");
                        livver.setName(nc.getString("nickname"));
                        livver.setSex(nc.getString("sex"));
                        livver.setId(nc.getString("uid"));
                        livver.setSign(status);
                        livver.setProfit(nc.getString("profit"));
                        livver.setPassword(nc.getString("passState"));
                        liveerlist.add(livver);
                        if (status.equals("1")) {
                            MainActivity.liveerlist.add(livver);
                        }
                    }


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 停止刷新
                            mSwipeLayout.setRefreshing(false);
                            adapter.setAndNotifyData(liveerlist);

                            // zuixin_listview.setAdapter(adapter);
                        }
                    }, 1000); // 5秒后发送消息，停止刷新
                } catch (JSONException e) {
                    e.printStackTrace();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 停止刷新
                            mSwipeLayout.setRefreshing(false);

                        }
                    }, 1000); // 5秒后发送消息，停止刷新
                }
            }

            @Override
            public void onError() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        mSwipeLayout.setRefreshing(false);

                    }
                }, 1000); // 2秒后发送消息，停止刷新
            }
        });
    }

    /***
     * 轮播图
     */
    private List<String> images;
    private List<String> links;

    private void getAdsListByPlace() {
        xhttp = new Xhttp(URLManager.getAdsListByPlace);
        xhttp.add("place", "index");
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONArray adsList = obj.getJSONArray("adsList");
                    images = new ArrayList<>();
                    links = new ArrayList<>();
                    for (int i = 0; i < adsList.length(); i++) {
                        JSONObject ads = adsList.getJSONObject(i);
                        String pic = URLManager.ads + ads.getString("picture");
                        String link = ads.getString("link");
                        images.add(pic);
                        links.add(link);
                    }
                    home_banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                    home_banner.setImageLoader(new PicassoImageLoader());
                    home_banner.setImages(images);//设置图片网址
                    home_banner.setOnBannerClickListener(new OnBannerClickListener() {//设置点击事件
                        @Override
                        public void OnBannerClick(int position) {
                            if (position - 1 >= 0) {
                                String link = links.get(position - 1);
                                if (!link.equals("") && !link.equals("null")) {
                                    ActivityJumpControl.getInstance(activity).gotoWebViewActivity(link);
                                }
                            }
                        }
                    });
                    if (images.size() > 1) {
                        home_banner.start();
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
                        MyData.GZZT = obj.getString("gzStatus");
                        MainActivity.position = pos;
                        MainActivity.roompath = rtmpPullUrl;
                        MainActivity.joinroomid = roomid;
                        MainActivity.ucid = ucid;
                        MyData.CID = cid;
                        MainActivity.liveername = liveer;
                        MainActivity.head = mhead;
                        MyData.LEVERPROFIT = profit;
                        ((MainActivity) context).requestBasicPermission(); // 申请APP基本权限
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

    private boolean hasjion = true;
    private int pos;
    private String ucid;
    private String liveer;
    private String mhead;


    private class LiveerAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater minflater;
        private ArrayList<LiveerMess> liveerList;

        public LiveerAdapter(Context context, ArrayList<LiveerMess> liveerList) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.liveerList = liveerList;
            this.context = context;
        }
        public LiveerAdapter(Context context) {
            this.context = context;
            this.minflater = LayoutInflater.from(context);
            this.liveerList = new ArrayList<>();

        }


        public void setAndNotifyData(ArrayList<LiveerMess> liveerList) {
            this.liveerList.addAll(liveerList);
            this.notifyDataSetChanged();
        }


        public void delAll() {
            this.liveerList.clear();
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
                convertView = minflater.inflate(R.layout.fragment_home_item, null);
                holder = new ViewHolder();
                holder.zhibo_rl = (RelativeLayout) convertView
                        .findViewById(R.id.zhibo_rl);
                holder.zhibo_img = (ImageView) convertView
                        .findViewById(R.id.zhibo_img);
                holder.zhibo_type = (TextView) convertView
                        .findViewById(R.id.zhibo_type);
                holder.zhibo_touxiang = (RoundedImageView) convertView
                        .findViewById(R.id.zhibo_touxiang);
                holder.zhibo_address = (TextView) convertView
                        .findViewById(R.id.zhibo_address);
                holder.zhibo_name = (TextView) convertView
                        .findViewById(R.id.zhibo_name);
                holder.zhibo_sex = (ImageView) convertView
                        .findViewById(R.id.zhibo_sex);
                holder.zhibo_sendmess = (ImageView) convertView
                        .findViewById(R.id.zhibo_sendmess);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LiveerMess liveerMess = liveerList.get(position);
            final String id = liveerMess.getId();
            final String status = liveerMess.getSign();
            final String profit = liveerMess.getProfit();
            final String password = liveerMess.getPassword();
            String head = liveerMess.getHead();
            String address = liveerMess.getCity();
            final String name = liveerMess.getName();
            String sex = liveerMess.getSex();
            final String cid = liveerMess.getCid();
            head = URLManager.head + head;
            Picasso.with(context).load(head).error(R.mipmap.backgrimg).into(holder.zhibo_img);
            Picasso.with(context).load(head).into(holder.zhibo_touxiang);
            if (status.equals("1")) {
                holder.zhibo_type.setText(context.getResources().getString(R.string.zhibozhong));
                final String finalHead = head;
                holder.zhibo_rl.setOnClickListener(new View.OnClickListener() {
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
                holder.zhibo_type.setText(context.getResources().getString(R.string.nobozhong));
                holder.zhibo_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogmess(getResources().getString(R.string.nolivehint));
                    }
                });
            }
            if (sex.equals("男")) {
                holder.zhibo_sex.setImageResource(R.mipmap.nanwhite);
            } else if (sex.equals("女")) {
                holder.zhibo_sex.setImageResource(R.mipmap.nvwhite);
            }

            holder.zhibo_address.setText(address);
            holder.zhibo_name.setText(name);
            holder.zhibo_sendmess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyData.IDENTITY.equals("")) {
                        ActivityJumpControl.getInstance(activity).gotoSixinzhuboActivity(id);
                    } else {
                        showMessage(getResources().getString(R.string.wszchint));
                    }
                }
            });
            holder.zhibo_touxiang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id, "2");
                }
            });
            return convertView;
        }

        class ViewHolder {
            private RoundedImageView zhibo_touxiang;
            private ImageView zhibo_sendmess, zhibo_img, zhibo_sex;
            private RelativeLayout zhibo_rl;
            private TextView zhibo_type, zhibo_name, zhibo_address;
        }

    }

    /**
     * dialog 消息提示
     */
    private void dialogmess(String mess) {
        dialog = new AlertDialog.Builder(context, R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_mess);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        TextView dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
        TextView dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
        dialog_mess_title.setText("消息提示");
        dialog_mess_con.setText(mess);

        TextView dialog_mess_quxiao = (TextView) dialog.findViewById(R.id.dialog_mess_quxiao);
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
        dialog = new AlertDialog.Builder(context, R.style.myDialog_style).create();
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
