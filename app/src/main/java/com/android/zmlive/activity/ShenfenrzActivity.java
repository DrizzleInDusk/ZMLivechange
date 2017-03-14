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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShenfenrzActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.shenfenrz_activity);
        findview();
        piclist =new ArrayList<>();
    }

    private LinearLayout sfrz_ll1;
    private TextView sfrz_zjlx;
    private ImageView addphoto1, addphoto2,addphoto3;
    private EditText sfrz_zsxmet,sfrz_zjhmet;
    private void findview() {
        findViewById(R.id.sfrz_back).setOnClickListener(this);
        findViewById(R.id.sfrz_renzheng).setOnClickListener(this);
        findViewById(R.id.sfrz_tijiao).setOnClickListener(this);
        findViewById(R.id.sfrz_zbxy).setOnClickListener(this);
        sfrz_ll1 = (LinearLayout) findViewById(R.id.sfrz_ll1);
        addphoto1 = (ImageView) findViewById(R.id.addphoto1);
        addphoto1.setOnClickListener(this);
        addphoto2 = (ImageView) findViewById(R.id.addphoto2);
        addphoto2.setOnClickListener(this);
        addphoto3 = (ImageView) findViewById(R.id.addphoto3);
        addphoto3.setOnClickListener(this);
        sfrz_zjlx = (TextView) findViewById(R.id.sfrz_zjlx);
        sfrz_zjlx.setOnClickListener(this);
        sfrz_zsxmet = (EditText) findViewById(R.id.sfrz_zsxmet);
        sfrz_zjhmet = (EditText) findViewById(R.id.sfrz_zjhmet);
        sfrz_zjhmet.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

    }

    /***
     * 点击事件
     */
    private boolean p1 = false, p2 = false,p3 = false;

    @Override
    public void onClick(View v) {
        showKeyboard(false);
        switch (v.getId()) {
            case R.id.addphoto1:
                cameraPermissions(activity);
                p1 = true;
                openphoto();
                break;
            case R.id.addphoto2:
                p2 = true;
                openphoto();
                break;
            case R.id.addphoto3:
                p3 = true;
                openphoto();
                break;
            case R.id.sfrz_tijiao:
                name=sfrz_zsxmet.getText().toString();
                code=sfrz_zjhmet.getText().toString();
                if (name.equals("")) {
                    showMessage(getResources().getString(R.string.qingshuru)+getResources().getString(R.string.zsxm));
                    return;
                }
                if (code.equals("")) {
                    showMessage(getResources().getString(R.string.qingshuru)+getResources().getString(R.string.zjhm));
                    return;
                }
                if (type.equals("")) {
                    showMessage(getResources().getString(R.string.qxzzjlx));
                    return;
                }
                if (!p3) {
                    showMessage(getResources().getString(R.string.sczphint));
                    return;
                }
                if (imgFile3==null) {
                    showMessage(getResources().getString(R.string.sczphint));
                    return;
                }
                if(code.getBytes().length != code.length()){
                    showMessage(getResources().getString(R.string.zjhm)+getResources().getString(R.string.er));
                    return;
                }
                auth();
                break;
            case R.id.sfrz_renzheng:
                sfrz_ll1.setVisibility(View.GONE);
                break;
            case R.id.sfrz_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.sfrz_zbxy://主播协议
                ActivityJumpControl.getInstance(activity).gotoZhuboxieyiActivity();
                break;
            case R.id.dialog_gander_nan://dialog_type 身份证
                dialog_gander_nan.setSelected(true);
                dialog_gander_nv.setSelected(false);
                type = "card";
                break;
            case R.id.dialog_gander_nv://dialog_type 护照
                dialog_gander_nan.setSelected(false);
                dialog_gander_nv.setSelected(true);
                type = "passport";
                break;
            case R.id.dialog_gander_quxiao://dialog_type 取消
                dialog.dismiss();
                type = "";
                break;
            case R.id.dialog_gander_yes://dialog_type 确定
                dialog.dismiss();
                if (!type.equals("")) {
                    if(type.equals("passport")){
                        sfrz_zjlx.setText("护照");
                    }else{
                        sfrz_zjlx.setText("身份证");
                    }
                }
                break;
            case R.id.sfrz_zjlx://选择证件类型
                 dialogtype();
                break;
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private AlertDialog dialog;
    /**
     * dialog 选择证件类型
     */
    private TextView dialog_gander_nan, dialog_gander_nv;

    private void dialogtype() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_gander);
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
        dialog_gander_nan = (TextView) dialog.findViewById(R.id.dialog_gander_nan);
        dialog_gander_nan.setText("身份证");
        dialog_gander_nan.setOnClickListener(this);
        dialog_gander_nv = (TextView) dialog.findViewById(R.id.dialog_gander_nv);
        dialog_gander_nv.setText("护照");
        dialog_gander_nv.setOnClickListener(this);
        dialog.findViewById(R.id.dialog_gander_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_gander_yes).setOnClickListener(this);
        dialog_gander_nan.performLongClick();
    }
    /***
     * 身份认证
     */
    private String code="", name="", type="";
    private List<File> piclist;
    private File imgFile1;
    private File imgFile2;
    private File imgFile3=null;

    private void auth() {
        showloading();
        xhttp = new Xhttp(URLManager.auth);
        xhttp.add("id", MyData.USERID);
        xhttp.add("code", code);
        xhttp.add("name", name);
        xhttp.add("type", type);
        xhttp.add("pic", imgFile1);//图片file集合
        xhttp.add("pic", imgFile2);//图片file集合
        xhttp.add("pic", imgFile3);//图片file集合
//        xhttp.add("pic", piclist, "image/jpeg");//图片file集合
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String mess = obj.getString("meg");
                    String status = obj.getString("status");
                    showMessage(mess);
                    if (status.equals("1")) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(piclist.size()>0){
            for(File file :piclist){
                Futile.deleteFile(file);
            }
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
        dialogWindow.setGravity(Gravity.CENTER);
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
            if (p1) {
                addphoto1.setImageBitmap(bmp);
                addphoto2.setVisibility(View.VISIBLE);
            }
            if (p2) {
                addphoto2.setImageBitmap(bmp);
                addphoto3.setVisibility(View.VISIBLE);
            }
            if (p3) {
                addphoto3.setImageBitmap(bmp);
                addphoto3.setEnabled(false);
            }

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
            piclist.add(imgFile);
            if (p1) {
                p1 = false;
                imgFile1=imgFile;
            }
            if (p2) {
                p2 = false;
                imgFile2=imgFile;
            }
            if (p3) {
                imgFile3=imgFile;
            }
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

}
