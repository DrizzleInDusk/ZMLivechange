package com.android.zmlive.tool.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.zmlive.R;
import com.android.zmlive.tool.Xhttp;

/**
 * Created by Administrator on 2016/7/2.
 */
public class BaseFragment extends Fragment {
    protected Context context;
    protected Xhttp xhttp;
    protected Activity activity;
    private Dialog dialog = null;
    private AlertDialog.Builder builder;
    private LayoutInflater mLayoutInflater;
    public static Toast mToast = null;
    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
        mLayoutInflater = LayoutInflater.from(getActivity());
    }
    private static Handler handler;
    protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(context.getMainLooper());
        }
        return handler;
    }
    /***/
    private void init() {
        builder = new AlertDialog.Builder(getActivity(),R.style.myDialog_style);
        View view = mLayoutInflater.inflate(R.layout.loading,null);
        builder.setView(view);
    }
    public void showloading(){
        if (dialog != null){
            dialog.dismiss();
        }
        init();
        dialog = builder.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);

        dialog.setCanceledOnTouchOutside(false);
    }
    public void dismissloading(){
        dialog.dismiss();
    }
    public void showMessage(String content){
        if (mToast !=null){
            mToast.cancel();
        }
        mToast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        mToast.show();
    }
}
