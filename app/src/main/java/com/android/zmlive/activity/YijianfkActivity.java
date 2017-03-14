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
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YijianfkActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.yijianfk_activity);
        findview();
    }

    private EditText yjfk_content, yjfk_name, yjfk_tel;
    private ImageView yjfk_img;

    private void findview() {
        findViewById(R.id.yjfkback).setOnClickListener(this);
        yjfk_content = (EditText) findViewById(R.id.yjfk_content);
        yjfk_img = (ImageView) findViewById(R.id.yjfk_img);
        yjfk_img.setOnClickListener(this);
        yjfk_name = (EditText) findViewById(R.id.yjfk_name);
        yjfk_tel = (EditText) findViewById(R.id.yjfk_tel);
        findViewById(R.id.yjfk_tijiao).setOnClickListener(this);

    }
    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.yjfk_tijiao:
                String con = yjfk_content.getText().toString().trim();
                String name = yjfk_name.getText().toString().trim();
                String tel = yjfk_tel.getText().toString().trim();
                if (con.equals("")) {
                    showMessage(getResources().getString(R.string.yjfk) + getResources().getString(R.string.yjfkhint2));
                    return;
                }
                if (name.equals("")) {
                    showMessage(getResources().getString(R.string.xm) + getResources().getString(R.string.yjfkhint2));
                    return;
                }
                if (tel.equals("")) {
                    showMessage(getResources().getString(R.string.lxfs) + getResources().getString(R.string.yjfkhint2));
                    return;
                }
                feedback(name, con, tel);
                break;
            case R.id.yjfk_img:
                cameraPermissions(activity);
                openphoto();
                break;
            case R.id.yjfkback:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
        showKeyboard(false);
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
private AlertDialog dialog;
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
            yjfk_img.setImageBitmap(bmp);

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
    }

    /***
     * 建议反馈
     */
    private void feedback(String name, String content, String contact) {
        showloading();
        xhttp = new Xhttp(URLManager.feedback);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("name", name);//姓名
        xhttp.add("content", content);//内容
        xhttp.add("contact", contact);//联系方式
        if (imgFile != null) {
            xhttp.add("pic", imgFile);
        }
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String mess = obj.getString("meg");
                    String status = obj.getString("status");
                    showMessage(mess);
                    if (status.equals("1")) {
                        if (imgFile != null) {
                            Futile.deleteFile(imgFile);
                        }
                        finish();
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

}
