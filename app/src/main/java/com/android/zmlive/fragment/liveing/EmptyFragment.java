package com.android.zmlive.fragment.liveing;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainLiveActivity;
import com.android.zmlive.tool.utils.BaseFragment;

/**
 * 该Fragment是用于MainDialogFragment中的pager，为了实现滑动隐藏交互Fragment的
 * 这个大家可以不用修改，但是根据各自的需求而定
 *
 */
public class EmptyFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);

        view.findViewById(R.id.liveer_tuichus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogmess();
            }
        });//退出
        return view;
    }

    private AlertDialog dialog;

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
        TextView dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
        dialog_mess_title.setText("消息提示！");
        dialog_mess_con.setText("是否要退出直播?");
        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overclose();
                dialog.dismiss();
            }
        });
    }

    private void overclose() {
        ((MainLiveActivity) context).livefinish();
    }
}