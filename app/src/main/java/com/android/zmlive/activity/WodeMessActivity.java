package com.android.zmlive.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.adapter.MyFragmentPagerAdapter;
import com.android.zmlive.fragment.MesssxFragment;
import com.android.zmlive.fragment.MessxtxxFragment;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.view.CustomViewPager;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;

import java.util.ArrayList;

public class WodeMessActivity extends UI implements View.OnClickListener {
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wodemess_activity);
        activity = this;
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        findview();
        initViewPager();
    }

    private RelativeLayout wdxx_xitongxxrl, wdxx_sixinrl;
    private TextView wdxx_xitongxx, wdxx_sixin;
    private TextView xitongxx_line, sixin_line, xitongxx_point, sixin_point;

    private void findview() {
        findViewById(R.id.wdxxback).setOnClickListener(this);
        wdxx_xitongxxrl = (RelativeLayout) findViewById(R.id.wdxx_xitongxxrl);
        wdxx_xitongxxrl.setOnClickListener(this);
        wdxx_sixinrl = (RelativeLayout) findViewById(R.id.wdxx_sixinrl);
        wdxx_sixinrl.setOnClickListener(this);
        wdxx_xitongxx = (TextView) findViewById(R.id.wdxx_xitongxx);
        xitongxx_line = (TextView) findViewById(R.id.xitongxx_line);
        xitongxx_point = (TextView) findViewById(R.id.xitongxx_point);

        wdxx_sixin = (TextView) findViewById(R.id.wdxx_sixin);
        sixin_line = (TextView) findViewById(R.id.sixin_line);
        sixin_point = (TextView) findViewById(R.id.sixin_point);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        if (unreadNum > 0) {
            sixin_point.setVisibility(View.VISIBLE);
        } else {
            sixin_point.setVisibility(View.GONE);
        }
    }

    /***
     * 点击事件
     */
    private boolean sixin = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wdxx_xitongxxrl:
                mPager.setCurrentItem(0);
                sixin = false;
                viewvis(wdxx_xitongxx, xitongxx_line);
                break;
            case R.id.wdxx_sixinrl:
                mPager.setCurrentItem(1);
                sixin = true;
                viewvis(wdxx_sixin, sixin_line);
                break;
            case R.id.wdxxback:
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private CustomViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private MessxtxxFragment xtxxFragment;
    private MesssxFragment sxFragment;
    int currIndex = 0; // 设置常量

    private void initViewPager() {
        mPager = (CustomViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<>();

        // 不同的Fragment传入的是不同的值，这个值用来在TestFragment类中的onCreatView()方法中根据这个
        // 传进来的int值返回不同的View
        xtxxFragment = new MessxtxxFragment();
        sxFragment = new MesssxFragment();
        fragmentsList.add(xtxxFragment);
        fragmentsList.add(sxFragment);

        // 设置ViewPager的适配器（自定义的继承自FragmentPagerAdapter的adapter）
        // 参数分别是FragmentManager和装载着Fragment的容器
        mPager.setAdapter(new MyFragmentPagerAdapter(
                getSupportFragmentManager(), fragmentsList));
        // 设置默认是第一页
        wdxx_xitongxxrl.performClick();
//        mPager.setCurrentItem(currIndex);
//        wdxx_xitongxxrl.performClick();
        // 设置ViewPager的页面切换监听事件
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());

    }

    // 这个是ViewPager的页面切换事件监听器
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {

            switch (arg0) {
                // 下面的意思是：拿第一个case 0举例，如果切换的是到case 0
                // 下面的if的意思是，如果现在是在序号为1的界面切换过来到序号为0的页面
                // 那么执行动画，并把一开始所在界面上的文字颜色设为轻亮，最后设置当前页面的文字为高亮
                // TranslateAnimation的四个参数代表的意思是：动画起始x,终结x，起始y,终结y位置
                case 0:
                    break;
                case 1:
                    break;
            }
            currIndex = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
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

}
