package com.android.zmlive.fragment.liveing;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.zmlive.R;
import com.android.zmlive.entity.LiveerMess;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 横向listview的数据适配器，也就是观众列表，后居者可以直接根据自己的需求在这里改功能以及布局文件就ok
 */
public class OnlinPeopleAdapter extends BaseAdapter {

    private Context context;
    private String roomId;
    private LayoutInflater minflater;
    private ArrayList<LiveerMess> userlist;

    public OnlinPeopleAdapter(Context context,String roomId) {
        this.minflater = LayoutInflater.from(context);
        this.context = context;
        this.roomId = roomId;
        userlist = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return userlist.size() == 0 ? 0 : userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return userlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.item_audienceadapter, null);
            holder = new ViewHolder();
            holder.online_people_head = (RoundedImageView) convertView
                    .findViewById(R.id.online_people_head);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String head = userlist.get(position).getHead();
        final String accid = userlist.get(position).getAccid();
        Picasso.with(context).load(head).error(R.mipmap.avatar_def).into(holder.online_people_head);
        holder.online_people_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpControl.getInstance((Activity) context).gotoUserMesActivity(accid, "1", roomId );
            }
        });
        return convertView;
    }

    private boolean hasitem = false;

    public void updatain(LiveerMess lm) {
        // System.out.println(mArrayList.indexOf(tname));
        hasitem = true;
        int atpos = getitemonpos(lm);
        if (atpos != -1) {
            hasitem = false;
        }
        if (hasitem) {
            this.userlist.add(lm);
        }
        notifyDataSetChanged();
    }

    public void updataout(LiveerMess lm) {
        int atpos = getitemonpos(lm);
        if (getitemonpos(lm) != -1) {
            this.userlist.remove(atpos);
        }
        notifyDataSetChanged();
    }

    private int getitemonpos(LiveerMess lm) {
        int itempos = -1;
        if (userlist.size() > 0) {
            for (int i = 0; i < userlist.size(); i++) {
                LiveerMess item = userlist.get(i);
                if (item.getAccid().equals(lm.getAccid())) {
                    itempos=i;
                }
            }
        }
        return itempos;
    }

    class ViewHolder {
        private RoundedImageView online_people_head;
    }
}