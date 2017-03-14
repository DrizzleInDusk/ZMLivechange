package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class XiugaiziliaoActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xiugaizl_activity);
        findview();
    }

    private TextView nicheng, xingbie, chengshi, shengao, nianling, tizhong, shangwei, shoujihao, qianming;
    private LinearLayout xgzl_head_ll;
    private RoundedImageView xgzl_head;

    private void findview() {
        findViewById(R.id.xgzl_back).setOnClickListener(this);
        findViewById(R.id.xgzl_head_ll).setOnClickListener(this);
        xgzl_head = (RoundedImageView) findViewById(R.id.xgzl_head);
        nicheng = (TextView) findViewById(R.id.xgzl_nicheng);
        nicheng.setOnClickListener(this);
        xingbie = (TextView) findViewById(R.id.xgzl_xingbie);
        xingbie.setOnClickListener(this);
        chengshi = (TextView) findViewById(R.id.xgzl_chengshi);
        chengshi.setOnClickListener(this);
        shengao = (TextView) findViewById(R.id.xgzl_shengao);
        shengao.setOnClickListener(this);
        nianling = (TextView) findViewById(R.id.xgzl_nianling);
        nianling.setOnClickListener(this);
        tizhong = (TextView) findViewById(R.id.xgzl_tizhong);
        tizhong.setOnClickListener(this);
        shangwei = (TextView) findViewById(R.id.xgzl_shangwei);
        shangwei.setOnClickListener(this);
        shoujihao = (TextView) findViewById(R.id.xgzl_shoujihao);
        shoujihao.setOnClickListener(this);
        qianming = (TextView) findViewById(R.id.xgzl_qianming);
        qianming.setOnClickListener(this);
        setusermess();
    }

    private void setusermess() {
        strqm="";
        Picasso.with(context).load((URLManager.head + MyData.HEAD)).into(xgzl_head);
        nicheng.setText(MyData.NICKNAME);
        xingbie.setText(MyData.SEX);
        chengshi.setText(MyData.CITY);
        shengao.setText(MyData.HEIGHT);
        nianling.setText(MyData.AGE);
        tizhong.setText(MyData.WEIGHT);
        shangwei.setText(MyData.BUST);
        shoujihao.setText(MyData.MEB);
        qianming.setText(MyData.SIGNATURE);
    }
    public static String strqm="";
    @Override
    protected void onResume() {
        super.onResume();
        if(!strqm.equals("")){
            qianming.setText(strqm);
        }

    }

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.xgzl_head_ll://修改头像
                cameraPermissions(activity);
                openphoto();
                break;
            case R.id.xgzl_nicheng://修改昵称
                dialogName("昵称");
                break;
            case R.id.xgzl_xingbie://修改性别
                dialogSex();
                break;
            case R.id.xgzl_chengshi://选择城市
                startActivityForResult(new Intent(this, AddAreaActivity.class), 10086);
                break;
            case R.id.xgzl_shengao://选择身高
                dialogName("身高");
                break;
            case R.id.xgzl_nianling://选择年龄
                dialogName("年龄");
                break;
            case R.id.xgzl_tizhong://选择体重
                dialogName("体重");
                break;
            case R.id.xgzl_shangwei://选择上围
                dialogshangwei();
                break;
            case R.id.xgzl_shoujihao://修改手机号
//                dialogName("手機號");
                break;
            case R.id.xgzl_qianming://修改签名
                ActivityJumpControl.getInstance(activity).gotoXiugaiqmActivity(MyData.SIGNATURE);
//                dialogName("签名");
                break;
            case R.id.xgzl_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.dialog_gander_nan://dialog_gander 男
                dialog_gander_nan.setSelected(true);
                dialog_gander_nv.setSelected(false);
                sex = "男";
                break;
            case R.id.dialog_gander_nv://dialog_gander 女
                dialog_gander_nan.setSelected(false);
                dialog_gander_nv.setSelected(true);
                sex = "女";
                break;
            case R.id.dialog_gander_quxiao://dialog_gander 取消
                dialog.dismiss();
                sex = "";
                break;
            case R.id.dialog_gander_yes://dialog_gander 确定
                dialog.dismiss();
                if (!sex.equals("")) {
                    updateUserInfo();
                    xingbie.setText(sex);
                }
                break;
            case R.id.dialog_cancel://dialog_name 取消
                dialog.dismiss();
                break;
            case R.id.dialog_confirm://dialog_name 确定
                String met = content.getText().toString().trim();
                if (met.equals("")) {
                    dialog.dismiss();
                    return;
                }
                switch (title) {
                    case "手机号":
                        break;
                    case "身高":
                        height = met;
                        break;
                    case "体重":
                        weight = met;
                        break;
                    case "年龄":
                        age = met;
                        break;
                    case "签名":
                        signature = met;
                        break;
                    case "昵称":
                        nickName = met;
                        break;
                }
                dialog.dismiss();
                updateUserInfo();
                break;

            case R.id.dialog_shangwei_b://dialogshangwei b
                bust = "B";
                tvsel(dialog_shangwei_b);
                break;
            case R.id.dialog_shangwei_c://dialogshangwei c
                bust = "C";
                tvsel(dialog_shangwei_c);
                break;
            case R.id.dialog_shangwei_d://dialogshangwei d
                bust = "D";
                tvsel(dialog_shangwei_d);
                break;
            case R.id.dialog_shangwei_e://dialogshangwei e
                bust = "E";
                tvsel(dialog_shangwei_e);
                break;
            case R.id.dialog_shangwei_f://dialogshangwei f
                bust = "F";
                tvsel(dialog_shangwei_f);
                break;
            case R.id.dialog_shangwei_g://dialogshangwei G
                bust = "G";
                tvsel(dialog_shangwei_g);
                break;
            case R.id.dialog_shangwei_quxiao://dialogshangwei 取消
                bust = "";
                dialog.dismiss();
                break;
            case R.id.dialog_shangwei_yes://dialogshangwei 确定
                shangwei.setText(bust);
                dialog.dismiss();
                updateUserInfo();
                break;
        }
    }

    private ArrayList<TextView> tvlist = new ArrayList<>();

    private void tvsel(TextView tv) {
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setSelected(false);
        }
        tv.setSelected(true);
        tvlist.add(tv);
    }

    private AlertDialog dialog;
    /**
     * dialog 选择性别
     */
    private TextView dialog_gander_nan, dialog_gander_nv;

    private void dialogSex() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_gander);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        dialog_gander_nan = (TextView) dialog.findViewById(R.id.dialog_gander_nan);
        dialog_gander_nan.setOnClickListener(this);
        dialog_gander_nv = (TextView) dialog.findViewById(R.id.dialog_gander_nv);
        dialog_gander_nv.setOnClickListener(this);
        dialog.findViewById(R.id.dialog_gander_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_gander_yes).setOnClickListener(this);
        dialog_gander_nan.performLongClick();
    }

    /**
     * dialog 选择上围
     */
    private TextView dialog_shangwei_b, dialog_shangwei_c, dialog_shangwei_d, dialog_shangwei_e, dialog_shangwei_f,dialog_shangwei_g;

    private void dialogshangwei() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_shangwei);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        dialog_shangwei_b = (TextView) dialog.findViewById(R.id.dialog_shangwei_b);
        dialog_shangwei_b.setOnClickListener(this);
        dialog_shangwei_c = (TextView) dialog.findViewById(R.id.dialog_shangwei_c);
        dialog_shangwei_c.setOnClickListener(this);
        dialog_shangwei_d = (TextView) dialog.findViewById(R.id.dialog_shangwei_d);
        dialog_shangwei_d.setOnClickListener(this);
        dialog_shangwei_e = (TextView) dialog.findViewById(R.id.dialog_shangwei_e);
        dialog_shangwei_e.setOnClickListener(this);
        dialog_shangwei_f = (TextView) dialog.findViewById(R.id.dialog_shangwei_f);
        dialog_shangwei_f.setOnClickListener(this);
        dialog_shangwei_g = (TextView) dialog.findViewById(R.id.dialog_shangwei_g);
        dialog_shangwei_g.setOnClickListener(this);
        dialog.findViewById(R.id.dialog_shangwei_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_shangwei_yes).setOnClickListener(this);
        dialog_shangwei_b.performLongClick();
    }

    /**
     * dialog 姓名
     */
    String title;
    EditText content;

    private void dialogName(String text) {
        title = text;
        TextView dialog_title;
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_name, null);
        dialog.setView(view);
        dialog.show();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        content = (EditText) view.findViewById(R.id.dialog_content);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        content.setHint("点此输入" + title);
        switch (title) {
            case "手机号":
                dialog_title.setText("修改" + title);
                content.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case "身高":
                dialog_title.setText("修改" + title + "(cm)");
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "体重":
                dialog_title.setText("修改" + title + "(kg)");
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "年龄":
                dialog_title.setText("修改" + title);
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "签名":
                dialog_title.setText("修改" + title);
                content.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "昵称":
                dialog_title.setText("修改" + title);
                content.setHint(MyData.NICKNAME);
                content.setMaxHeight(20);
                content.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                InputMethodManager inputMgr = (InputMethodManager) context
//                        .getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMgr.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
                showKeyboard(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showKeyboard(false);
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

    private static final int PHOTO_CARMERA = 10001;// 相机
    private static final int PHOTO_PICK = 10002;// 相册
    private static final int PHOTO_CUT = 10003;// 裁剪
    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/flowerlives/recordimg/";
    public static File destDir = new File(SDPATH);

    // 创建一个以当前系统时间为名称的文件，防止重复
    private File imgFile = null;

    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    private boolean issdkard() {
        // 首先判断sdcard是否插入
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            return true;
        } else {
            showMessage("请检查sd卡是否插入");
            return false;
        }
    }
    // CAMERA Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA};
    /**
     * grant permissions
     * @param activity
     */
    public static void cameraPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    /**
     * dialog相册
     */
    private void openphoto() {
        if (!issdkard()) {
            return;
        }
        imgFile = new File(destDir, getPhotoFileName());
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_photos);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);

        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        TextView rl1 = (TextView) dialog
                .findViewById(R.id.dialog_photo_paizhao);
        TextView rl2 = (TextView) dialog
                .findViewById(R.id.dialog_photo_xiangce);
        TextView rl3 = (TextView) dialog
                .findViewById(R.id.dialog_photo_quxiao);
        rl1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { // 拍照
                startCamera(dialog);
                dialog.dismiss();
            }
        });
        rl2.setOnClickListener(new View.OnClickListener() { // 相册

            @Override
            public void onClick(View v) {
                startPick(dialog);
                dialog.dismiss();
            }
        });
        rl3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Futile.deleteFile(imgFile);
                imgFile = null;
                dialog.dismiss();
            }
        });
    }

    protected void startCamera(DialogInterface dialog) {
        dialog.dismiss();
        // 调用系统的拍照功能
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("camerasensortype", 2); // 调用前置摄像头
        intent.putExtra("autofocus", true); // 自动对焦
        intent.putExtra("fullScreen", false); // 全屏
        intent.putExtra("showActionIcons", false);
        // 指定调用相机拍照后照片的存储路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
        startActivityForResult(intent, PHOTO_CARMERA);
    }

    // 调用系统相册
    protected void startPick(DialogInterface dialog) {
        dialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_PICK);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case 10086: {
                    try {
                        city = data.getStringExtra("address");
                        if (!city.equals("")) {
                            updateUserInfo();
                        }
                        chengshi.setText(city);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case PHOTO_CARMERA:
                    startPhotoZoom(Uri.fromFile(imgFile), 300);
                    break;
                case PHOTO_PICK:
                    if (null != data) {
                        startPhotoZoom(data.getData(), 300);
                    }
                    break;
                case PHOTO_CUT:
                    if (null != data) {
                        setPicToView(data);
                    }
                    break;
                default:
                    if (imgFile != null) {
                        Futile.deleteFile(imgFile);
                        imgFile = null;
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 调用系统裁剪
    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以裁剪
        intent.putExtra("crop", true);
        // aspectX,aspectY是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY是裁剪图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        // 设置是否返回数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CUT);
    }

    // 将裁剪后的图片显示在ImageView上
    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (null != bundle) {
            final Bitmap bmp = bundle.getParcelable("data");
            xgzl_head.setImageBitmap(bmp);

            saveCropPic(bmp);
        }
    }

    // 把裁剪后的图片保存到sdcard上
    private void saveCropPic(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fis = null;
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        try {
            fis = new FileOutputStream(imgFile);
            fis.write(baos.toByteArray());
            fis.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos) {
                    baos.close();
                }
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateUserInfo();
    }

    /***
     * 更新用户信息
     */
    private String sex = "", nickName = "", city = "", height = "", age = "", weight = "", bust = "", signature = "";
    private boolean nullvalue = false;

    private void updateUserInfo() {
        showloading();
        xhttp = new Xhttp(URLManager.updateUserInfo);
        xhttp.add("id", MyData.USERID);
        if (!sex.equals("")) {
            xhttp.add("sex", sex);
            nullvalue = true;
        }
        if (!nickName.equals("")) {
            xhttp.add("nickName", nickName);
            nullvalue = true;
        }
        if (!city.equals("")) {
            xhttp.add("city", city);
            nullvalue = true;
        }
        if (!height.equals("")) {
            xhttp.add("height", height);
            nullvalue = true;
        }
        if (!age.equals("")) {
            xhttp.add("age", age);
            nullvalue = true;
        }
        if (!weight.equals("")) {
            xhttp.add("weight", weight);
            nullvalue = true;
        }
        if (!bust.equals("")) {
            xhttp.add("bust", bust);
            nullvalue = true;
        }
        if (!signature.equals("")) {
            xhttp.add("signature", signature);
            nullvalue = true;
        }
        if (imgFile != null) {
            xhttp.add("head", imgFile);
            nullvalue = true;
        }
        if (!nullvalue) {
            return;
        }
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    showMessage(obj.getString("meg"));
                    if (!nickName.equals("")) {
                        nicheng.setText(nickName);
                    }
                    if (!height.equals("")) {
                        shengao.setText(height+"cm");
                    }
                    if (!age.equals("")) {
                        nianling.setText(age+"岁");
                    }
                    if (!weight.equals("")) {
                        tizhong.setText(weight+"kg");
                    }
                    if (!signature.equals("")) {
                        qianming.setText(signature);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    dismissloading();
                    deldata();
                }
            }

            @Override
            public void onError() {
                dismissloading();
                deldata();
            }
        });
    }

    private void deldata() {
        sex = "";
        nickName = "";
        city = "";
        height = "";
        age = "";
        weight = "";
        bust = "";
        signature = "";
        if (imgFile != null) {
            Futile.deleteFile(imgFile);
            imgFile = null;
        }
    }
}
