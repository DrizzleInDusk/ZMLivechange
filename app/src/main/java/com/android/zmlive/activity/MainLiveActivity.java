package com.android.zmlive.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.zmlive.R;
import com.android.zmlive.fragment.liveing.LiveDialogFragment;
import com.android.zmlive.fragment.liveing.LiveViewFragment;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import java.util.HashMap;
import java.util.Map;

public class MainLiveActivity extends UI {
    private String roomId;
    private ChatRoomInfo roomInfo;

    public static int onlinenum = 0;
    public static int maxonlinenum = 0;
    public static int onlinefx = 0;
    public static boolean onlive = false;
    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    private LiveViewFragment liveViewFragment;
    private Activity activity;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏.
        onlinenum = 0;
        onlinefx = 0;
        maxonlinenum = 0;
        liactivity = this;
        activity = this;
        context = this;
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        roomId = getIntent().getStringExtra("ROOM_ID");
        setContentView(R.layout.mainlive_activity);
        /*这里可以看到的就是我们将初始化直播的Fragment添加到了这个页面作为填充
        * 并且将MainDialogFragment显示在该页面的顶部已达到各种不同交互的需求*/
        liveViewFragment = new LiveViewFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flmain, liveViewFragment).commit();
        new LiveDialogFragment().show(getSupportFragmentManager(), "LiveDialogFragment");

        // 注册监听
        registerObservers(true);
        enterRoom();
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        onlive = true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!islf) {
            stopmusic();
        }
        onlive = false;
        registerObservers(false);
        showKeyboard(false);
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        overclose();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        livefinish();
        return;
    }

    private boolean islf = false;

    public void livefinish() {
        islf = true;
        stopmusic();
        showKeyboard(false);
        ActivityJumpControl.getInstance(activity).gotoOverLiveActivity(maxonlinenum + "", onlinefx + "");
        ActivityJumpControl.getInstance(activity).popActivity(activity);
        finish();
    }

    public void setmusic(String musicpath) {
        liveViewFragment.setmusic(musicpath);
    }

    public void pausemusic() {
        liveViewFragment.pausemusic();
    }

    public void resumemusic() {
        liveViewFragment.resumemusic();
    }

    public void stopmusic() {
        liveViewFragment.stopmusic();
    }

    private static MainLiveActivity liactivity;

    public static void finlive() {
        liactivity.finish();
    }

    public void switchCamera() {
        liveViewFragment.switchCamera();
    }

    public void switchcameraParameters() {
        liveViewFragment.switchcameraParameters();
    }

    public void switchlvjing() {
        liveViewFragment.doit();
    }

    private void overclose() {
        closeRoom();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }

    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                LogUtil.showILog("enterRequest>>>", "聊天室连接中....");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                LogUtil.showILog("enterRequest>>>", "聊天室登录中....");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {

            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                int code = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
                if (code != ResponseCode.RES_ECONNECTION) {
                    LogUtil.showILog("enterRequest>>>", "未登录....");
                    Toast.makeText(context, "未登录,code=" + code, Toast.LENGTH_LONG).show();
                }
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
                LogUtil.showILog("enterRequest>>>", "网络链接已断开....");
                Toast.makeText(context, getResources().getString(R.string.net_broken), Toast.LENGTH_LONG).show();
            }
        }
    };


    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            Toast.makeText(context, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason(), Toast.LENGTH_LONG).show();
            System.out.println("被踢出聊天室，原因:>>>" + chatRoomKickOutEvent.getReason());
            livefinish();
        }
    };

    private Map<String, Object> notext = new HashMap<>();
    private int isin = 0;

    private void enterRoom() {
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        notext.put("head", MyData.HEAD);
        notext.put("id", MyData.USERID);
        notext.put("lev", MyData.LEVEL);
        notext.put("accid", MyData.ACCOUNT);
        data.setNotifyExtension(notext);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {

            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                LogUtil.showILog("enterRequest>>>", "成功进入聊天室");
                if (isin == 0) {
                    isin++;
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.showILog("主播开启直播后添加机器人>>>", "");
                            addRobot();
                        }
                    }, 5000);
                }
            }

            @Override
            public void onFailed(int code) {
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(context, "你已被拉入黑名单，不能在进入", Toast.LENGTH_LONG).show();
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    Toast.makeText(context, "聊天室不存在", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "进入聊天室失败", Toast.LENGTH_LONG).show();
                }
                livefinish();
                LogUtil.showILog("enterRequest>>>", "进入聊天室失败了code=" + code);
            }

            @Override
            public void onException(Throwable throwable) {
                LogUtil.showILog("enterRequest>>>", "进入聊天室出错了" + throwable.toString());
                livefinish();
            }
        });
    }

    private Xhttp xhttp;

    /***
     * 主播开启直播后添加机器人
     */
    private void addRobot() {
        xhttp = new Xhttp(URLManager.addRobot);
        xhttp.add("uid",  MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {

                Log.i("9999", "onSuccess: ");
            }

            @Override
            public void onError() {

            }
        });
    }

    /***
     * 关闭直播
     */
    private void closeRoom() {
        xhttp = new Xhttp(URLManager.closeRoom);
        xhttp.add("cid", MyData.CID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onError() {
            }
        });
    }
}
