package com.android.zmlive.tool.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.zmlive.R;

/**
 * Created by Administrator on 2016/7/15.
 */
public class MyPopupWindow {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private String content;
    private boolean result;
    public MyPopupWindow(Context context, boolean result,String content){
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.result = result;
        this.content = content;
        initView();
    }
    /***/
    private View view;
    public void initView(){
        view = mLayoutInflater.inflate(R.layout.popup_tanchuang,null);
        ImageView iv = (ImageView) view.findViewById(R.id.duicuo);
        TextView tv = (TextView) view.findViewById(R.id.text);
        if (result){
            iv.setImageResource(R.mipmap.pop_ok);
        }else {
            iv.setImageResource(R.mipmap.pop_error);
        }
        tv.setText(content);
    }

    /**
     *
     * */
    PopupWindow popupWindow;
    public void show(){
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(new View(context), Gravity.CENTER,0,0);

    }
    public void dismiss(){
        popupWindow.dismiss();
    }
}
