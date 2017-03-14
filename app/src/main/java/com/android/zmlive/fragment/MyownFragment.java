package com.android.zmlive.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainActivity;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseFragment;
import com.android.zmlive.tool.utils.MyDezuanPopupWindow;
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首页
 * Created by Kkan on 2016/7/6.
 */
public class MyownFragment extends BaseFragment implements View.OnClickListener {
    private View contentView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_myown, container, false);
            initView(contentView);
        }
        if (contentView != null) {
            return contentView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private RoundedImageView myown_head;
    private TextView myown_name, myown_id, guanzhunum, zannum, fansnum, myown_level,myown_wszc,myown_yqmbudeng;
    private ImageView myown_gander;
    private RelativeLayout myown_guanzhu, myown_dianzan, myown_fans;

    private void initView(View v) {
        v.findViewById(R.id.myownleft_go).setOnClickListener(this);
        v.findViewById(R.id.myownbianjiimg).setOnClickListener(this);
        myown_head = (RoundedImageView) v.findViewById(R.id.myown_head);//头像
        myown_head.setOnClickListener(this);
        myown_name = (TextView) v.findViewById(R.id.myown_name);//名字
        myown_gander = (ImageView) v.findViewById(R.id.myown_gander);//性别
        myown_level = (TextView) v.findViewById(R.id.myown_level);//等级
        myown_id = (TextView) v.findViewById(R.id.myown_id);//id
        guanzhunum = (TextView) v.findViewById(R.id.guanzhunum);//关注数量
        zannum = (TextView) v.findViewById(R.id.zannum);//点赞数量
        fansnum = (TextView) v.findViewById(R.id.fansnum);//粉丝数量
        v.findViewById(R.id.myown_guanzhu).setOnClickListener(this);
        v.findViewById(R.id.myown_dianzan).setOnClickListener(this);
        v.findViewById(R.id.myown_fans).setOnClickListener(this);
        v.findViewById(R.id.myown_qiandaosz).setOnClickListener(this);
        myown_wszc= (TextView) v.findViewById(R.id.myown_wszc);
        myown_wszc.setOnClickListener(this);
        v.findViewById(R.id.myown_bangzhufk).setOnClickListener(this);
        v.findViewById(R.id.myown_hezuozx).setOnClickListener(this);
        v.findViewById(R.id.myown_shipinzx).setOnClickListener(this);
        v.findViewById(R.id.myown_shenfenrz).setOnClickListener(this);
        myown_yqmbudeng= (TextView) v.findViewById(R.id.myown_yqmbudeng);
        myown_yqmbudeng.setOnClickListener(this);
        v.findViewById(R.id.myown_xiezhu).setOnClickListener(this);
        v.findViewById(R.id.myown_shezhi).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myownleft_go: {//返回主页
                ((MainActivity) activity).gotoFragment(1);
            }
            break;
            case R.id.myown_head: {//返回主页

            }
            break;
            case R.id.myownbianjiimg://编辑资料
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoXiugaiziliaoActivity();
                    break;
                }
            case R.id.myown_guanzhu://我的关注
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoGuanzhuActivity("concern");
                    break;
                }
            case R.id.myown_dianzan://我的点赞
                if (MyData.IDENTITY.equals("")) {
                    break;
                }
            case R.id.myown_fans://我的粉丝
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoGuanzhuActivity("fans");
                    break;
                }
            case R.id.myown_qiandaosz://签到送钻
                if (MyData.IDENTITY.equals("")) {
                        sign();
                    break;
                }
            case R.id.myown_bangzhufk://帮助反馈
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoBangzhufkActivity();
                    break;
                }
            case R.id.myown_hezuozx://合作中心
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoHezuozxActivity();
                    break;
                }
            case R.id.myown_shipinzx://视频中心
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoShipinzhongxinActivity();
                    break;
                }
            case R.id.myown_shenfenrz://身份认证
                if (MyData.IDENTITY.equals("")) {
                    jumpAuth();
                    break;
                }
            case R.id.myown_yqmbudeng://邀请码补登
                if (MyData.IDENTITY.equals("")) {
                    ActivityJumpControl.getInstance(activity).gotoYaoqingmabdActivity();
                    break;
                }
            case R.id.myown_xiezhu://协助
                if (MyData.IDENTITY.equals("")) {
                    assistList();
                    break;
                }
            case R.id.myown_wszc://完善注册
                ActivityJumpControl.getInstance(activity).gotoChooseLoginActivity();
                break;
            case R.id.myown_shezhi://设置
                ActivityJumpControl.getInstance(activity).gotoShezhiActivity();
                break;
        }
    }

    /***
     * 协助
     */
    private void assistList() {
        showloading();
        xhttp = new Xhttp(URLManager.assistList);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String type = obj.getString("type");
                    if (!type.equals("zb") && !type.equals("cg") && !type.equals("sh")) {
                        String mess = obj.getString("meg");
                        showMessage(mess);
                    } else {
                        ActivityJumpControl.getInstance(activity).gotoXiezhuActivity();
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
    /***
     * 每日签到
     */
    private MyDezuanPopupWindow dezuanPopupWindow;
    private void sign() {
        showloading();
        xhttp = new Xhttp(URLManager.sign);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissloading();
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    if (status.equals("1")) {
                        MyData.SIGN = "0";
                        dezuanPopupWindow=new MyDezuanPopupWindow(context,"获得1颗钻");
                        dezuanPopupWindow.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dezuanPopupWindow.dismiss();
                            }
                        }, 2000);
                    } else {
                        showMessage(mess);
                        MyData.SIGN = "0";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                dismissloading();
            }
        });
    }
    /***
     * 是否身份认证
     */
    private void jumpAuth() {
        showloading();
        xhttp = new Xhttp(URLManager.jumpAuth);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                dismissloading();
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    if(status.equals("0")){
                        showMessage(mess);
                    }else if(status.equals("1")){
                        String authStatus = obj.getString("authStatus");
                        if(authStatus.equals("0")){
                            ActivityJumpControl.getInstance(activity).gotoShenfenrzActivity();
                        }else if(authStatus.equals("1")){
                            showMessage(getResources().getString(R.string.sfrzhint1));
                        }else if(authStatus.equals("2")){
                            showMessage(getResources().getString(R.string.sfrzhint2));
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                dismissloading();
            }
        });
    }

    /***
     * 获取用户信息
     */
    private void userInfo() {
        showloading();
        xhttp = new Xhttp(URLManager.userInfo2);
        xhttp.add("uid",MyData.USERID);
        xhttp.add("gid",MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status=obj.getString("status");
                    String mess=obj.getString("meg");
                    JSONObject userInfo=obj.getJSONObject("userInfo");

                    MyData.HEAD=userInfo.getString("head");
                    MyData.NICKNAME=userInfo.getString("nickname");
                    MyData.CONCERN=userInfo.getString("concern");
                    MyData.PRAISE=userInfo.getString("praise");
                    MyData.FANS=userInfo.getString("fans");
                    MyData.LEVEL=userInfo.getString("level");
                    MyData.SEX=userInfo.getString("sex");
                    MyData.COLOUR = userInfo.getString("colour");
                    String city=userInfo.getString("city");
                    String signature=userInfo.getString("signature");
                    String height=userInfo.getString("height");
                    String age=userInfo.getString("age");
                    String weight=userInfo.getString("weight");
                    String bust="";
                    try {
                        bust=userInfo.getString("bust");
                    }catch(Exception e){
                        e.printStackTrace();
                        bust="";
                    }
                    if(city.equals("")){
                        city="--";
                    }
                    if(signature.equals("")){
                        signature="--";
                    }
                    if(height.equals("")){
                        height="--";
                    }else{
                        height=height+" cm";
                    }
                    if(age.equals("")){
                        age="--";
                    }else{
                        age=age+" 岁";
                    }
                    if(bust.equals("")){
                        bust="--";
                    }
                    if(weight.equals("")){
                        weight="--";
                    }else{
                        weight=weight+" kg";
                    }
                    MyData.CITY=city;
                    MyData.SIGNATURE=signature;
                    MyData.HEIGHT=height;
                    MyData.HEIGHT=height;
                    MyData.AGE=age;
                    MyData.BUST=bust;
                    MyData.WEIGHT=weight;
                    setusermess();
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

    private void setusermess() {
        if(MyData.COLOUR.equals("blue")){
            myown_level.setBackgroundResource(R.drawable.round3_lev1);
        }else if(MyData.COLOUR.equals("green")){
            myown_level.setBackgroundResource(R.drawable.round3_lev2);
        }else if(MyData.COLOUR.equals("yellow")){
            myown_level.setBackgroundResource(R.drawable.round3_lev3);
        }else if(MyData.COLOUR.equals("orange")){
            myown_level.setBackgroundResource(R.drawable.round3_lev4);
        }else if(MyData.COLOUR.equals("purple")){
            myown_level.setBackgroundResource(R.drawable.round3_lev5);
        }
        Picasso.with(context).load((URLManager.head + MyData.HEAD)).into(myown_head);
        myown_name.setText(MyData.NICKNAME);
        myown_id.setText("ID " + MyData.USERID);
        guanzhunum.setText(MyData.CONCERN);
        zannum.setText(MyData.PRAISE);
        fansnum.setText(MyData.FANS);
        myown_level.setText(MyData.LEVEL);
        if (MyData.SEX.equals("男")) {
            myown_gander.setImageResource(R.mipmap.nanimg);
        } else {
            myown_gander.setImageResource(R.mipmap.nvimg);
        }
    }
    @Override
    public void onResume() {
        if(!MyData.USERID.equals("0")){
            myown_wszc.setVisibility(View.GONE);
            userInfo();
        }else{
            myown_wszc.setVisibility(View.VISIBLE);
            setusermess();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        //移除当前视图，防止重复加载相同视图使得程序闪退
        ((ViewGroup) contentView.getParent()).removeView(contentView);
        super.onDestroyView();
    }

}
