package com.android.zmlive.fragment.liveing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainLiveActivity;
import com.android.zmlive.entity.LiveerMess;
import com.android.zmlive.entity.PeopleComeBean;
import com.android.zmlive.entity.Song;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseFragment;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static com.netease.nimlib.sdk.msg.constant.NotificationType.ChatRoomMemberExit;
import static com.netease.nimlib.sdk.msg.constant.NotificationType.ChatRoomMemberIn;

/**
 * 该Fragment是用于dialogFragment中的pager，为了实现滑动隐藏交互Fragment的
 * 交互的操作都在这个界面实现的，如果大家要改交互主要修改这个界面就可以了
 */
public class LayerFragment extends BaseFragment implements View.OnClickListener {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
            }
        }
    };

    /**
     * 标示判断
     */
    private boolean isOpen;

    /**
     * 动画相关
     */
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;
    private AnimatorSet animatorSetHide = new AnimatorSet();
    private AnimatorSet animatorSetShow = new AnimatorSet();

    /**
     * 数据相关
     */
    private List<View> giftViewCollection = new ArrayList<View>();
    private List<String> messageData = new LinkedList<>();
    private MessageAdapter messageAdapter;

    private String roomId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liveer, container, false);

        findview(view);
        roomId = ((MainLiveActivity) context).getRoomId();
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearTiming();
        return view;
    }


    private OnlinPeopleAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llInputParent.getVisibility() == View.VISIBLE) {
                    liveer_bottomrl.setVisibility(View.VISIBLE);
                    llInputParent.setVisibility(View.GONE);
                    if (fximgvis) {
                        liveer_fximg.setVisibility(View.VISIBLE);
                        liveer_gbfximg.setVisibility(View.VISIBLE);
                    }
                    hideKeyboard();
                }
                if (liveer_shezhi.isSelected()) {
                    setvis(View.GONE);
                    liveer_shezhi.setSelected(false);
                }
            }
        });
        softKeyboardListnenr();
        registerObservers(true);
        messageAdapter = new MessageAdapter(getActivity());
        lvmessage.setAdapter(messageAdapter);
        lvmessage.setSelection(messageData.size());

        adapter = new OnlinPeopleAdapter(context, roomId);
        hlvaudience.setAdapter(adapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }


    /**
     * 消息监听器  只写了成员进入的  礼物接受消息还没写   礼物消息部分可以  参照 A
     */
    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> chatRoomMessages) {
            for (ChatRoomMessage chatRoomMessage : chatRoomMessages) {
                ChatRoomNotificationAttachment notifyExtension = (ChatRoomNotificationAttachment) chatRoomMessage.getAttachment();
                LogUtil.showWLog(Tag, "新收到的消息message.getAttachment()>>>" + chatRoomMessage.getAttachment());
                if (notifyExtension.getType() == NotificationType.ChatRoomMemberIn) {
                    fetchData();

                    for (ChatRoomMember item : items) {
                        Log.i("----", "onEvent: " + item.getNick());
                    }


                }


            }
        }
    };

    private void userinroom(String head, String id, String lev, final String accid, String operator) {
        ChatRoomMember chatRoomMember = new ChatRoomMember();
        chatRoomMember.setAvatar(head);
        chatRoomMember.setNick(id);
        chatRoomMember.setAccount(accid);
        chatRoomMember.setRoomId(lev);
        if (!memberCache.containsKey(operator)) {

            LiveerMess lm = new LiveerMess();
            lm.setHead(URLManager.head + head);
            lm.setAccid(accid);
            adapter.updatain(lm);


        }
        memberCache.put(operator, chatRoomMember);
    }

    private void useroutroom(String operator) {
        ChatRoomMember chatRoomMember = memberCache.get(operator);
        String accid = chatRoomMember.getAccount();
        LiveerMess lm = new LiveerMess();
        lm.setAccid(accid);
        adapter.updataout(lm);
        memberCache.remove(operator);
    }

    private void setqmd(String content) {
        try {
            JSONObject object = new JSONObject(content);
            String dsname = object.getString("nickName");
            String zDiamond = object.getString("zDiamond");
            String sDiamond = object.getString("sDiamond");
            String sPic = object.getString("sPic");
            try {
                int zs = Integer.parseInt(sDiamond);
                MainLiveActivity.onlinefx = MainLiveActivity.onlinefx + zs;
            } catch (Exception e) {
                e.printStackTrace();
            }
            liveer_liwuimg.setVisibility(View.VISIBLE);
            liveer_liwuimg.setImageResource(R.mipmap.avatar_def);
            Picasso.with(context).load(sPic).into(liveer_liwuimg);
            if (intype) {
                getHandler().removeCallbacks(runnable);
            }
            getHandler().postDelayed(runnable, 2000);
            intype = false;
            MyData.PROFIT = zDiamond;
            liveer_qinmi.setText(getResources().getString(R.string.qinmi) + " " + zDiamond);
            showGift(dsname + sDiamond + "", "系统", dsname + "打赏主播");
//            roomtisi("系統", dsname + "打賞主播");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean intype = false;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            liveer_liwuimg.setVisibility(View.GONE);
            intype = true;
        }
    };

    private static final int LIMIT = 1000;
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime

    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
    private List<ChatRoomMember> items = new ArrayList<>();
    private boolean isNormalEmpty = false; // 固定成员是否拉取完
    private String Tag = "LayerFragment";

    private void fetchData() {
        LogUtil.showELog(Tag, "网易云信开始获取在线成员信息");
        if (!isNormalEmpty) {
            // 拉取固定在线成员
            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
        } else {
            // 拉取非固定成员
            getMembers(MemberQueryType.GUEST, enterTime, 0);
        }
    }

    /**
     * 获取成员列表
     */

    private List<ChatRoomMember> HaveResult = new ArrayList<>();


    private List<ChatRoomMember> HaResult = new ArrayList<>();

    private void getMembers(final MemberQueryType memberQueryType, final long time, int limit) {
        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, memberQueryType, time, (LIMIT - limit)).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
            @Override
            public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                boolean success = code == ResponseCode.RES_SUCCESS;
                if (success) {

                    checkMemberHave(HaveResult, result);
                    addMembers(result);
                    if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < LIMIT) {
                        isNormalEmpty = true; // 固定成员已经拉完
                        getMembers(MemberQueryType.GUEST, enterTime, result.size());
                    }
                } else {
                    LogUtil.showELog(Tag, "fetch members by page failed, code:" + code);
                }
            }
        });
        if (isNormalEmpty) {
            stopRefreshing();
        }
    }

    private List<ChatRoomMember> checkMemberHave(List<ChatRoomMember> haveResult, List<ChatRoomMember> result) {
        List<ChatRoomMember> reS = new ArrayList<>();


        if (haveResult.size() == 0) {
            haveResult.addAll(result);

            for (ChatRoomMember chatRoomMember : result) {
                List<String> strings=new ArrayList<>();
                strings.add(chatRoomMember.getAccount());
                NIMClient.getService(UserService.class).fetchUserInfo(strings)
                        .setCallback(new RequestCallback<List<NimUserInfo>>() {
                            @Override
                            public void onSuccess(List<NimUserInfo> nimUserInfos) {
                                NimUserInfo userInfo=nimUserInfos.get(0);
                                String s=userInfo.getExtension().toString();
                                try {
                                    JSONObject object=new JSONObject(s);
                                    String color=object.getString("colour");
                                    String level=object.getString("level");


                                    messageAdapter.AddData(new PeopleComeBean(userInfo.getName()+ "来啦！",color,"join",level));
                                    showGift(userInfo.getName(), level + "级",userInfo.getName() + "进入直播间");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                for (NimUserInfo nimUserInfo : nimUserInfos) {
                                    Log.w("----", "onSuccess: "+nimUserInfo.getExtension().toString() );
                                }





                                for (NimUserInfo nimUserInfo : nimUserInfos) {
                                    Log.w("----", "onSuccess: "+nimUserInfo.getExtension().toString() );
                                }



                            }

                            @Override
                            public void onFailed(int i) {

                            }

                            @Override
                            public void onException(Throwable throwable) {

                            }
                        });

            }
            return result;
        } else {
            result.removeAll(haveResult);
            for (final ChatRoomMember chatRoomMember : result) {
                List<String> strings=new ArrayList<>();
                strings.add(chatRoomMember.getAccount());
                NIMClient.getService(UserService.class).fetchUserInfo(strings)
                        .setCallback(new RequestCallback<List<NimUserInfo>>() {
                            @Override
                            public void onSuccess(List<NimUserInfo> nimUserInfos) {
                                NimUserInfo userInfo=nimUserInfos.get(0);
                                String s=userInfo.getExtension().toString();
                                try {
                                    JSONObject object=new JSONObject(s);
                                    String color=object.getString("colour");
                                    String level=object.getString("level");


                                    messageAdapter.AddData(new PeopleComeBean(userInfo.getName()+ "来啦！",color,"join",level));
                                    showGift(userInfo.getName(), level + "级",userInfo.getName() + "进入直播间");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                for (NimUserInfo nimUserInfo : nimUserInfos) {
                                    Log.w("----", "onSuccess: "+nimUserInfo.getExtension().toString() );
                                }



                            }

                            @Override
                            public void onFailed(int i) {

                            }

                            @Override
                            public void onException(Throwable throwable) {

                            }
                        });

            }
            return result;
//
//            for (ChatRoomMember chatRoomMember : result) {
//                for (ChatRoomMember roomMember : haveResult) {
//                    Log.w("---", "checkMemberHave: " + roomMember.getAccount() + "-" + chatRoomMember.getAccount());
//                    if (roomMember.getAccount() == chatRoomMember.getAccount()) {
//                        continue;
//                    } else {
//                        reS.add(chatRoomMember);
//                    }
//                }
//
//
//            }
//            return reS;
        }


    }

    private void stopRefreshing() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                listView.onRefreshComplete();
                addheaditem();
            }
        }, 50);
    }

    private void addheaditem() {


        LogUtil.showELog(Tag, "在线成员人数>>>>" + items.size());
//        MainLiveActivity.onlinenum = MainLiveActivity.onlinenum + items.size();
        liveer_online.setText(getResources().getString(R.string.onlinepeo) + "   " + items.size());

        for (final ChatRoomMember member : items) {

            LogUtil.showELog(Tag, "人>>>>" + member.getNick());
            String accid = member.getAccount();
            LiveerMess lm = new LiveerMess();
            lm.setHead(member.getAvatar());
            lm.setAccid(accid);
            adapter.updatain(lm);

            // messageAdapter.NotifyAdapter();


            userinroom(member.getAvatar(), roomId, member.getMemberLevel() + "", member.getAccount(), member.getAccount());


//
//            final String accid = member.getAccount();
//            if (!accid.equals(MyData.ACCOUNT)) {
//                LiveerMess lm = new LiveerMess();
//                lm.setHead(member.getAvatar());
//                lm.setAccid(accid);
//                adapter.updatain(lm);
//            }
        }
//        for (ChatRoomMember chatRoomMember : HaResult) {
//            messageAdapter.AddData(chatRoomMember.getNick() + "来啦！");
//        }
        MainLiveActivity.onlinenum = items.size();
        MainLiveActivity.maxonlinenum = items.size();

    }

    private void addMembers(List<ChatRoomMember> members) {
        for (ChatRoomMember member : members) {
            if (!isNormalEmpty) {
                updateTime = member.getUpdateTime();
            } else {
                enterTime = member.getEnterTime();
            }

            if (memberCache.containsKey(member.getAccount())) {
                items.remove(memberCache.get(member.getAccount()));
            }
            memberCache.put(member.getAccount(), member);
            Log.i("------>>>>>>>", "addMembers: " + member.getNick());
            items.add(member);

        }
        Collections.sort(items, comp);
    }

    private static Map<MemberType, Integer> compMap = new HashMap<>();

    static {
        compMap.put(MemberType.CREATOR, 0);
        compMap.put(MemberType.ADMIN, 1);
        compMap.put(MemberType.NORMAL, 2);
        compMap.put(MemberType.LIMITED, 3);
        compMap.put(MemberType.GUEST, 4);
    }

    private static Comparator<ChatRoomMember> comp = new Comparator<ChatRoomMember>() {
        @Override
        public int compare(ChatRoomMember lhs, ChatRoomMember rhs) {
            if (lhs == null) {
                return 1;
            }
            if (rhs == null) {
                return -1;
            }
            return compMap.get(lhs.getMemberType()) - compMap.get(rhs.getMemberType());
        }
    };

    /**
     * 界面相关
     */
    private HorizontalListView hlvaudience;
    private ListView lvmessage;
    private EditText etInput;
    private LinearLayout llpicimage, llgiftcontent, llInputParent, liveer_shezhill;
    private View sixin_fragment;
    private RelativeLayout rlsentimenttime, tvChat, liveer_shezhi, liveer_bottomrl, livefenxiang, liveyinyue, livesixin;
    private TextView liveer_online, sendInput, liveer_qinmi, liveer_id;
    private ImageView liveer_liwuimg, liveer_fanzhuan, liveer_meiyan, liveer_sgd, liveer_bf, liveer_fximg, liveer_gbfximg;

    private View notifyBar;
    private TextView notifyBarText;
    private FlingListeber listener;
    private GestureDetector detector;

    private void findview(View view) {
        sixin_fragment = view.findViewById(R.id.sixin_fragment);//私信
        notifyBar = view.findViewById(R.id.status_notify_bar);
        notifyBarText = (TextView) view.findViewById(R.id.status_desc_label);
        notifyBar.setVisibility(View.GONE);
        sixin_fragment.setVisibility(View.GONE);

        listener = new FlingListeber();
        detector = new GestureDetector(listener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        liveer_bottomrl = (RelativeLayout) view.findViewById(R.id.liveer_bottomrl);
        llpicimage = (LinearLayout) view.findViewById(R.id.liveer_llpicimage);//在线人数父布局
        rlsentimenttime = (RelativeLayout) view.findViewById(R.id.liveer_rlsentimenttime);//亲密度父布局
        liveer_online = (TextView) view.findViewById(R.id.liveer_online);//在线人数
        liveer_qinmi = (TextView) view.findViewById(R.id.liveer_qinmi);//亲密度
        liveer_id = (TextView) view.findViewById(R.id.liveer_id);//直播间id
        hlvaudience = (HorizontalListView) view.findViewById(R.id.liveer_hlvaudience);//显示所有头像
        hlvaudience.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        liveer_fximg = (ImageView) view.findViewById(R.id.liveer_fximg);//推荐分享
        liveer_fximg.setOnClickListener(this);
        liveer_gbfximg = (ImageView) view.findViewById(R.id.liveer_gbfximg);//推荐分享显示
        liveer_gbfximg.setOnClickListener(this);
        llgiftcontent = (LinearLayout) view.findViewById(R.id.liveer_llgiftcontent);//礼物动画显示
        liveer_liwuimg = (ImageView) view.findViewById(R.id.liveer_liwuimg);//礼物显示
        lvmessage = (ListView) view.findViewById(R.id.liveer_lvmessage);//聊天显示
        lvmessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        tvChat = (RelativeLayout) view.findViewById(R.id.liveer_tvChat);//发送聊天显示
        tvChat.setOnClickListener(this);
        view.findViewById(R.id.liveing_tuichus).setOnClickListener(this);//退出
        llInputParent = (LinearLayout) view.findViewById(R.id.liveer_llinputparent);//聊天框父布局
        etInput = (EditText) view.findViewById(R.id.liveer_etInput);//聊天输入框
        sendInput = (TextView) view.findViewById(R.id.liveer_sendInput);//聊天发送按钮
        sendInput.setOnClickListener(this);
        livesixin = (RelativeLayout) view.findViewById(R.id.livesixin);//私信按钮
        livesixin.setOnClickListener(this);
        liveyinyue = (RelativeLayout) view.findViewById(R.id.liveyinyue);//音乐按钮
        liveyinyue.setOnClickListener(this);
        livefenxiang = (RelativeLayout) view.findViewById(R.id.livefenxiang);//分享按钮
        livefenxiang.setOnClickListener(this);
        liveer_shezhi = (RelativeLayout) view.findViewById(R.id.liveer_shezhi);//设置按钮
        liveer_shezhi.setOnClickListener(this);
        liveer_shezhill = (LinearLayout) view.findViewById(R.id.liveer_shezhill);//设置按钮父布局
        liveer_fanzhuan = (ImageView) view.findViewById(R.id.liveer_fanzhuan);//设置反转摄像头
        liveer_fanzhuan.setOnClickListener(this);
        liveer_meiyan = (ImageView) view.findViewById(R.id.liveer_meiyan);//设置美颜
        liveer_meiyan.setOnClickListener(this);
        liveer_sgd = (ImageView) view.findViewById(R.id.liveer_sgd);//设置闪光灯
        liveer_sgd.setOnClickListener(this);
        liveer_bf = (ImageView) view.findViewById(R.id.liveer_bf);//播放按钮
        liveer_bf.setOnClickListener(this);

        liveer_id.setText("ID " + MyData.USERID);
        liveer_qinmi.setText(getResources().getString(R.string.qinmi) + " " + MyData.PROFIT);
        Picasso.with(context).load(URLManager.ads + MyData.Xiaopic).into(liveer_fximg);
        liveer_online.setText(getResources().getString(R.string.onlinepeo) + "   " + MainLiveActivity.onlinenum);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liveer_tvChat:/*聊天*/
                showChat();
                break;
            case R.id.liveer_sendInput:/*发送*/
                sendText();
                break;
            case R.id.liveing_tuichus:/*退出直播*/
                dialogmess();
                break;
            case R.id.dialog_mess_quxiao:/*退出直播否*/
                dialog.dismiss();
                break;
            case R.id.dialog_mess_yes:/*退出直播 是*/
                overclose();
                dialog.dismiss();
                break;
            case R.id.liveer_shezhi:/*设置按钮*/
                if (liveer_shezhi.isSelected()) {
                    setvis(View.GONE);
                    liveer_shezhi.setSelected(false);
                } else {
                    liveer_shezhi.setSelected(true);
                    setvis(View.VISIBLE);
                }
                break;
            case R.id.liveer_fanzhuan:/*设置反转摄像头*/
                ((MainLiveActivity) context).switchCamera();
                break;
            case R.id.liveer_sgd:/*设置闪光灯*/
                ((MainLiveActivity) context).switchcameraParameters();
                break;
            case R.id.liveer_meiyan:/*设置滤镜切换*/
                ((MainLiveActivity) context).switchlvjing();
                break;
            case R.id.livefenxiang:/*分享*/
                if (canfenx) {
                    canfenx = false;
                    dialoshare();
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canfenx = true;
                        }
                    }, 2000);
                }
                break;
            case R.id.liveyinyue:/*音乐按钮*/
                dialogmusic();
                break;
            case R.id.livesixin:/*私信按钮*/
                ActivityJumpControl.getInstance(activity).gotoWodeSixinActivity();
                break;
            case R.id.liveer_bf:/*播放按钮*/
                if (liveer_bf.isSelected()) {
                    ((MainLiveActivity) context).resumemusic();
                    liveer_bf.setImageResource(R.mipmap.pause);
                    liveer_bf.setSelected(false);
                } else {
                    ((MainLiveActivity) context).pausemusic();
                    liveer_bf.setImageResource(R.mipmap.musicplay);
                    liveer_bf.setSelected(true);
                }

                break;
            case R.id.liveer_fximg:/*推荐分享*/
                if (fximgvis) {
                    ActivityJumpControl.getInstance(activity).gotoFenxiangActivity();
                }
                break;
            case R.id.liveer_gbfximg:/*推荐分享显示*/
                fximgvis = false;
                liveer_fximg.setVisibility(View.GONE);
                liveer_gbfximg.setVisibility(View.GONE);
                break;
        }
    }

    private boolean fximgvis = true;
    private String mymusicpath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);

    }

    private boolean canfenx = true;

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.showDLog("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Futile.showMessage(context, platform + " 收藏成功啦");
            } else {
                Futile.showMessage(context, platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Futile.showMessage(context,platform + " 分享失败啦");
            if (t != null) {
                LogUtil.showDLog("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Futile.showMessage(context,platform + " 分享取消了");
        }
    };

    private void setvis(int v) {
        liveer_shezhill.setVisibility(v);
        liveer_fanzhuan.setVisibility(v);
        liveer_meiyan.setVisibility(v);
        liveer_sgd.setVisibility(v);
    }

    private AlertDialog dialog;
    private ListView mlist_listview;

    /**
     * dialog 音乐
     */
    private void dialogmusic() {
        dialog = new AlertDialog.Builder(context, R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_music);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getWidth() * 0.5); // 高度设置为屏幕的0.5
        p.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        mlist_listview = (ListView) dialog.findViewById(R.id.mymlist_listview);
        new getsoundTask().execute();
    }

    /**
     * dialog 消息提示
     */
    private void dialogmess() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_mess);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        TextView dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
        TextView dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
        dialog_mess_title.setText("消息提示！");
        dialog_mess_con.setText("是否要退出直播?");
        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(this);
    }

    /**
     * dialog 分享
     */
    private void dialoshare() {
        dialog = new AlertDialog.Builder(context, R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_fenxiang);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        dialog.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.LINE)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareAction(activity).setPlatform(SHARE_MEDIA.FACEBOOK)
                        .withTitle(getResources().getString(R.string.fxbt))
                        .withText(getResources().getString(R.string.fxnr))
                        .withTargetUrl(URLManager.shareurl)
                        .setCallback(umShareListener)
                        .share();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.share_qx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void overclose() {
        ((MainLiveActivity) context).livefinish();
    }

    /**
     * 添加礼物view,(考虑垃圾回收)
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            /*如果垃圾回收中没有view,则生成一个*/
            view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_roomin, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            llgiftcontent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }

    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = llgiftcontent.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llgiftcontent.removeViewAt(index);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }

    /**
     * 显示礼物的方法
     */
    private void showGift(String tag, String leve, String name) {

        View giftView = llgiftcontent.findViewWithTag(tag);
        if (giftView == null) {/*该用户不在礼物显示列表*/
            if (llgiftcontent.getChildCount() > 1) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
                View giftView1 = llgiftcontent.getChildAt(0);
                TextView picTv1 = (TextView) giftView1.findViewById(R.id.inname);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = llgiftcontent.getChildAt(1);
                TextView picTv2 = (TextView) giftView2.findViewById(R.id.inname);
                long lastTime2 = (Long) picTv2.getTag();
                if (lastTime1 > lastTime2) {/*如果第二个View显示的时间比较长*/
                    removeGiftView(1);
                } else {/*如果第一个View显示的时间长*/
                    removeGiftView(0);
                }
            }

            giftView = addGiftView();/*获取礼物的View的布局*/
            giftView.setTag(tag);/*设置view标识*/

            TextView crvheadimage = (TextView) giftView.findViewById(R.id.inname);
            TextView giftinlv = (TextView) giftView.findViewById(R.id.inlv);/*找到数量控件*/
            crvheadimage.setText(name);
            giftinlv.setText(leve);
//            giftNum.setText("x1");/*设置礼物数量*/
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/
//            giftNum.setTag(1);/*给数量控件设置标记*/

            llgiftcontent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            llgiftcontent.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    giftNumAnim.start(giftNum);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {/*该用户在礼物显示列表*/
            TextView crvheadimage = (TextView) giftView.findViewById(R.id.inname);/*找到头像控件*/
            TextView giftinlv = (TextView) giftView.findViewById(R.id.inlv);/*找到数量控件*/
            crvheadimage.setText(name);
            giftinlv.setText(leve);
//            int showNum = (Integer) giftNum.getTag() + 1;
//            giftNum.setText("x" + showNum);
//            giftNum.setTag(showNum);
            crvheadimage.setTag(System.currentTimeMillis());
//            giftNumAnim.start(giftNum);
        }
    }

    /**
     * 显示聊天布局
     */
    private void showChat() {
        liveer_bottomrl.setVisibility(View.GONE);
        llInputParent.setVisibility(View.VISIBLE);
        if (fximgvis) {
            liveer_fximg.setVisibility(View.GONE);
            liveer_gbfximg.setVisibility(View.GONE);
        }
        llInputParent.requestFocus();
        showKeyboard();
    }

    /**
     * 发送消息
     */
    private void sendText() {
        String inputmess = etInput.getText().toString().trim();
        if (!inputmess.isEmpty()) {
            checkSW(inputmess);
            hideKeyboard();
        } else
            hideKeyboard();
    }


    private void sendmess(String msg) {
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(roomId, msg);
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        LogUtil.showILog(Tag, "消息发送成功");
                    }

                    @Override
                    public void onFailed(int code) {
                        LogUtil.showILog(Tag, "消息发送失败：code:" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        LogUtil.showILog(Tag, "消息发送失败");
//                        showMessage("消息发送失败！");
                    }
                });
        messageAdapter.AddData(new PeopleComeBean("你: " + etInput.getText().toString().trim(),null,null,null));
        etInput.setText("");
//        messageAdapter.NotifyAdapter(messageData);
        lvmessage.setSelection(messageAdapter.getCount());
    }

    private Xhttp xhttp;

    /***
     * 敏感词过滤
     */
    private void checkSW(final String msg) {
        xhttp = new Xhttp(URLManager.checkSW);
        xhttp.add("word", msg);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    if (status.equals("1")) {
                        sendmess(msg);
                    } else {
                        String mess = obj.getString("meg");
                        Futile.showMessage(context, mess);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
            }
        });
    }


    private class FlingListeber implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY())) {
                if (e2.getY() - e1.getY() > 30) {// 向下滑
                    LogUtil.showDLog("liveer>>>>>", "FlingListeber>>向下滑");
                    doit();
                } else if (e1.getY() - e2.getY() > 30) {// 向上滑
                    LogUtil.showDLog("liveer>>>>>", "FlingListeber>>向上滑");
                    doit();
                }
            }
            return false;
        }
    }

    private void doit() {
//        ((MainLiveActivity) context).switchlvjing();
    }

    /**
     * 显示软键盘并因此头布局
     */
    private void showKeyboard() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etInput, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    /**
     * 隐藏软键盘并显示头布局
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    /**
     * 软键盘显示与隐藏的监听
     */
    private void softKeyboardListnenr() {
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {/*软键盘显示：执行隐藏title动画，并修改listview高度和装载礼物容器的高度*/
                animateToHide();
                dynamicChangeListviewH(100);
                dynamicChangeGiftParentH(true);
            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏：隐藏聊天输入框并显示聊天按钮，执行显示title动画，并修改listview高度和装载礼物容器的高度*/
                liveer_bottomrl.setVisibility(View.VISIBLE);
                llInputParent.setVisibility(View.GONE);
                if (fximgvis) {
                    liveer_fximg.setVisibility(View.VISIBLE);
                    liveer_gbfximg.setVisibility(View.VISIBLE);
                }
                animateToShow();
                dynamicChangeListviewH(150);
                dynamicChangeGiftParentH(false);
            }
        });
    }

    /**
     * 动态的修改listview的高度
     *
     * @param heightPX
     */
    private void dynamicChangeListviewH(int heightPX) {
        ViewGroup.LayoutParams layoutParams = lvmessage.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(getActivity(), heightPX);
        lvmessage.setLayoutParams(layoutParams);
    }

    /**
     * 动态修改礼物父布局的高度
     *
     * @param showhide
     */
    private void dynamicChangeGiftParentH(boolean showhide) {
        if (showhide) {/*如果软键盘显示中*/
            if (llgiftcontent.getChildCount() != 0) {
                /*判断是否有礼物显示，如果有就修改父布局高度，如果没有就不作任何操作*/
                ViewGroup.LayoutParams layoutParams = llgiftcontent.getLayoutParams();
                layoutParams.height = llgiftcontent.getChildAt(0).getHeight();
                llgiftcontent.setLayoutParams(layoutParams);
            }
        } else {/*如果软键盘隐藏中*/
            /*就将装载礼物的容器的高度设置为包裹内容*/
            ViewGroup.LayoutParams layoutParams = llgiftcontent.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            llgiftcontent.setLayoutParams(layoutParams);
        }
    }

    /**
     * 头部布局执行显示的动画
     */
    private void animateToShow() {
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(rlsentimenttime, "translationX", -rlsentimenttime.getWidth(), 0);
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(llpicimage, "translationY", -llpicimage.getHeight(), 0);
        animatorSetShow.playTogether(leftAnim, topAnim);
        animatorSetShow.setDuration(300);
        animatorSetShow.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isOpen = true;
            }
        });
        if (!isOpen) {
            animatorSetShow.start();
        }
    }

    /**
     * 头部布局执行退出的动画
     */
    private void animateToHide() {
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(rlsentimenttime, "translationX", 0, -rlsentimenttime.getWidth());
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(llpicimage, "translationY", 0, -llpicimage.getHeight());
        animatorSetHide.playTogether(leftAnim, topAnim);
        animatorSetHide.setDuration(300);
        animatorSetHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isOpen = true;
            }
        });
        if (!isOpen) {
            animatorSetHide.start();
        }
    }

    /**
     * 定时清除礼物
     */
    private void clearTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = llgiftcontent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = llgiftcontent.getChildAt(i);
                    TextView crvheadimage = (TextView) view.findViewById(R.id.inname);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 3000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);
    }

    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }

    private SongListAdapter songadapter;

    class getsoundTask extends AsyncTask<ArrayList<Song>, ArrayList<Song>, ArrayList<Song>> {
        @Override
        protected ArrayList<Song> doInBackground(ArrayList<Song>... arrayLists) {
            return Futile.getMusicFile(context);
        }

        @Override
        protected void onPreExecute() {
            showloading();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Song> result) {
            dismissloading();
            songadapter = new SongListAdapter(context, result);
            mlist_listview.setAdapter(songadapter);
            super.onPostExecute(result);
        }
    }

    private class SongListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater minflater;
        private ArrayList<Song> songList;

        public SongListAdapter(Context context, ArrayList<Song> songList) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.songList = songList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return songList.size();
        }

        @Override
        public Object getItem(int position) {
            return songList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Song song = songList.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.musiclist_activity_item2, null);
                holder = new ViewHolder();
                holder.song_name = (TextView) convertView
                        .findViewById(R.id.song_name);
                holder.song_time = (TextView) convertView
                        .findViewById(R.id.song_time);
                holder.song_size = (TextView) convertView
                        .findViewById(R.id.song_size);
                holder.song_down = (TextView) convertView
                        .findViewById(R.id.song_down);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String filename = song.getTitle();
            final String musicpath = song.getMusicpath();
            int shichang = song.getShichang();
            long musicsize = song.getMusicsize();
            int m = shichang / 1000 % 60;
            int s = shichang / 1000 / 60;
            String msize = Futile.getFormatSize(musicsize);
            holder.song_name.setText(filename);
            holder.song_time.setText("时长：" + s + "：" + m);
            holder.song_size.setText(msize);
            holder.song_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (musicpath.isEmpty()) {
                        return;
                    }
                    mymusicpath = musicpath;
                    if (liveer_bf.getVisibility() != View.VISIBLE) {
                        liveer_bf.setVisibility(View.VISIBLE);
                    }
                    ((MainLiveActivity) context).setmusic(mymusicpath);
                    liveer_bf.setImageResource(R.mipmap.pause);
                    liveer_bf.setSelected(false);
                    dialog.dismiss();
                }
            });
            return convertView;
        }

        class ViewHolder {
            private TextView song_name;
            private TextView song_time;
            private TextView song_size;
            private TextView song_down;

        }
    }
}