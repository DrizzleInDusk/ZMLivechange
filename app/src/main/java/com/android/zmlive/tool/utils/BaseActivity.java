package com.android.zmlive.tool.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import com.android.zmlive.R;
import com.android.zmlive.tool.Xhttp;


/**
 * Created by Administrator on 2016/7/2.
 */
public abstract class BaseActivity extends FragmentActivity {
    private boolean destroyed = false;

    private static Handler handler;
    private static Handler mhandler= new Handler();;
    protected Context context;
    protected Xhttp xhttp;
    protected Activity activity;
    private Dialog dialog = null;
    private AlertDialog.Builder builder;
    private LayoutInflater mLayoutInflater;
    public static Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        context = this;
        activity = this;
        mLayoutInflater = LayoutInflater.from(this);
        init();
    }

    protected final void postDelayed(final Runnable runnable, long delay) {
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // run
                runnable.run();
            }
        }, delay);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 延时弹出键盘
     *
     * @param focus ：键盘的焦点项
     */
    protected void showKeyboardDelayed(View focus) {
        final View viewToFocus = focus;
        if (focus != null) {
            focus.requestFocus();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (viewToFocus == null || viewToFocus.isFocused()) {
                    showKeyboard(true);
                }
            }
        }, 200);
    }
    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }
    }
    /**
     * 判断页面是否已经被销毁（异步回调时使用）
     */
    protected boolean isDestroyedCompatible() {
        if (Build.VERSION.SDK_INT >= 17) {
            return isDestroyedCompatible17();
        } else {
            return destroyed || super.isFinishing();
        }
    }

    @TargetApi(17)
    private boolean isDestroyedCompatible17() {
        return super.isDestroyed();
    }

    protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getMainLooper());
        }
        return handler;
    }

    protected <T extends View> T findView(int resId) {
        return (T) (findViewById(resId));
    }


    /***/
    private void init() {
        builder = new AlertDialog.Builder(this, R.style.myDialog_style);
        View view = mLayoutInflater.inflate(R.layout.loading, null);
        builder.setView(view);
    }

    public void showloading() {
        if (dialog != null) {
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

    public void dismissloading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showMessage(String content) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
