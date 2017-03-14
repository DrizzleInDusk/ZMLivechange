package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.UserMes;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class XiezhuActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.xiezhu_activity);
        findview();
        assistList();
        from = Futile.getValue(context, "account");
        verifyPHONEPermissions(activity);
    }

    private TextView xz_shouhu, xz_shouhu_tv, xz_changguan, xz_changguan_tv, tianjiashouhu, xz_zwsj;
    private ListView xz_listview;
    private TextView xz_et, xz_gl;
    private ImageView xz_deleteet;
    private LinearLayout xz_ll, xz_sousuo;

    private void findview() {
        findViewById(R.id.xz_back).setOnClickListener(this);
        xz_listview = (ListView) findViewById(R.id.xz_listview);
        xz_shouhu_tv = (TextView) findViewById(R.id.xz_shouhu_tv);
        xz_gl = (TextView) findViewById(R.id.xz_gl);
        xz_gl.setOnClickListener(this);
        xz_gl.setVisibility(View.GONE);
        xz_shouhu = (TextView) findViewById(R.id.xz_shouhu);
        xz_shouhu.setOnClickListener(this);
        xz_changguan_tv = (TextView) findViewById(R.id.xz_changguan_tv);
        xz_changguan = (TextView) findViewById(R.id.xz_changguan);
        xz_changguan.setOnClickListener(this);
        tianjiashouhu = (TextView) findViewById(R.id.tianjiashouhu);
        tianjiashouhu.setOnClickListener(this);
        xz_zwsj = (TextView) findViewById(R.id.xz_zwsj);
        xz_ll = (LinearLayout) findViewById(R.id.xz_ll);
        xz_sousuo = (LinearLayout) findViewById(R.id.xz_sousuo);
        xz_deleteet = (ImageView) findViewById(R.id.xz_deleteet);
        xz_deleteet.setOnClickListener(this);
        xz_et = (TextView) findViewById(R.id.xz_et);
        xz_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cgss = s.toString();
                if (cgss.length() > 0) {
                    xz_deleteet.setVisibility(View.VISIBLE);
                } else {
                    xz_deleteet.setVisibility(View.INVISIBLE);
                }
            }
        });

        xz_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //do something;
                    if (cgss.length() > 0) {
                        searchUserByCG(cgss);
                        showKeyboard(false);
                    } else {
                    }
                }
                return false;
            }
        });
    }

    private String cgss = "";

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xz_shouhu://守护
                xz_shouhu.setTextColor(Color.parseColor("#9854CF"));
                xz_changguan.setTextColor(Color.parseColor("#000000"));
                xz_shouhu_tv.setVisibility(View.VISIBLE);
                xz_changguan_tv.setVisibility(View.GONE);
                setenable(true);
                if (type.equals("sh")) {
                    if (zblist != null && zblist.size() > 0) {
                        xz_listview.setVisibility(View.VISIBLE);
                        xz_zwsj.setVisibility(View.GONE);
                        adapter = new XiezhuAdapter(context, zblist, type);
                        xz_listview.setAdapter(adapter);
                    } else {
                        zwsj("");
                    }
                } else if (type.equals("zb")) {
                    tianjiashouhu.setVisibility(View.VISIBLE);
                    if (shlist != null && shlist.size() > 0) {
                        xz_listview.setVisibility(View.VISIBLE);
                        xz_zwsj.setVisibility(View.GONE);
                        adapter = new XiezhuAdapter(context, shlist, type);
                        xz_listview.setAdapter(adapter);
                    } else {
                        zwsj("");
                    }
                }
                break;
            case R.id.xz_changguan://场管
                xz_shouhu.setTextColor(Color.parseColor("#000000"));
                xz_changguan.setTextColor(Color.parseColor("#9854CF"));
                xz_shouhu_tv.setVisibility(View.GONE);
                tianjiashouhu.setVisibility(View.GONE);
                xz_changguan_tv.setVisibility(View.VISIBLE);
                setenable(false);
                if (cglist != null && cglist.size() > 0) {
                    xz_listview.setVisibility(View.VISIBLE);
                    xz_zwsj.setVisibility(View.GONE);
                    adapter = new XiezhuAdapter(context, cglist, "cg");
                    xz_listview.setAdapter(adapter);
                } else {
                    zwsj("");
                }
                break;
            case R.id.tianjiashouhu://添加守护
                ActivityJumpControl.getInstance(activity).gotoTianjiashActivity();
                break;
            case R.id.xz_deleteet://清空输入框
                xz_et.setText("");
                break;
            case R.id.xz_gl://管理
                if (!type.equals("zb")) {
                    if (type.equals("cg")) {
                        ActivityJumpControl.getInstance(activity).gotoXiezhuzhiboActivity();
                    } else if (type.equals("sh")) {
                        ActivityJumpControl.getInstance(activity).gotoXiezhuglActivity();
                    }
                }
                break;
            case R.id.xz_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
            case R.id.dialog_cancel:
                toyxid = "";
                dialog.dismiss();
                break;
            case R.id.dialog_confirm:
                String content = dialog_content.getText().toString().trim();
                if (content.equals("")) {
                    showMessage("请输入消息内容！");
                    return;
                }
                mmsendmessage(toyxid, content);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    private void setenable(boolean boo) {
        xz_shouhu.setEnabled(!boo);
        xz_changguan.setEnabled(boo);
    }

    private AlertDialog dialog;
    private EditText dialog_content;
    private String toyxid = "", from = "";

    private void dialogsendmess(String name, String code, String mob) {
        toyxid = code + mob;
        toyxid = toyxid.toLowerCase();
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_xzsendmess, null);
        dialog.setView(view);
        dialog.show();
        TextView dialog_toname = (TextView) view.findViewById(R.id.dialog_toname);
        dialog_toname.setText("向用户" + name + "发送消息：");
        dialog_content = (EditText) view.findViewById(R.id.dialog_content);
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showKeyboard(false);
            }
        });
    }

    /***
     * 协助
     */
    private ArrayList<UserMes> cglist;
    private ArrayList<UserMes> shlist;
    private ArrayList<UserMes> zblist;
    private String type;
    private XiezhuAdapter adapter;

    private void assistList() {
        cglist = null;
        shlist = null;
        zblist = null;
        xz_listview.setVisibility(View.GONE);
        showloading();
        xhttp = new Xhttp(URLManager.assistList);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    type = obj.getString("type");
                    if (type.equals("zb")) {
                        xz_gl.setVisibility(View.GONE);
                        tianjiashouhu.setVisibility(View.VISIBLE);
                        try {
                            JSONArray cgList = obj.getJSONArray("cgList");
                            cglist = new ArrayList<>();
                            for (int i = 0; i < cgList.length(); i++) {
                                JSONObject user = cgList.getJSONObject(i);
                                UserMes userMes = new UserMes();
                                userMes.setHead(user.getString("head"));
                                userMes.setId(user.getString("id"));
                                userMes.setMobile(user.getString("mobile"));
                                userMes.setCode(user.getString("code"));
                                userMes.setNickname(user.getString("nickName"));
                                cglist.add(userMes);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray shList = obj.getJSONArray("shList");
                            shlist = new ArrayList<>();
                            for (int i = 0; i < shList.length(); i++) {
                                JSONObject user = shList.getJSONObject(i);
                                UserMes userMes = new UserMes();
                                userMes.setHead(user.getString("head"));
                                userMes.setId(user.getString("id"));
                                userMes.setMobile(user.getString("mobile"));
                                userMes.setCode(user.getString("code"));
                                userMes.setNickname(user.getString("nickName"));
                                shlist.add(userMes);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        onpick(getResources().getString(R.string.shouhu));
                    } else if (type.equals("sh")) {
                        xz_gl.setVisibility(View.VISIBLE);
                        tianjiashouhu.setVisibility(View.GONE);
                        try {
                            JSONArray cgList = obj.getJSONArray("cgList");
                            cglist = new ArrayList<>();
                            for (int i = 0; i < cgList.length(); i++) {
                                JSONObject user = cgList.getJSONObject(i);
                                UserMes userMes = new UserMes();
                                userMes.setHead(user.getString("head"));
                                userMes.setId(user.getString("id"));
                                userMes.setMobile(user.getString("mobile"));
                                userMes.setCode(user.getString("code"));
                                userMes.setNickname(user.getString("nickName"));
                                cglist.add(userMes);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray zbList = obj.getJSONArray("zbList");
                            zblist = new ArrayList<>();
                            for (int i = 0; i < zbList.length(); i++) {
                                JSONObject user = zbList.getJSONObject(i);
                                UserMes userMes = new UserMes();
                                userMes.setHead(user.getString("head"));
                                userMes.setId(user.getString("id"));
                                userMes.setMobile(user.getString("mobile"));
                                userMes.setCode(user.getString("code"));
                                userMes.setNickname(user.getString("nickName"));
                                zblist.add(userMes);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        onpick(getResources().getString(R.string.zhubo));
                    } else {
                        isgone();
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
     * 协助-场管-搜索
     */
    private ArrayList<UserMes> usermeslist;

    private void searchUserByCG(String keyword) {
        showloading();
        showKeyboard(false);
        xhttp = new Xhttp(URLManager.searchUserByCG);
        xhttp.add("keyword", keyword);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    try {
                        JSONArray userList = obj.getJSONArray("userList");
                        usermeslist = new ArrayList<>();
                        for (int i = 0; i < userList.length(); i++) {
                            JSONObject gg = userList.getJSONObject(i);
                            UserMes usetmes = new UserMes();
                            usetmes.setHead(gg.getString("head"));
                            usetmes.setId(gg.getString("id"));
                            usetmes.setMobile(gg.getString("mobile"));
                            usetmes.setNickname(gg.getString("nickName"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new XiezhuAdapter(context, usermeslist, "");
                        xz_listview.setVisibility(View.VISIBLE);
                        xz_zwsj.setVisibility(View.GONE);
                        xz_listview.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        zwsj(obj.getString("meg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    zwsj("");
                }
                dismissloading();
            }

            @Override
            public void onError() {
                zwsj("");
                dismissloading();
            }
        });
    }

    /***
     * 协助里发送消息
     */

    private void mmsendmessage(String to, String content) {
        showloading();
        showKeyboard(false);
        xhttp = new Xhttp(URLManager.mmsendmessage);
        xhttp.add("from", from);
        xhttp.add("to", to);
        xhttp.add("content", content);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String mess = obj.getString("meg");
                    showMessage(mess);
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

    private void onpick(String xzsh) {
        xz_ll.setVisibility(View.VISIBLE);
        xz_shouhu.setText(xzsh);
        xz_shouhu.performClick();
    }

    private void isgone() {
        xz_gl.setVisibility(View.VISIBLE);
        tianjiashouhu.setVisibility(View.GONE);
        xz_ll.setVisibility(View.GONE);
        xz_sousuo.setVisibility(View.VISIBLE);
    }

    private void zwsj(String title) {
        xz_zwsj.setVisibility(View.VISIBLE);
        xz_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        xz_zwsj.setText(title);
    }


    private class XiezhuAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private String Tag;
        private LayoutInflater minflater;

        public XiezhuAdapter(Context context, ArrayList<UserMes> usermeslist, String Tag) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.usermeslist = usermeslist;
            this.context = context;
            this.Tag = Tag;
        }

        @Override
        public int getCount() {
            return usermeslist.size();
        }

        @Override
        public Object getItem(int position) {
            return usermeslist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.xiezhu_activity_item, null);
                holder = new ViewHolder();
                holder.xz_touxiang = (RoundedImageView) convertView
                        .findViewById(R.id.xz_touxiang);
                holder.sz_name = (TextView) convertView
                        .findViewById(R.id.sz_name);
                holder.xz_emails = (ImageView) convertView
                        .findViewById(R.id.xz_emails);
                holder.xz_tel = (ImageView) convertView
                        .findViewById(R.id.xz_tel);
                holder.xz_ll = (LinearLayout) convertView
                        .findViewById(R.id.xz_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

            String head = usermes.getHead();
            final String id = usermes.getId();
            final String code = usermes.getCode();
            final String mobile = usermes.getMobile();
            final String nickName = usermes.getNickname();
            Picasso.with(context).load(URLManager.head + head).error(R.mipmap.fw658).into(holder.xz_touxiang);
            holder.sz_name.setText(nickName);
            holder.xz_emails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogsendmess(nickName, code, mobile);
                }
            });
            holder.xz_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id);
                }
            });
            if (Tag.equals("cg")) {
                holder.xz_tel.setVisibility(View.GONE);
            } else {
                holder.xz_tel.setVisibility(View.VISIBLE);
                holder.xz_tel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + mobile);
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
            }
            return convertView;
        }

        class ViewHolder {
            private TextView sz_name;
            private LinearLayout xz_ll;
            private ImageView xz_emails, xz_tel;
            private RoundedImageView xz_touxiang;
        }
    }

    // CAMERA Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CALL_PHONE};

    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     *
     * @param activity
     */
    public static void verifyPHONEPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CALL_PHONE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
