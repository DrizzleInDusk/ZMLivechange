package com.android.zmlive.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zmlive.R;
import com.android.zmlive.fragment.liveing.AudienceDialogFragment;
import com.android.zmlive.fragment.liveing.AudienceViewFragment;
import com.android.zmlive.permission.MPermission;
import com.android.zmlive.permission.annotation.OnMPermissionDenied;
import com.android.zmlive.permission.annotation.OnMPermissionGranted;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseActivity;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent.ChatRoomKickOutReason.CHAT_ROOM_INVALID;

public class MainAudienceActivity extends BaseActivity {
    private String roomId;
    private String Tag="MainAudienceActivity";
    private ChatRoomInfo roomInfo;

    public static int onlinenum = 0;
    public static boolean onaudience = false;
    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    private AudienceViewFragment audienceViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏.
        onlinenum = 0;
        audiactivity=this;
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        roomId = getIntent().getStringExtra("ROOM_ID");
        setContentView(R.layout.mainaudience_activity);
        /*这里可以看到的就是我们将初始化直播的Fragment添加到了这个页面作为填充
        * 并且将MainDialogFragment显示在该页面的顶部已达到各种不同交互的需求*/
        audienceViewFragment = new AudienceViewFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.flmain, audienceViewFragment).commit();
        new AudienceDialogFragment().show(getSupportFragmentManager(), "AudienceDialogFragment");

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
        onaudience=true;
    }

    @Override
    protected void onDestroy() {
        onaudience = false;
        super.onDestroy();
        registerObservers(false);
        showKeyboard(false);
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        overclose();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        audiencefinish();
        return;
    }

    public void audiencefinish() {
        showKeyboard(false);
        ActivityJumpControl.getInstance(activity).popActivity(activity);
        finish();
    }

    private static MainAudienceActivity audiactivity ;
    public static void finaudience(){
        audiactivity.finish();
    }

    private void overclose() {
        quitRoom();
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
                showMessage(getResources().getString(R.string.net_broken));
            }
        }
    };

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            if(chatRoomKickOutEvent.getReason()==CHAT_ROOM_INVALID){
                dialogmess();
                showMessage("主播已停止直播");
            }else{
                showMessage("被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason());
                audiencefinish();
            }
            System.out.println("被踢出聊天室，原因:>>>"+ chatRoomKickOutEvent.getReason());
        }
    };

    private AlertDialog dialog;

    /**
     * dialog 消息提示
     */
    private void dialogmess() {
        try {
            dialog = new AlertDialog.Builder(context).create();
            dialog.show();
            dialog.setContentView(R.layout.dialog_mess);
            TextView dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
            TextView dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
            dialog_mess_title.setText("直播消息提示！");
            dialog_mess_con.setText("" + getResources().getString(R.string.living_finished));
            dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    audiencefinish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Map<String, Object> notext = new HashMap<>();

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
                getroommes();
                LogUtil.showILog("enterRequest>>>", "成功进入聊天室");
            }

            @Override
            public void onFailed(int code) {
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    showMessage("你已被拉入黑名单，不能在进入");
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    showMessage("聊天室不存在");
                } else {
                    showMessage("进入聊天室失败");
                }
                audiencefinish();
                LogUtil.showILog("enterRequest>>>", "进入聊天室失败了code=" + code);
            }

            @Override
            public void onException(Throwable throwable) {
                LogUtil.showILog("enterRequest>>>", "进入聊天室出错了" + throwable.toString());
                audiencefinish();
            }
        });
    }

    private void getroommes() {
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RequestCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo param) {
                getChatRoomMaster(param.getCreator());
            }

            @Override
            public void onFailed(int code) {
                LogUtil.showELog(Tag, "fetch room info failed:" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.showELog(Tag, "fetch room info exception:" + exception);
            }
        });
    }
    private String account;

    public String getAccount() {
        return account;
    }

    private void getChatRoomMaster(String acc){
        // fetch
        List<String> accounts = new ArrayList<>(1);
        accounts.add(acc);
        NIMClient.getService(ChatRoomService.class).fetchRoomMembersByIds(roomId, accounts).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
            @Override
            public void onResult(int code, List<ChatRoomMember> members, Throwable exception) {
                ChatRoomMember member = members.get(0);
                account=member.getAccount();
            }
        });
    }
    /***
     * 退出直播间
     */
    private void quitRoom() {
        xhttp = new Xhttp(URLManager.quitRoom);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
            }

            @Override
            public void onError() {
            }
        });
    }


    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    /**
     * 基本权限管理
     */
    public void requestBasicPermission() {
        MPermission.with((Activity) context)
                .addRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(
                        Manifest.permission.READ_PHONE_STATE)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        if (MainActivity.roompath.equals("")) {
            showMessage(context.getResources().getString(R.string.livenull));
            return;
        }
        audiencefinish();
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityJumpControl.getInstance(activity).gotoAudienceActivity(MainActivity.roompath, MainActivity.joinroomid, MainActivity.ucid, MainActivity.head, MainActivity.liveername);
            }
        },500);
        //        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        showMessage(context.getResources().getString(R.string.nullpermiss));
    }
}
