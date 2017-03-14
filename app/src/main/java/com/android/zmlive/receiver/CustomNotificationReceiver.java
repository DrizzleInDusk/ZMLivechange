package com.android.zmlive.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainActivity;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义通知消息广播接收器
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class CustomNotificationReceiver extends BroadcastReceiver {
    private static int NOTIFY_ID = 1000;
    private String soundon,vibrateon;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = context.getPackageName() + NimIntent.ACTION_RECEIVE_CUSTOM_NOTIFICATION;
        if (action.equals(intent.getAction())) {

            // 从intent中取出自定义通知
            CustomNotification notification = (CustomNotification) intent.getSerializableExtra(NimIntent.EXTRA_BROADCAST_MSG);
//            try {
//                JSONObject obj = JSONObject.parseObject(notification.getContent());
//                if (obj != null && obj.getIntValue("id") == 2) {
//                    // 加入缓存中
////                    CustomNotificationCache.getInstance().addCustomNotification(notification);
//
//                    // Toast
//                    String content = obj.getString("content");
//                    String tip = String.format("自定义消息[%s]：%s", notification.getFromAccount(), content);
//                    Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
//                }
//            } catch (JSONException e) {
//                LogUtil.showELog("demo", e.getMessage());
//            }

            soundon= Futile.getValue(context,"soundon");
            vibrateon=Futile.getValue(context,"vibrateon");
            if(soundon.equals("")){
                soundon="on";
            }
            if(vibrateon.equals("")){
                vibrateon="on";
            }
            sendNotification(context, notification.getContent(), "有新消息！\n" + notification.getContent());
            // 处理自定义通知消息
            LogUtil.showILog("demo", "receive custom notification: " + notification.getContent() + " from :" + notification.getSessionId() + "/" + notification.getSessionType());
        }
    }
//    private  Intent intent;
    private void sendNotification(Context context, String text, String mas) {
        //实例化通知栏构造器NotificationCompat.Builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getResources().getString(R.string.app_name))//设置通知栏标题   "测试标题"
                .setContentText(text) //<span style="font-family: Arial;">/设置通知栏显示内容</span> "测试内容"
                .setTicker(mas) //通知首次出现在通知栏，带上升动画效果的   "测试通知来啦"
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        if(soundon.equals("on")&&vibrateon.equals("on")){
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        }else if(soundon.equals("on")&&!vibrateon.equals("on")){
            mBuilder.setDefaults( Notification.DEFAULT_SOUND);
        }else if(!soundon.equals("on")&&vibrateon.equals("on")){
            mBuilder.setDefaults( Notification.DEFAULT_VIBRATE);
        }

        Notification notification = mBuilder.build();
// 设置启动的程序，如果存在则找出，否则新的启动
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(context, MainActivity.class));//用ComponentName得到class对象
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式，两种情况
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);//将经过设置了的Intent绑定给PendingIntent

        notification.contentIntent = contentIntent;// 通知绑定 PendingIntent
        notification.flags |= Notification.FLAG_AUTO_CANCEL;//点击后删除，如果是FLAG_NO_CLEAR则不删除，FLAG_ONGOING_EVENT用于某事正在进行，例如电话，具体查看参考。

        //获取状态通知栏管理：
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(NOTIFY_ID, notification);
    }

}
