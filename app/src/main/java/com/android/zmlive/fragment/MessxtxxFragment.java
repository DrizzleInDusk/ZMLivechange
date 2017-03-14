package com.android.zmlive.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseFragment;
import com.android.zmlive.tool.utils.TimeUtils;
import com.android.zmlive.view.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统消息
 * Created by Kkan on 2016/7/6.
 */
public class MessxtxxFragment extends BaseFragment implements View.OnClickListener {
    private View contentView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_xitongxx, container, false);
            initView(contentView);
            megList();
        }
        if (contentView != null) {
            return contentView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private ListView xtxx_listview;
//    private View footerview;
//    private TextView list_footer;
//    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;
//    private int lastItem;
    private void initView(View v) {
//        footerview = LayoutInflater.from(context).inflate(R.layout.list_footer_item, null);
//        list_footer = (TextView) footerview.findViewById(R.id.list_footer);
        xtxx_listview = (ListView) v.findViewById(R.id.xtxx_listview);
//        xtxx_listview.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    //滚动到底部
//                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
//                        View v = (View) view.getChildAt(view.getChildCount() - 1);
//                        int[] location = new int[2];
//                        v.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
//                        int y = location[1];
//                        if (view.getLastVisiblePosition() != getLastVisiblePosition)//第一次拖至底部
////                                && lastVisiblePositionY != y)//第一次拖至底部
//                        {
//                            try {
//                                xtxx_listview.removeFooterView(footerview);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            list_footer.setText(getResources().getString(R.string.listlodmore));
//                            xtxx_listview.addFooterView(footerview);
//                            getLastVisiblePosition = view.getLastVisiblePosition() + 1;
//                            lastVisiblePositionY = y + footerview.getHeight();
//                            return;
//                        } else if (view.getLastVisiblePosition() == getLastVisiblePosition)//第二次拖至底部
////                                && lastVisiblePositionY == y)//第二次拖至底部
//                        {
////                            billList2();
//                            list_footer.setText(getResources().getString(R.string.listfooter));
//                        }
//                    }
//                    //未滚动到底部，第二次拖至底部都初始化
//                    getLastVisiblePosition = 0;
//                    lastVisiblePositionY = 0;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                lastItem = firstVisibleItem + visibleItemCount - 1;
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        //移除当前视图，防止重复加载相同视图使得程序闪退
        ((ViewGroup) contentView.getParent()).removeView(contentView);
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private AlertDialog dialog;

    private void dialoxtxx(String title,String content) {
        dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_xtxx, null);
        dialog.setView(view);
        dialog.show();
        TextView dialog_content = (TextView) view.findViewById(R.id.dialog_content);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        TextView dialog_cancel = (TextView) view.findViewById(R.id.dialog_cancel);
        dialog_cancel.setVisibility(View.GONE);
        dialog_title.setText("来自"+title+"的消息");
        dialog_content.setText(content);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /***
     * 系统消息
     */
    private XtxxAdapter adapter;
    private Map<String, ArrayList<String>> messmap;

    private void megList() {
        xtxx_listview.setVisibility(View.GONE);
        xhttp = new Xhttp(URLManager.megList);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONArray ncList = obj.getJSONArray("pushList");
                    messmap = new LinkedHashMap<>();
                    for (int i = 0; i < ncList.length(); i++) {
                        JSONObject nc = ncList.getJSONObject(i);
                        String time = TimeUtils.datetoDate(nc.getString("time"), "yyyy-MM-dd HH:mm:ss", "yyyy.MM.dd HH:mm");
                        ArrayList<String> mlist = messmap.get(time);
                        if (mlist == null) {
                            mlist = new ArrayList<>();
                            messmap.put(time, mlist);
                        }
                        mlist.add(nc.getString("content"));
                    }

                    xtxx_listview.setVisibility(View.VISIBLE);
                    adapter = new XtxxAdapter(context, messmap);
                    xtxx_listview.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });
    }


    private class XtxxAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater minflater;
        private Map<String, ArrayList<String>> messmap;

        public XtxxAdapter(Context context, Map<String, ArrayList<String>> messmap) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.messmap = messmap;
            this.context = context;
        }

        @Override
        public int getCount() {
            return messmap.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            String title = null;
            ArrayList<String> messist = null;
            for (String key : messmap.keySet()) {
                messist = messmap.get(key);
                if (position == 0) {
                    title = key;
                    break;
                }
                position--;
            }
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.fragment_xitongxx_item, null);
                holder = new ViewHolder();
                holder.xtxxitem_listview = (ListView) convertView
                        .findViewById(R.id.xtxxitem_listview);
                holder.xtxx_time = (TextView) convertView
                        .findViewById(R.id.xtxx_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.xtxx_time.setText(title);
            holder.xtxxitem_listview.setAdapter(new Xtxx1Adapter(context, messist));
            return convertView;
        }

        class ViewHolder {
            private ListView xtxxitem_listview;
            private TextView xtxx_time;
        }

    }

    private class Xtxx1Adapter extends BaseAdapter {
        private Context context;
        private LayoutInflater minflater;
        private ArrayList<String> pushmesslist;

        public Xtxx1Adapter(Context context, ArrayList<String> pushmesslist) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.pushmesslist = pushmesslist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return pushmesslist.size();
        }

        @Override
        public Object getItem(int position) {
            return pushmesslist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.fragment_xitongxx_item1, null);
                holder = new ViewHolder();
                holder.xtxx_touxiang = (RoundedImageView) convertView
                        .findViewById(R.id.xtxx_touxiang);
                holder.xtxx_content = (TextView) convertView
                        .findViewById(R.id.xtxx_content);
                holder.xtxx_itemll = (LinearLayout) convertView
                        .findViewById(R.id.xtxx_itemll);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final String content = pushmesslist.get(position);
            holder.xtxx_content.setText(content);
            holder.xtxx_itemll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialoxtxx("系统",content);
                }
            });
            return convertView;
        }

        class ViewHolder {
            private RoundedImageView xtxx_touxiang;
            private LinearLayout xtxx_itemll;
            private TextView xtxx_content;
        }

    }
}
