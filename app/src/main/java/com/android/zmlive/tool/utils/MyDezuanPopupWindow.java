package com.android.zmlive.tool.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.zmlive.R;

/**
 * Created by Administrator on 2016/7/15.
 */
public class MyDezuanPopupWindow {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private String content;
    public MyDezuanPopupWindow(Context context,String content){
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.content = content;
        initView();
    }
    /***/
    private View view;
    public void initView(){
        view = mLayoutInflater.inflate(R.layout.popup_dezuan,null);
        TextView tv = (TextView) view.findViewById(R.id.text);
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
