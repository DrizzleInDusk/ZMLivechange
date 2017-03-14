package com.android.zmlive.fragment.liveing;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.LogWriter;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainActivity;
import com.android.zmlive.activity.MainAudienceActivity;
import com.android.zmlive.entity.LiveerMess;
import com.android.zmlive.entity.LwMess;
import com.android.zmlive.entity.PeopleComeBean;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.MyData;
import com.android.zmlive.tool.URLManager;
import com.android.zmlive.tool.Xhttp;
import com.android.zmlive.tool.XhttpCallBack;
import com.android.zmlive.tool.utils.BaseFragment;
import com.android.zmlive.tool.utils.LogUtil;
import com.android.zmlive.view.PeriscopeLayout;
import com.android.zmlive.view.RoundedImageView;
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
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONArray;
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
 *
 * 这个界面是 观众的交互页面
 */
public class AudienceFragment extends BaseFragment implements View.OnClickListener {

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
    private String Tag = "AudienceFragment";


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
        View view = inflater.inflate(R.layout.fragment_audience, container, false);

        ucid = activity.getIntent().getStringExtra("ucid");
        head = activity.getIntent().getStringExtra("head");
        liveername = activity.getIntent().getStringExtra("liveername");
        findview(view);
        roomId = ((MainAudienceActivity) context).getRoomId();
        getroommes();
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getActivity(), R.anim.gift_out);
  giftNumAnim = new NumAnim();
        clearTiming();
        return view;
    }

    private OnlinPeopleAdapter adapter;
    private String account;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llInputParent.getVisibility() == View.VISIBLE) {
                    audience_bottomrl.setVisibility(View.VISIBLE);
                    llInputParent.setVisibility(View.GONE);
                    if(fximgvis) {
                        audience_fximg.setVisibility(View.VISIBLE);
                        audience_gbfximg.setVisibility(View.VISIBLE);
                    }
                    hideKeyboard();
                }
                if(liwu){
                    liwu = false;
                    try {
                        dialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        softKeyboardListnenr();
        registerObservers(true);
        account = ((MainAudienceActivity) context).getAccount();
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
     * 聊天室 消息监听       礼物消息  成员进入 都在这个里面处理（可能要重新些一下）  观众的还没写  可以参照 Layerfragment
     */
    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            // 处理新收到的消息
            if (messages == null || messages.isEmpty()) {
                return;
            }
            for (ChatRoomMessage message : messages) {
                // 保证显示到界面上的消息，来自同一个聊天室
                if (message.getAttachment() != null) {
                    ChatRoomNotificationAttachment notifyExtension = (ChatRoomNotificationAttachment) message.getAttachment();
                    Map<String, Object> notext = notifyExtension.getExtension();
                    final String operator = notifyExtension.getOperator();
                    final String operatorNick = notifyExtension.getOperatorNick();
                    LogUtil.showWLog(Tag, "新收到的消息message.getAttachment()>>>" + message.getAttachment());


                    if (notifyExtension.getType().equals(ChatRoomMemberIn)) {
                        if (notext != null) {
                            final String head = (String) notext.get("head");
                            final String id = notext.get("id") + "";
                            final String lev = notext.get("lev") + "";
                            final String accid = (String) notext.get("accid");
                            if (MyData.USERID.equals(id)) {
                                getHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchData();
                                    }
                                }, 200);
                            }
                            getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    userinroom(head, id, lev, accid, operator);
                                }
                            });
                            showGift(operatorNick, lev + "级", operatorNick + "进入直播间");
                           // roomtisi(lev + "級", operatorNick + "進入直播間");

                            if (!accid.equals(MyData.ACCOUNT)) {
                                MainAudienceActivity.onlinenum = MainAudienceActivity.onlinenum + 1;
                            }
                        }
                        onlinepeople.setText(MainAudienceActivity.onlinenum + " 人");
                    } else if (notifyExtension.getType().equals(ChatRoomMemberExit)) {
                        if (MainAudienceActivity.onlinenum > 2) {
                            MainAudienceActivity.onlinenum = MainAudienceActivity.onlinenum - 1;
                        } else {
                            MainAudienceActivity.onlinenum = 1;
                        }
                        onlinepeople.setText(MainAudienceActivity.onlinenum + " 人");
                        useroutroom(operator);
                    }
                } else {
                    LogUtil.showELog(Tag, "新收到的消息message.getContent()>>>" + message.getContent());
                    String account = message.getFromAccount();
                    if (account.equals(URLManager.APP_ACCID)) {
                        String content = message.getContent();


                        LogUtil.showELog(Tag, "新收到的消息message.getContent()>>>" + message.getContent());
                        setqmd(content);
                    } else {
                        if (!account.equals(MyData.ACCOUNT)) {
                            if (!MyData.ACCID.equals(account)) {
                                String messname = "";
                                // 聊天室中显示姓名
                                if (message.getChatRoomMessageExtension() != null) {
                                    messname = message.getChatRoomMessageExtension().getSenderNick();
                                } else {
                                    messname = message.getFromNick();
                                }
                             //   messageData.add(messname + ": " +  message.getContent());
                               // messageAdapter.NotifyAdapter(messageData);
                                messageAdapter.AddData(new PeopleComeBean(messname + ": " + message.getContent(),null,null,null));
                                lvmessage.setSelection(messageAdapter.getCount());
                            }
                        }
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
            lm.setHead(URLManager.head +head);
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
    /*
    {"colour":"purple","level":"3","count":"1","nickName":"小菜鸡","gid":"dianzan.png","head":"http://zb.zm-y.com/zmzb/head/20170202101924063567120","zDiamond":"5537"}
     */

    private void setqmd(String content) {
        try {
            LogUtil.showDLog("打赏成功>>>",""+content);
            JSONObject object = new JSONObject(content);
            String dsname = object.getString("nickName");
            String zDiamond = object.getString("zDiamond");
          //  String sDiamond = object.getString("sDiamond");
            String sPic = object.getString("head");
            audience_liwuimg.setVisibility(View.VISIBLE);
            audience_liwuimg.setImageResource(R.mipmap.avatar_def);
            Picasso.with(context).load(sPic).into(audience_liwuimg);
            if (intype) {
                getHandler().removeCallbacks(runnable);
            }
            getHandler().postDelayed(runnable, 2000);
            intype = false;
            MyData.PROFIT = zDiamond;
            audience_qinmidu.setText(getResources().getString(R.string.qinmi) + " " + zDiamond);
            showGift(dsname + "" + "", "系统", dsname + "打赏主播");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean intype = false;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            audience_liwuimg.setVisibility(View.GONE);
            intype = true;
        }
    };

    private static final int LIMIT = 1000;
    private long updateTime = 0; // 非游客的updateTime
    private long enterTime = 0; // 游客的enterTime

    private Map<String, ChatRoomMember> memberCache = new ConcurrentHashMap<>();
    private List<ChatRoomMember> items = new ArrayList<>();
    private boolean isNormalEmpty = false; // 固定成员是否拉取完

    private void fetchData() {
        LogUtil.showELog(Tag, "网易云信开始获取在线成员信息");
        if (!isNormalEmpty) {
//            // 拉取固定在线成员
//            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
            // 拉取非固定成员
            getMembers(MemberQueryType.GUEST, enterTime, 0);
        } else {
            // 拉取固定在线成员
            getMembers(MemberQueryType.ONLINE_NORMAL, updateTime, 0);
//            // 拉取非固定成员
//            getMembers(MemberQueryType.GUEST, enterTime, 0);
        }
    }

    /**
     * 获取成员列表
     */
    private void getMembers(final MemberQueryType memberQueryType, final long time, int limit) {
        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, memberQueryType, time, (LIMIT - limit)).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
            @Override
            public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                boolean success = code == ResponseCode.RES_SUCCESS;
                if (success) {
                    addMembers(result);
//                    if (memberQueryType == MemberQueryType.ONLINE_NORMAL && result.size() < LIMIT) {
//                        isNormalEmpty = true; // 固定成员已经拉完
//                        getMembers(MemberQueryType.GUEST, enterTime, result.size());
//                    }
                    if (memberQueryType == MemberQueryType.GUEST && result.size() < LIMIT) {
                        isNormalEmpty = true; // 固定成员已经拉完
                        getMembers(MemberQueryType.ONLINE_NORMAL, enterTime, result.size());
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

    private void stopRefreshing() {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addheaditem();
            }
        }, 50);
    }

    private void addheaditem() {
        LogUtil.showELog(Tag, "在线成员人数>>>>" + items.size());
        MainAudienceActivity.onlinenum = MainAudienceActivity.onlinenum + items.size();
        onlinepeople.setText(MainAudienceActivity.onlinenum + " 人");
        for (final ChatRoomMember member : items) {
            final String accid = member.getAccount();
            if (!accid.equals(MyData.ACCOUNT)) {
                LiveerMess lm = new LiveerMess();
                lm.setHead(member.getAvatar());
                lm.setAccid(accid);
                adapter.updatain(lm);
            }
        }
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
    private String ucid, head, liveername;
    /**
     * 界面相关
     */
    private LinearLayout llpicimage, llgiftcontent, llInputParent, audience_lwll, audience_messll;
    private RelativeLayout rlsentimenttime, tvChat, audience_bottomrl, audience_sixin, audience_liwu, audience_xin, audience_fenxiang;
    private RelativeLayout audience_liwuremen, audience_liwugds, audience_liwush, audience_liwudd;
    private TextView liwuremen_tv, liwugds_tv, liwush_tv, liwudd_tv, liwu_zwsj;
    private TextView sendInput, audience_name, onlinepeople, audience_guznzhu, audience_qinmidu, audience_id, bzye, lwfs;
    private HorizontalListView hlvaudience;
    private ListView lvmessage;
    private EditText etInput;
    private ImageView audience_liwuimg, audience_tuichus,audience_fximg,audience_gbfximg;
    private RoundedImageView audience_head;
    private GridView liwu_gridview;
    private PeriscopeLayout audience_periscope;

    private FlingListeber listener;
    private GestureDetector detector;
    private void findview(View view) {
        listener = new FlingListeber();
        detector = new GestureDetector(listener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        audience_messll = (LinearLayout) view.findViewById(R.id.audience_messll);//聊天父布局
        llpicimage = (LinearLayout) view.findViewById(R.id.audience_llpicimage);//在线人数父布局
        audience_tuichus = (ImageView) view.findViewById(R.id.audience_tuichus);//退出直播间
        rlsentimenttime = (RelativeLayout) view.findViewById(R.id.audience_rlsentimenttime);//亲密度父布局
        hlvaudience = (HorizontalListView) view.findViewById(R.id.audience_hlvaudience);//显示所有头像
        llgiftcontent = (LinearLayout) view.findViewById(R.id.audience_llgiftcontent);//礼物动画显示
        lvmessage = (ListView) view.findViewById(R.id.audience_lvmessage);//聊天显示
        tvChat = (RelativeLayout) view.findViewById(R.id.audience_tvChat);//发送聊天显示
        llInputParent = (LinearLayout) view.findViewById(R.id.audience_llinputparent);//聊天框父布局
        etInput = (EditText) view.findViewById(R.id.audience_etInput);//聊天输入框
        sendInput = (TextView) view.findViewById(R.id.audience_sendInput);//聊天发送按钮
        audience_head = (RoundedImageView) view.findViewById(R.id.audience_head);//主播头像
        audience_name = (TextView) view.findViewById(R.id.audience_name);//主播姓名
        onlinepeople = (TextView) view.findViewById(R.id.audience_onlinepeople);//直播间人数
        audience_guznzhu = (TextView) view.findViewById(R.id.audience_guznzhu);//关注主播
        audience_qinmidu = (TextView) view.findViewById(R.id.audience_qinmidu);//亲密度
        audience_id = (TextView) view.findViewById(R.id.audience_id);//直播间id
        audience_fximg = (ImageView) view.findViewById(R.id.audience_fximg);//推荐分享
        audience_fximg.setOnClickListener(this);
        audience_gbfximg = (ImageView) view.findViewById(R.id.audience_gbfximg);//推荐分享显示
        audience_gbfximg.setOnClickListener(this);
        audience_liwuimg = (ImageView) view.findViewById(R.id.audience_liwuimg);//礼物显示
        audience_bottomrl = (RelativeLayout) view.findViewById(R.id.audience_bottomrl);

        audience_periscope = (PeriscopeLayout) view.findViewById(R.id.audience_periscope);//点赞效果
        audience_sixin = (RelativeLayout) view.findViewById(R.id.audience_sixin);//私信
        audience_sixin.setOnClickListener(this);
        audience_liwu = (RelativeLayout) view.findViewById(R.id.audience_liwu);//礼物
        audience_liwu.setOnClickListener(this);
        audience_xin = (RelativeLayout) view.findViewById(R.id.audience_xin);//点赞
        audience_xin.setOnClickListener(this);
        audience_fenxiang = (RelativeLayout) view.findViewById(R.id.audience_fenxiang);//分享
        audience_fenxiang.setOnClickListener(this);

        tvChat.setOnClickListener(this);
        sendInput.setOnClickListener(this);
        audience_guznzhu.setOnClickListener(this);
        audience_tuichus.setOnClickListener(this);

        if (MyData.GZZT.equals("0")) {
            audience_guznzhu.setVisibility(View.VISIBLE);
        } else {
            audience_guznzhu.setVisibility(View.GONE);
        }
        audience_qinmidu.setText(getResources().getString(R.string.qinmi) + " " + MyData.LEVERPROFIT);
        audience_id.setText("ID " + ucid);
        if (!head.equals("")) {
            Picasso.with(context).load(head).error(R.mipmap.avatar_def).into(audience_head);
        }
        audience_name.setText(liveername);
        Picasso.with(context).load( URLManager.ads+MyData.Xiaopic).into(audience_fximg);
        audience_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityJumpControl.getInstance(activity).gotoUserMesActivity(ucid, "2");
            }
        });
    }

    private boolean liwu = false;
    private LwAdapter lwadapter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.audience_tuichus:/*退出*/
                dialogmess();
                break;
            case R.id.audience_tvChat:/*聊天*/
                showChat();
                break;
            case R.id.audience_sendInput:/*发送*/
                sendText();
                break;
            case R.id.audience_guznzhu://关注
                if (!MyData.USERID.equals("0")) {
                    if (MyData.GZZT.equals("0")) {
                        addConcern();
                    } else {
                    }
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.audience_sixin://发私信
                if (!MyData.USERID.equals("0")) {
                    ActivityJumpControl.getInstance(activity).gotoSixinzhuboActivity(ucid);
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.dialog_bzye:/*播钻充值*/
                String totle = bzye.getText().toString();
                ActivityJumpControl.getInstance(activity).gotoChongzhiActivity(totle);
                break;
            case R.id.audience_liwu://礼物

                if (!MyData.USERID.equals("0")) {
                    balance();
                    if (!liwu) {
                        dialogliwu();

                        liwu = true;
                    }

                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.dialog_audience_liwuremen://礼物热门
                changetv(liwuremen_tv);
                getGiftsList("hot");
                break;
            case R.id.dialog_audience_liwugds://礼物高大上
                changetv(liwugds_tv);
                getGiftsList("gds");
                break;
            case R.id.dialog_audience_liwush://礼物奢华
                changetv(liwush_tv);
                getGiftsList("sh");
                break;
            case R.id.dialog_audience_liwudd://礼物低调
                changetv(liwudd_tv);
                getGiftsList("dd");
                break;
            case R.id.dialog_audience_lwfs://礼物发送
                if (cansl) {
                    cansl = false;
                    if (!diamond.equals("") && !giftsid.equals("")) {
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cansl = true;
                            }
                        }, 1000);
                        try {
                            int d = Integer.parseInt(diamond);
                            if (d > dm) {
                                showMessage(getResources().getString(R.string.nullyue));
                                return;
                            }
                            giveGifts(giftsid);
                        } catch (Exception e) {
                            e.printStackTrace();
                            giveGifts(giftsid);
                        }
                    }
                } else {
                    showMessage(getResources().getString(R.string.czpf));
                }
                if (liwu) {
                    liwu = false;
                    diamond = "";
                    giftsid = "";
                    try {
                        dialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.audience_xin://点赞
                if (!MyData.USERID.equals("0")) {
                    audience_periscope.addHeart();
                    if (candz) {
                        candz = false;
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                candz = true;
                            }
                        }, 3000);
                        addPraise();
                    }
                } else {
                    showMessage(getResources().getString(R.string.wszchint));
                }
                break;
            case R.id.audience_fenxiang://分享
                if (canfenx) {
                    canfenx = false;
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canfenx = true;
                        }
                    }, 2000);
                    dialoshare();
                }
                break;
            case R.id.dialog_mess_quxiao:/*退出直播否*/
                dialog.dismiss();
                break;
            case R.id.dialog_mess_yes:/*退出直播 是*/
                overclose();
                dialog.dismiss();
                break;
            case R.id.audience_fximg:/*推荐分享*/
                if(fximgvis){
                    ActivityJumpControl.getInstance(activity).gotoFenxiangActivity();
                }
                break;
            case R.id.audience_gbfximg:/*推荐分享显示*/
                fximgvis=false;
                audience_fximg.setVisibility(View.GONE);
                audience_gbfximg.setVisibility(View.GONE);
                break;
        }
    }
    private boolean fximgvis=true;
    private boolean canfenx = true;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            LogUtil.showDLog("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                showMessage(platform + " 收藏成功啦");
            } else {
                showMessage(platform + " 分享成功啦");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            showMessage(platform + " 分享失败啦");
            if (t != null) {
                LogUtil.showDLog("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            showMessage(platform + " 分享取消了");
        }
    };

    private ArrayList<TextView> tvlist = new ArrayList<>();

    private void changetv(TextView tv) {//aa7E3AB3
        for (int i = 0; i < tvlist.size(); i++) {
            tvlist.remove(i).setBackgroundColor(Color.parseColor("#00000000"));
        }
        tv.setBackgroundColor(Color.parseColor("#aa7E3AB3"));
        tvlist.add(tv);
    }


    private void overclose() {
        ((MainAudienceActivity) context).audiencefinish();
    }

    private AlertDialog dialog;

    /**
     * dialog 分享
     */
    private void dialoshare() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
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
        p.width = (int) (d.getWidth()*1); // 宽度设置为屏幕的0.65
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
    /**
     * dialog 消息提示
     */
    private void dialogmess() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_mess);
        TextView dialog_mess_title = (TextView) dialog.findViewById(R.id.dialog_mess_title);
        TextView dialog_mess_con = (TextView) dialog.findViewById(R.id.dialog_mess_con);
        dialog_mess_title.setText("消息提示！");
        dialog_mess_con.setText("是否退出直播间?");
        dialog.findViewById(R.id.dialog_mess_quxiao).setOnClickListener(this);
        dialog.findViewById(R.id.dialog_mess_yes).setOnClickListener(this);
    }

    /**
     * dialog 选择礼物
     */
    private void dialogliwu() {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_liwu);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (ViewGroup.LayoutParams.WRAP_CONTENT); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth()*1); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);
        audience_lwll = (LinearLayout) dialog.findViewById(R.id.dialog_audience_lwll);//礼物父布局
        liwu_zwsj = (TextView) dialog.findViewById(R.id.dialog_liwu_zwsj);//礼物信息错误
        audience_liwuremen = (RelativeLayout) dialog.findViewById(R.id.dialog_audience_liwuremen);//礼物热门
        liwuremen_tv = (TextView) dialog.findViewById(R.id.dialog_liwuremen_tv);//礼物热门
        audience_liwuremen.setOnClickListener(this);
        audience_liwugds = (RelativeLayout) dialog.findViewById(R.id.dialog_audience_liwugds);//礼物高大上
        liwugds_tv = (TextView) dialog.findViewById(R.id.dialog_liwugds_tv);//礼物高大上
        audience_liwugds.setOnClickListener(this);
        audience_liwush = (RelativeLayout) dialog.findViewById(R.id.dialog_audience_liwush);//礼物奢华
        liwush_tv = (TextView) dialog.findViewById(R.id.dialog_liwush_tv);//礼物奢华
        audience_liwush.setOnClickListener(this);
        audience_liwudd = (RelativeLayout) dialog.findViewById(R.id.dialog_audience_liwudd);//礼物低调
        liwudd_tv = (TextView) dialog.findViewById(R.id.dialog_liwudd_tv);//礼物低调
        audience_liwudd.setOnClickListener(this);
        liwu_gridview = (GridView) dialog.findViewById(R.id.dialog_liwu_gridview);//礼物显示列表
        bzye = (TextView) dialog.findViewById(R.id.dialog_bzye);//播钻余额
        bzye.setOnClickListener(this);
        lwfs = (TextView) dialog.findViewById(R.id.dialog_audience_lwfs);//发送礼物
        lwfs.setOnClickListener(this);
        changetv(liwuremen_tv);

        lwlist = new ArrayList<>();

        lwlist.add(new LwMess("bao.png", "10", "包", ""));
        lwlist.add(new LwMess("dianzan.png", "10", "点赞", ""));
        lwlist.add(new LwMess("flower.png", "10", "鲜花", ""));
        lwlist.add(new LwMess("ganbei.png", "10", "干杯", ""));
        lwlist.add(new LwMess("hongbao.png", "10", "红包", ""));
        lwlist.add(new LwMess("huatong.png", "10", "话筒", ""));
        lwlist.add(new LwMess("jindan.png", "10", "金蛋", ""));
        lwlist.add(new LwMess("aaacar.gif", "10", "跑车", ""));
//        lwlist.add(new LwMess("0", "10", "喜欢你", ""));
//        lwlist.add(new LwMess("0", "10", "我爱你", ""));
//        lwlist.add(new LwMess("0", "10", "新年快乐", ""));
//        lwlist.add(new LwMess("0", "10", "糖", ""));
//        lwlist.add(new LwMess("0", "10", "飞机", ""));
//        lwlist.add(new LwMess("0", "10", "吻", ""));
//        lwlist.add(new LwMess("0", "10", "幸运草", ""));
//        lwlist.add(new LwMess("0", "10", "钻石", ""));
        liwu_gridview.setVisibility(View.VISIBLE);
        lwadapter = new LwAdapter(context, lwlist);
        liwu_gridview.setAdapter(lwadapter);



        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                liwu = false;
            }
        });
    }
    private boolean cansl = true;
    private boolean candz = true;

    /***
     * 添加关注
     */
    private void addConcern() {
        showloading();
        xhttp = new Xhttp(URLManager.addConcern);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("ucid", ucid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    String mess = obj.getString("meg");
                    showMessage(mess);
                    if (status.equals("1")) {
                        audience_guznzhu.setVisibility(View.GONE);
                        MyData.GZZT = "1";
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
     * 用户给主播打赏
     */
    private String diamond = "";
    private String giftsid = "";
    private void giveGifts(String giftsid) {
        showloading();
        xhttp = new Xhttp(URLManager.giveGifts);
        xhttp.add("uid", MyData.USERID);
        xhttp.add("zbid", ucid);
        xhttp.add("roomid", roomId); xhttp.add("count", 1+"");xhttp.add("gprice", 10+"");
        xhttp.add("gid", giftsid);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
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

    /***
     * 礼物集合
     */
    private ArrayList<LwMess> lwlist;

    private void getGiftsList(String type) {
        showloading();
        liwu_gridview.setVisibility(View.GONE);
        liwu_zwsj.setVisibility(View.GONE);
        xhttp = new Xhttp(URLManager.getGiftsList);
        xhttp.add("type", type);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                String mess = "";
                try {
                    JSONObject obj = new JSONObject(result);
                    mess = obj.getString("meg");
                    JSONArray giftsList = obj.getJSONArray("giftsList");
                    lwlist = new ArrayList<>();
//                    for (int i = 0; i < giftsList.length(); i++) {
//                        JSONObject gift = giftsList.getJSONObject(i);
//
//                        lwlist.add(new LwMess(gift.getString("id"), gift.getString("price"), gift.getString("name"), gift.getString("picture")));
//                    }


                 //   lwlist = new ArrayList<>();
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwlist.add(new LwMess("0", "10", "测试", ""));
//                    lwadapter = new LwAdapter(context, lwlist);
//                    liwu_gridview.setAdapter(lwadapter);


                    liwu_gridview.setVisibility(View.VISIBLE);
                    lwadapter = new LwAdapter(context, lwlist);
                    liwu_gridview.setAdapter(lwadapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    liwu_zwsj.setVisibility(View.VISIBLE);
                    liwu_zwsj.setText(mess);
                }
                dismissloading();
            }

            @Override
            public void onError() {
                liwu_zwsj.setVisibility(View.VISIBLE);
                dismissloading();
            }
        });
    }

    /***
     * 直播间里点赞
     */
    private void addPraise() {
        xhttp = new Xhttp(URLManager.addPraise);
        xhttp.add("yxid", account);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /***
     * 获取用户余额和收益
     */
    private int dm = 0;

    private void balance() {
        xhttp = new Xhttp(URLManager.balance);
        xhttp.add("uid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    dm = obj.getInt("diamond");
                    bzye.setText(dm + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                    bzye.setText("0");
                }
            }

            @Override
            public void onError() {
            }
        });
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

    private void getroommes() {
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RequestCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo param) {
                LogUtil.showDLog(Tag,"getroommes>>>>创建者帐号>>" + param.getCreator());
            }

            @Override
            public void onFailed(int code) {
                LogUtil.showDLog(Tag, "fetch room info failed:" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.showDLog(Tag, "fetch room info exception:" + exception);
            }
        });
    }

    /**
     * 显示礼物的方法
     */
    private void showGift(String tag, String leve, String name) {
        View giftView = llgiftcontent.findViewWithTag(tag);
        if (giftView == null) {/*该用户不在礼物显示列表*/
            if (llgiftcontent.getChildCount() > 2) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
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
            final TextView giftinlv = (TextView) giftView.findViewById(R.id.inlv);/*找到数量控件*/
            crvheadimage.setText(name);
            giftinlv.setText(leve);
            giftinlv.setText("x1");/*设置礼物数量*/    //ll
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/  //ll
            giftinlv.setTag(1);/*给数量控件设置标记*/

            llgiftcontent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            llgiftcontent.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                 giftNumAnim.start(giftinlv);//ll
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
            int showNum = (Integer) giftinlv.getTag() + 1;  //ll
            giftinlv.setText("x" + showNum);  //ll
            giftinlv.setTag(showNum);//ll
            crvheadimage.setTag(System.currentTimeMillis());
            giftNumAnim.start(giftinlv);//ll
        }
    }

    /**
     * 显示聊天布局
     */
    private void showChat() {
        audience_bottomrl.setVisibility(View.GONE);
        llInputParent.setVisibility(View.VISIBLE);
        if(fximgvis){
            audience_fximg.setVisibility(View.GONE);
            audience_gbfximg.setVisibility(View.GONE);
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
//                        if (code == ResponseCode.RES_CHATROOM_MUTED) {
//                            showMessage("用户被禁言");
//                        } else {
//                            showMessage("消息发送失败：code:" + code);
//                        }
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
                        showMessage(mess);
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
                audience_bottomrl.setVisibility(View.VISIBLE);
                llInputParent.setVisibility(View.GONE);
                if(fximgvis){
                    audience_fximg.setVisibility(View.VISIBLE);
                    audience_gbfximg.setVisibility(View.VISIBLE);
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

    private class LwAdapter extends BaseAdapter {
        private Context context;
        private int itemat = -1;
        private LayoutInflater minflater;
        private ArrayList<LwMess> lwlist;

        public LwAdapter(Context context, ArrayList<LwMess> lwlist) {
            super();
            this.minflater = LayoutInflater.from(context);
            this.lwlist = lwlist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return lwlist.size();
        }

        @Override
        public Object getItem(int position) {
            return lwlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = minflater.inflate(R.layout.audience_activity_item, null);
                holder = new ViewHolder();
                holder.liwuitem_img = (ImageView) convertView
                        .findViewById(R.id.liwuitem_img);
                holder.liwuitem_money = (TextView) convertView
                        .findViewById(R.id.liwuitem_money);
                holder.liwuitem_title = (TextView) convertView
                        .findViewById(R.id.liwuitem_title);
                holder.liwuitem = (LinearLayout) convertView
                        .findViewById(R.id.liwuitem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final LwMess lwmess = lwlist.get(position);
            String title = lwmess.getTitle();
            final String money = lwmess.getMoney();
            final String img = lwmess.getMipmapid();
            String pic = lwmess.getPic();
            holder.liwuitem_money.setText(money + "钻");
            holder.liwuitem_title.setText(title);
            //Picasso.with(context).load(URLManager.gifts + pic).into(holder.liwuitem_img);
            switch (position) {
                case 0:


                    Picasso.with(context).load(R.mipmap.bao).into(holder.liwuitem_img);


                    break;
                case 1:


                    Picasso.with(context).load(









































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































































                            R.mipmap.dianzan).into(holder.liwuitem_img);


                    break;
                case 2:


                    Picasso.with(context).load(R.mipmap.flower).into(holder.liwuitem_img);


                    break;
                case 3:

                    Picasso.with(context).load(R.mipmap.ganbei).into(holder.liwuitem_img);


                    break;
                case 4:


                    Picasso.with(context).load(R.mipmap.hongbao).into(holder.liwuitem_img);


                    break;
                case 5:


                    Picasso.with(context).load(R.mipmap.huatong).into(holder.liwuitem_img);


                    break;
                case 6:


                    Picasso.with(context).load(R.mipmap.jindan).into(holder.liwuitem_img);


                    break;
                case 7:


                    Picasso.with(context).load(R.mipmap.car).into(holder.liwuitem_img);


                    break;
                case 8:


                    Picasso.with(context).load(R.mipmap.love).into(holder.liwuitem_img);


                    break;
                case 9:


                    Picasso.with(context).load(R.mipmap.love1).into(holder.liwuitem_img);


                    break;
                case 10:


                    Picasso.with(context).load(R.mipmap.newyear).into(holder.liwuitem_img);


                    break;
                case 11:


                    Picasso.with(context).load(R.mipmap.tang).into(holder.liwuitem_img);


                    break;
                case 12:


                    Picasso.with(context).load(R.mipmap.wanliu).into(holder.liwuitem_img);


                    break;
                case 13:


                    Picasso.with(context).load(R.mipmap.wen).into(holder.liwuitem_img);


                    break;
                case 14:


                    Picasso.with(context).load(R.mipmap.xingyun).into(holder.liwuitem_img);


                    break;
                case 15:


                    Picasso.with(context).load(R.mipmap.zuan).into(holder.liwuitem_img);


                    break;

            }



             Picasso.with(context).load(R.mipmap.zuan).into(holder.liwuitem_img);


            holder.liwuitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diamond = money;
                    itemat = position;
                    giftsid = img;
                    notifyDataSetChanged();
                }
            });
            viewcolor(position, holder);
            return convertView;
        }

        private void viewcolor(int position, ViewHolder holder) {
            if (position == itemat) {
                holder.liwuitem.setBackgroundColor(Color.parseColor("#aadfafdd"));
            } else {
                holder.liwuitem.setBackgroundColor(Color.parseColor("#00000000"));

            }
        }

        class ViewHolder {
            private ImageView liwuitem_img;
            private LinearLayout liwuitem;
            private TextView liwuitem_money, liwuitem_title;
        }
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
                    gouplive();
                    LogUtil.showDLog("audience>>>>>", "FlingListeber>>向下滑");
                } else if (e1.getY() - e2.getY() > 30) {// 向上滑
                    gouplive();
                    LogUtil.showDLog("audience>>>>>", "FlingListeber>>向上滑");
                }
            }
            return false;
        }
    }
    public  void gouplive() {
        if (MainActivity.position == (MainActivity.liveerlist.size() - 1)) {
            MainActivity.position = 0;
        } else {
            MainActivity. position++;
        }
        if (MainActivity.position > MainActivity.liveerlist.size()||MainActivity.liveerlist.size()==0||MainActivity.liveerlist.size()==1) {
            return;
        }
        LiveerMess liveerMess = MainActivity.liveerlist.get(MainActivity.position);
        mucid = liveerMess.getId();
        final String profit = liveerMess.getProfit();
        String password = liveerMess.getPassword();
        liveer = liveerMess.getName();
        mhead = URLManager.head + liveerMess.getHead();
        final String cid = liveerMess.getCid();
        if (!password.equals("pass-null")) {
            inpasw(cid, profit, password);
        } else {
            if (hasjion) {
                hasjion = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        joinRoom(cid, profit, "");
                    }
                }, 600);
            }
        }
    }

    public void godownlive() {

    }
    /***
     * 进入直播
     */

    private boolean hasjion = true;
    private String liveer;
    private String mucid;
    private String mhead;

    private void joinRoom(final String cid, final String profit, String password) {
        showloading();
        xhttp = new Xhttp(URLManager.joinRoom);
        xhttp.add("cid", cid);
        if (!password.equals("")) {
            xhttp.add("password", password);
        }
        xhttp.add("gid", MyData.USERID);
        xhttp.post(new XhttpCallBack() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    hasjion = true;
                    if (status.equals("1")) {
                        String rtmpPullUrl = obj.getString("rtmpPullUrl");
                        String roomid = obj.getString("roomid");
                        MyData.GZZT = obj.getString("gzStatus");
                        MainActivity.roompath = rtmpPullUrl;
                        MainActivity.joinroomid = roomid;
                        MainActivity.ucid = mucid;
                        MyData.CID = cid;
                        MainActivity.liveername = liveer;
                        MainActivity.head = mhead;
                        MyData.LEVERPROFIT = profit;
                        ((MainAudienceActivity) context).requestBasicPermission(); // 申请APP基本权限
                    } else {
                        String mess = obj.getString("meg");
                        showMessage(mess);
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

    private EditText content;

    private void inpasw(final String cid, final String profit, final String password) {
        dialog = new AlertDialog.Builder(context,R.style.myDialog_style).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_inpaswo, null);
        dialog.setView(view);
        dialog.show();
        content = (EditText) dialog.findViewById(R.id.dialog_content);
        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ps = content.getText().toString().trim();
                joinRoom(cid, profit, ps);
                dialog.dismiss();
            }
        });
    }
}