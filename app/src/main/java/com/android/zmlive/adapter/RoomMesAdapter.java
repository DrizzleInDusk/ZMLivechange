package com.android.zmlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.zmlive.R;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * Created by Kkan on 2016/10/6.
 */

public class RoomMesAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater minflater;
    private ArrayList<IMMessage> liveerList;

    public RoomMesAdapter(Context context) {
        super();
        this.minflater = LayoutInflater.from(context);
        this.liveerList = new ArrayList<>();
        this.context = context;
    }
    public void update(IMMessage roomMess){
        liveerList.add(roomMess);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return liveerList.size();
    }

    @Override
    public Object getItem(int position) {
        return liveerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.liveing_activity_item, null);
            holder = new ViewHolder();
            holder.roommes_user_head = (ImageView) convertView
                    .findViewById(R.id.roommes_user_head);
            holder.roommes_user_name = (TextView) convertView
                    .findViewById(R.id.roommes_user_name);
            holder.roommes_content = (TextView) convertView
                    .findViewById(R.id.roommes_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        IMMessage mess = liveerList.get(position);
        holder. roommes_user_name.setText(mess.getContent());
        holder. roommes_content.setText(mess.getContent());
        return convertView;
    }

    class ViewHolder {
        private TextView roommes_user_name,roommes_content;
        private ImageView roommes_user_head;
    }

}
