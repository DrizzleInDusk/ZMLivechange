package com.android.zmlive.tool;


public class URLManager {
    public static final String APP_ACCID = "zmzb2016zmlcb";
    public static final String ZBAPPID = "1827e0440df34adf9a1a457a46ef5dad";
    public static final String YXAPPID = "3fed746995cb046aee3f132950760b98";
    public static final String QQAPPID = "1105672855";
    public static final String QQAPPSECRET = "lTYQGXJiqtWnWfIq";
    public static final String WXAPPID = "wx908ce783634f826d";
    public static final String WXAPPSECRET = "ad4be238d238b4757c15367d0743988b";
    public static final String LINEID = "1486299663";
    public static final String LINESecret = "2e4b904c5495970297f61ff0f4d7a6e4";
    public static final String FACKBOOKID = "686369434861961";
    public static final String MOBAPPKEY = "196f85516019c";
    public static final String MOBSECRET = "b17eae2680ca2e2c049568d111d77348";

    public static final String shareurl = "http://mzb.zmkjgame.com/dp/dPage.do";

public static final String APP_SERVER_URL = "http://mzb.zmkjgame.com/";
    //	  public static final String APP_SERVER_URL = "http://192.168.125.138:8080/zmzb/";

    //轮播图路径
    public static final String ads = APP_SERVER_URL + "zmzb/ads/";
    //头像路径
    public static final String head = APP_SERVER_URL + "zmzb/head/";
    //视频封面路径
    public static final String cover = APP_SERVER_URL + "zmzb/cover/";
    //视频播放路径
    public static final String video = APP_SERVER_URL + "zmzb/video/";
    //礼物路径
    public static final String gifts = APP_SERVER_URL + "zmzb/gifts/";

    /**
     * 发送验证码
     * mobile=13933448146
     */
    public static final String sendCode = APP_SERVER_URL + "user/sendCode";
    /**
     * 忘记密码
     * mobile=13933448146
     * password=111111
     * yzCode=894617
     */
    public static final String forgetPassword = APP_SERVER_URL + "user/forgetPassword";
    /**
     * 手机注册
     * mobile=13933448146
     * password=11111
     * yzCode=894617
     * city=迁安
     * sex=男
     */
    public static final String register = APP_SERVER_URL + "user/register";
    /**
     * 获取地区列表
     * pid=0  地区id
     */
    public static final String getAreas = APP_SERVER_URL + "areas/get AreasListByPid";
    /**
     * 注册用户登录
     * mobile=13933448146
     * password=11111
     */
    public static final String login = APP_SERVER_URL + "user/login";
    /**
     * 邀请码登录
     * code=2bgfooQn
     */
    public static final String loginByCode = APP_SERVER_URL + "user/loginByCode";
    /**
     * 开启直播
     * uid=登陆id
     * cname=直播间标题
     * city=定位城市
     * password=不是必选项
     * topic=不是必选项
     */
    public static final String creatRoom = APP_SERVER_URL + "room/creatRoom";
    /**
     * 主播开启直播后添加机器人
     * roomid=聊天室的id
     */
    public static final String addRobot = APP_SERVER_URL + "user/addRobot";
    /**
     * 直播间的分享图片：
     */
    public static final String appShare = APP_SERVER_URL + "about/appShare";
    /**
     * 礼物集合
     * type=hot热门，type=gds高大上，type=sh奢华，type=dd低调
     */
    public static final String getGiftsList = APP_SERVER_URL + "gc/getGiftsListByType";
    /**
     * 进入直播
     * cid=房间id
     * gid=登陆id
     * password=密碼
     */
    public static final String joinRoom = APP_SERVER_URL + "room/joinRoom";
    /**
     * 关闭直播
     * cid=房间id
     */
    public static final String closeRoom = APP_SERVER_URL + "room/closeRoom";
    /**
     * 退出直播间：
     * uid=登陆id
     */
    public static final String quitRoom = APP_SERVER_URL + "room/quitRoom";
    /**
     * 私信主播（就是预约）
     * uid=yonghuid
     * btime=shijian
     * place=didian
     * money=qian
     * zbid=zhuboid
     */
    public static final String yyzb = APP_SERVER_URL + "bc/yyzb";
    /**
     * 获取用户信息：
     * yxid=主播的id
     * gid=登陆的id
     */
    public static final String userInfo = APP_SERVER_URL + "user/userInfo";
    /**
     * 获取用户信息：
     * uid=主播的id
     * gid=登陆的id
     */
    public static final String userInfo2 = APP_SERVER_URL + "user/userInfo2";
    /**
     * 添加关注
     * uid  登录用户的id
     * ucid需要关注的用户id
     */
    public static final String addConcern = APP_SERVER_URL + "cc/addConcern";
    /**
     * 直播名片列表
     * place=index&city=迁安&uid=11
     * place=index首页  place=newest最新   place=city同城（需要city参数） place=concern关注（需要uid参数）
     */
    public static final String indexNameCard = APP_SERVER_URL + "nc/nameCard";
    /**
     * 搜索（用户ID，用户昵称，直播间名称）
     * keyword=可
     */
    public static final String searchNameCard = APP_SERVER_URL + "nc/searchNameCard";
    /**
     * 守护或者场管同意主播进行直播
     * cid=直播间id
     */
    public static final String agree = APP_SERVER_URL + "room/agree";
    /**
     * 轮播图
     * place=index首页，place=city同城。
     */
    public static final String getAdsListByPlace = APP_SERVER_URL + "adsc/getAdsListByPlace";
    /**
     * 更新用户信息
     * id=10
     * nickName=赫赫
     * sex=男
     * city=唐山
     * height=170
     * age=22
     * weight=130
     * bust=D
     * head=file
     * signature=签名
     */
    public static final String updateUserInfo = APP_SERVER_URL + "user/updateUserInfo";
    /**
     * 我的关注列表
     * uid=10
     */
    public static final String myConcern = APP_SERVER_URL + "cc/myConcern";
    /**
     * 我的粉丝列表
     * uid=10
     */
    public static final String myFans = APP_SERVER_URL + "cc/myFans";
    /**
     * 删除我的关注：
     * str=2,3,4
     */
    public static final String delMyConcern = APP_SERVER_URL + "cc/delMyConcern";
    /**
     * 协助：
     * uid=10
     * type= sh守护列表     cg场管列表
     */
    public static final String assistList = APP_SERVER_URL + "user/assistList";
    /**
     * 协助-场管-搜索：
     * keyword=搜索内容
     */
    public static final String searchUserByCG = APP_SERVER_URL + "user/searchUserByCG";
    /**
     * 添加守护--搜索
     * keyword=2222
     */
    public static final String searchUserForApp = APP_SERVER_URL + "user/searchUserForApp";
    /**
     * 主播设置守护
     * uid=用户id
     * gid=设置成守护的id
     */
    public static final String applyGuard = APP_SERVER_URL + "guard/applyGuard";
    /**
     * 邀请码补登：XqpzS2rb
     * uid=10
     * code=kagd238da
     */
    public static final String codeSupplement = APP_SERVER_URL + "user/codeSupplement";
    /**
     * 合作中心：
     * name=adh
     * areas=alhd
     * phone=1861261269
     * description=akldhakldhadhwui
     */
    public static final String cooperation = APP_SERVER_URL + "cooc/cooperation";
    /**
     * 设置-提醒设置
     * uid=10
     * type=0全开   type=1官方消息   type=2开播提醒   type=3全关    默认type=0
     */
    public static final String remindSet = APP_SERVER_URL + "user/remindSet";
    /**
     * 进入我的红利
     * uid=10
     */
    public static final String myDividend = APP_SERVER_URL + "user/myDividend";
    /**
     * 我的团队
     * uid=10
     * type=1一级队员   type=2二级队员   type=3三级队员
     */
    public static final String memberList = APP_SERVER_URL + "user/memberList";
    /**
     * 红利管理--收益额
     * uid=用户id
     */
    public static final String profit = APP_SERVER_URL + "ggc/profit";
    /**
     * 预约主播列表信息
     * gid=用户id
     */
    public static final String yyList = APP_SERVER_URL + "bc/yyList";
    /**
     * 每日签到：
     * uid=用户id
     */
    public static final String sign = APP_SERVER_URL + "user/sign";
    /**
     * 建议反馈：
     * uid=用户id
     * pic=file图片
     * name=姓名
     * content=具体内容
     * contact=联系方式
     */
    public static final String feedback = APP_SERVER_URL + "fbc/feedback";
    /**
     * 身份认证：
     * id=用户id
     * pic=图片file集合
     * code=证件号码
     * name=真实姓名
     * type= card 身份证   passport 护照
     */
    public static final String auth = APP_SERVER_URL + "user/auth";
    /**
     * 用户给主播打赏：
     * uid=用户id
     * zbid=主播id
     * giftsid=礼物id
     * roomid=聊天室id
     */
    public static final String giveGifts = APP_SERVER_URL + "ggc/giveGifts";
    /**
     * 视频中心：
     * type=time 按时间  num 按热度
     */
    public static final String appVideoList = APP_SERVER_URL + "vc/appVideoList";
    /**
     * 点击视频：
     * vid=22
     */
    public static final String updateNum = APP_SERVER_URL + "vc/updateNum";
    /**
     * 提现帐号绑定：
     * uid=用户id
     * type=alipay 绑定支付宝	 bank 绑定银行卡
     * account=绑定帐号
     * pname=姓名
     */
    public static final String binding = APP_SERVER_URL + "user/binding";
    /**
     * 进入提现页面：
     * uid=用户id
     */
    public static final String totm = APP_SERVER_URL + "user/totm";
    /**
     * 提现：
     * uid=用户id
     * type=提现类型 alipay 用支付宝提现  bank  用银行卡提现
     * mode=提现方式  ye 提现余额   sy  提现收益
     * money=提现多少
     * password=提现密码
     */
    public static final String atm = APP_SERVER_URL + "tmc/atm";
    /**
     * 充值记录：
     * uid=用户id
     */
    public static final String payRecord = APP_SERVER_URL + "pr/payRecord";
    /**
     * 提现记录：
     * uid=用户id
     */
    public static final String tmList = APP_SERVER_URL + "tmc/tmList";
    /**
     * 送礼榜：
     * uid=用户id
     */
    public static final String ggRankList = APP_SERVER_URL + "ggc/ggRankList";
    /**
     * 分享榜：
     * uid=用户id
     */
    public static final String fxRankList = APP_SERVER_URL + "ggc/fxRankList";
    /**
     * 收到的礼物：
     * uid=用户id
     */
    public static final String receivedliwu = APP_SERVER_URL + "ggc/received";
    /**
     * 送出的礼物：
     * uid=用户id
     */
    public static final String sendliwu = APP_SERVER_URL + "ggc/send";
    /**
     * 关于我们，联系我们：
     */
    public static final String appAbout = APP_SERVER_URL + "about/appAbout";
    /**
     * 帮助问答：
     */
    public static final String helpList = APP_SERVER_URL + "help/helpList";
    /**
     * 修改登录或者支付密码：
     * uid=用户id
     * opass=原始密码
     * npass=新密码
     * cpass=确认新密码
     * type=密码类型   lo 登录密码  tr 支付密码
     */
    public static final String editPassword = APP_SERVER_URL + "user/editPassword";
    /**
     * 设置支付密码：
     * uid=用户id
     * npass=新密码
     * cpass=确认新密码
     */
    public static final String setTRPassword = APP_SERVER_URL + "user/setTRPassword";
    /**
     * 进入密码管理：（判断支付密码有没有）
     * uid=用户id
     */
    public static final String joinPM = APP_SERVER_URL + "user/joinPM";
    /**
     * 协助里发送消息：
     * from=发送消息人的云信id
     * to=接收消息人的云信id
     * content=消息内容
     * 云信id=用户的code+mobile，要求字母小写
     */
    public static final String mmsendmessage = APP_SERVER_URL + "user/sendMessage";
    /**
     * 系统消息：
     * uid=用户id
     */
    public static final String megList = APP_SERVER_URL + "push/megList";
    /**
     * 申请直播的列表：
     * uid=用户id
     */
    public static final String applyList = APP_SERVER_URL + "room/applyList";
    /**
     * 用户预约主播的回复：
     * bid=记录的id
     * content=回复的内容
     */
    public static final String reply = APP_SERVER_URL + "bc/reply";
    /**
     * 直播间里举报：
     * uid=用户id
     * pic=file图片
     * name=举报标题
     * content=具体内容
     * contact=联系方式
     * yxid=云信id
     */
    public static final String report = APP_SERVER_URL + "fbc/report";
    /**
     * 聊天室发送消息之前调用（敏感词过滤）：
     * word=消息内容
     */
    public static final String checkSW = APP_SERVER_URL + "sw/checkSW";
    /**
     * 核实付款：
     * orders=订单id
     * uid=用户id
     * money=多少钱
     */
    public static final String paypal = APP_SERVER_URL + "pr/paypal";
    /**
     * 获取用户余额和收益：
     * uid=用户id
     */
    public static final String balance = APP_SERVER_URL + "user/balance";
    /**
     * 是否身份认证：//0没有权限  1有权限
     * uid=用户id
     */
    public static final String jumpAuth = APP_SERVER_URL + "user/jumpAuth";
    /**
     * 话题
     */
    public static final String appSearchList = APP_SERVER_URL + "search/appSearchList";
    /**
     * 直播间里点赞
     * yxid=主播的云信id
     */
    public static final String addPraise = APP_SERVER_URL + "user/addPraise";
    /**
     * 点击邀请码补登
     * uid=用户id
     */
    public static final String authCode = APP_SERVER_URL + "user/authCode";
    /**
     * 提醒设置状态
     * uid=用户id
     */
    public static final String getRemind = APP_SERVER_URL + "user/getRemind";
    /**
     * 到府收款
     * uid=用户id
     * money=多少钱
     * place=上门取款地址
     * stime=上门取款时间
     * name=姓名
     * mobile=联系方式
     */
    public static final String theDoor = APP_SERVER_URL + "pr/theDoor";
}
