package com.android.zmlive.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.UserMes;
import com.android.zmlive.tool.ActivityJumpControl;
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

public class TianjiashActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        setContentView(R.layout.tianjiash_activity);
        findview();
    }

    private TextView tjsh_et;
    private TextView tjsh_zwsj;
    private ImageView tjsh_deleteet;
    private ListView tjsh_listview;

    private void findview() {
        findViewById(R.id.tjsh_back).setOnClickListener(this);
        tjsh_zwsj = (TextView) findViewById(R.id.tjsh_zwsj);
        tjsh_listview = (ListView) findViewById(R.id.tjsh_listview);
        tjsh_deleteet = (ImageView) findViewById(R.id.tjsh_deleteet);
        tjsh_deleteet.setOnClickListener(this);
        tjsh_et = (TextView) findViewById(R.id.tjsh_et);
        tjsh_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tjsh = s.toString();
                if (tjsh.length() > 0) {
                    tjsh_deleteet.setVisibility(View.VISIBLE);
                } else {
                    tjsh_deleteet.setVisibility(View.INVISIBLE);
                }
            }
        });
        tjsh_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //do something;
                    if (tjsh.length() > 0) {
                        searchUserForApp(tjsh);
                        showKeyboard(false);
                    } else {
                    }
                }
                return false;
            }
        });

    }

    private String tjsh = "";

    /***
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tjsh_deleteet://清空输入框
                tjsh_et.setText("");
                break;
            case R.id.tjsh_back:
                ActivityJumpControl.getInstance(activity).popActivity(activity);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }
    /***
     * 添加守护--搜索
     */

    private TianjiashAdapter adapter;
    private ArrayList<UserMes> usermeslist;

    private void searchUserForApp(String keyword) {
        showloading();
        showKeyboard(false);
        xhttp = new Xhttp(URLManager.searchUserForApp);
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
                            usetmes.setNickname(gg.getString("nickName"));
                            usermeslist.add(usetmes);
                        }
                        adapter = new TianjiashAdapter(context, usermeslist);
                        tjsh_listview.setVisibility(View.VISIBLE);
                        tjsh_zwsj.setVisibility(View.GONE);
                        tjsh_listview.setAdapter(adapter);
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

    private void zwsj(String title) {
        tjsh_zwsj.setVisibility(View.VISIBLE);
        tjsh_listview.setVisibility(View.GONE);
        if (title.equals("")) {
            title = getResources().getString(R.string.zwsj);
        }
        tjsh_zwsj.setText(title);
    }

    private class TianjiashAdapter extends BaseAdapter {
        private ArrayList<UserMes> usermeslist;
        private Context context;
        private LayoutInflater minflater;

        public TianjiashAdapter(Context context, ArrayList<UserMes> usermeslist) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.usermeslist = usermeslist;
            this.context = context;
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
                convertView = minflater.inflate(R.layout.tianjiash_activity_item, null);
                holder = new ViewHolder();
                holder.tjsh_head = (RoundedImageView) convertView
                        .findViewById(R.id.tjsh_head);
                holder.tjsh_name = (TextView) convertView
                        .findViewById(R.id.tjsh_name);
                holder.tjsh_sh = (TextView) convertView
                        .findViewById(R.id.tjsh_sh);
                holder.tjsh_ll = (LinearLayout) convertView
                        .findViewById(R.id.tjsh_ll);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            UserMes usermes = usermeslist.get(position);

            final String id = usermes.getId();
            String head = usermes.getHead();
            String nickname = usermes.getNickname();

            holder.tjsh_name.setText(nickname);
            Picasso.with(context).load(URLManager.head + head).error(R.mipmap.fw658).into(holder.tjsh_head);
            holder.tjsh_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ActivityJumpControl.getInstance(activity).gotoUserMesActivity(id);
                }
            });
            holder.tjsh_sh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyGuard(id);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private LinearLayout tjsh_ll;
            private TextView tjsh_name, tjsh_sh;
            private RoundedImageView tjsh_head;
        }
    }

    /***
     * 主播设置守护
     */

    private void applyGuard(String gid) {
        showloading();
        showKeyboard(false);
        xhttp = new Xhttp(URLManager.applyGuard);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("gid", gid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    showMessage(obj.getString("meg"));
                    if(status.equals("1")){
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
