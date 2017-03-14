package com.android.zmlive.fragment.liveing;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.entity.PeopleComeBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里是聊天列表的数据适配器，比如大家使用的是环信或者第三方直播的聊天室功能，都会用的listview，
 * 对于聊天列表里面的交互以及显示方式，大家都可以在这里修改，以及布局文件
 */
public class MessageAdapter extends BaseAdapter {

    private Context mContext;
    private ViewHolder holder;
    private List<PeopleComeBean> data;

    public MessageAdapter(Context context, List<PeopleComeBean> data) {
        this.mContext = context;
        this.data = data;
    }


    public MessageAdapter(Context context) {
        this.data = new ArrayList<>();
        this.mContext = context;

    }


    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void NotifyAdapter(List<PeopleComeBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }


    public void AddData(PeopleComeBean data) {
        this.data.add(data);
        notifyDataSetChanged();
    }


    public List<PeopleComeBean> getData() {
        return data;
    }

    public void setData(List<PeopleComeBean> data) {
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();


            convertView = View.inflate(mContext, R.layout.item_messageadapter_peoplecome, null);
            holder.tvcontent = (TextView) convertView.findViewById(R.id.tvcontent);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_mes_iv);
            holder.level = (TextView) convertView.findViewById(R.id.item_mes_tv_level);


            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        Log.w("----", "getView: "+data.get(position).getLevel() );
        holder.tvcontent.setText(data.get(position).getData());
        try {
            holder.level.setText(data.get(position).getLevel());

            String color = data.get(position).getColor();
            switch (color) {
                case "blue":
                    Picasso.with(mContext).load(R.mipmap.lan).into(holder.imageView);
                    break;
                case "green":
                    Picasso.with(mContext).load(R.mipmap.lv).into(holder.imageView);
                    break;
                case "orange":
                    Picasso.with(mContext).load(R.mipmap.cheng).into(holder.imageView);
                    break;
                case "yellow":
                    Picasso.with(mContext).load(R.mipmap.huang).into(holder.imageView);
                    break;
                case "purple":
                    Picasso.with(mContext).load(R.mipmap.zi).into(holder.imageView);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvcontent.setText(data.get(position).getData());


        String type = data.get(position).getType();
        if (type == null) {
            holder.level.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvcontent;
        ImageView imageView;
        TextView level;
    }
}