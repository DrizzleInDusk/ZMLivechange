package com.android.zmlive.tool;

import android.app.Activity;
import android.content.Intent;

import com.android.zmlive.activity.BangzhufkActivity;
import com.android.zmlive.activity.BuildLiveActivity;
import com.android.zmlive.activity.CaiwuGlActivity;
import com.android.zmlive.activity.ChongzhiActivity;
import com.android.zmlive.activity.ChooseLoginActivity;
import com.android.zmlive.activity.DaoFUskActivity;
import com.android.zmlive.activity.FensigxActivity;
import com.android.zmlive.activity.FenxiangActivity;
import com.android.zmlive.activity.ForgetPassActivity;
import com.android.zmlive.activity.GuanzhuActivity;
import com.android.zmlive.activity.HezuozxActivity;
import com.android.zmlive.activity.HongliActivity;
import com.android.zmlive.activity.HongliGlActivity;
import com.android.zmlive.activity.JubaoActivity;
import com.android.zmlive.activity.LoginActivity;
import com.android.zmlive.activity.LoginCodeActivity;
import com.android.zmlive.activity.MainActivity;
import com.android.zmlive.activity.MainAudienceActivity;
import com.android.zmlive.activity.MainLiveActivity;
import com.android.zmlive.activity.OverLiveActivity;
import com.android.zmlive.activity.RegistActivity;
import com.android.zmlive.activity.RexieyiActivity;
import com.android.zmlive.activity.ShenfenrzActivity;
import com.android.zmlive.activity.ShezhiActivity;
import com.android.zmlive.activity.ShezhidlmmActivity;
import com.android.zmlive.activity.ShezhifwtkActivity;
import com.android.zmlive.activity.ShezhijymmActivity;
import com.android.zmlive.activity.ShezhishgyActivity;
import com.android.zmlive.activity.ShezhiGywmActivity;
import com.android.zmlive.activity.ShezhiHmdActivity;
import com.android.zmlive.activity.ShezhiLxwmActivity;
import com.android.zmlive.activity.ShezhiMmglActivity;
import com.android.zmlive.activity.ShezhiTxActivity;
import com.android.zmlive.activity.ShezhiyszcActivity;
import com.android.zmlive.activity.ShipinbofangActivity;
import com.android.zmlive.activity.ShipinzhongxinActivity;
import com.android.zmlive.activity.ShouzhimxActivity;
import com.android.zmlive.activity.SixinzhuboActivity;
import com.android.zmlive.activity.SousuoActivity;
import com.android.zmlive.activity.TianjiashActivity;
import com.android.zmlive.activity.TixianActivity;
import com.android.zmlive.activity.TixianBdActivity;
import com.android.zmlive.activity.UserMesActivity;
import com.android.zmlive.activity.WebViewActivity;
import com.android.zmlive.activity.WodeMessActivity;
import com.android.zmlive.activity.WodeSixinActivity;
import com.android.zmlive.activity.XiezhuActivity;
import com.android.zmlive.activity.XiezhuglActivity;
import com.android.zmlive.activity.XiezhuyyActivity;
import com.android.zmlive.activity.XiezhuzhiboActivity;
import com.android.zmlive.activity.XiugaiqmActivity;
import com.android.zmlive.activity.XiugaiziliaoActivity;
import com.android.zmlive.activity.XuanzehuatiActivity;
import com.android.zmlive.activity.XuanzhezhifuActivity;
import com.android.zmlive.activity.YaoqingmabdActivity;
import com.android.zmlive.activity.YijianfkActivity;
import com.android.zmlive.activity.ZhuboxieyiActivity;

import java.util.ArrayList;

/**
 * 所有页面跳转总控制
 */
public class ActivityJumpControl {
    private static ActivityJumpControl mInstance = null;
    private Activity mOwner = null;
    private ArrayList<Activity> mArrayList;

    private ActivityJumpControl(Activity owner) {
        mArrayList = new ArrayList<Activity>();
        mOwner = owner;
    }

    public static ActivityJumpControl getInstance(Activity owner) {
        if (mInstance == null) {
            mInstance = new ActivityJumpControl(owner);
        } else {
            mInstance.mOwner = owner;
        }
        return mInstance;
    }

    /**********************************************************************************
     * 全局设置使用
     ********************************************************************************/

    // 管理用户窗口和回退事件处理
    public void pushActivity(Activity activity) {
        mArrayList.add(activity);
    }

    public void popActivity(Activity activity) {
        mArrayList.remove(activity);
        activity = null;
    }

    public void popAllActivity() {
        while (mArrayList.size() > 0) {
            Activity ac = mArrayList.get(mArrayList.size() - 1);
            ac.finish();
            popActivity(ac);
        }
    }

    /**
     * 选择登陆方式
     */
    public void gotoChooseLoginActivity() {
        Intent intent = new Intent(mOwner, ChooseLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 邀请码登陆
     */
    public void gotoLoginCodeActivity() {
        Intent intent = new Intent(mOwner, LoginCodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 登陆
     */
    public void gotoLoginActivity() {
        Intent intent = new Intent(mOwner, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 注册
     */
    public void gotoRegistActivity() {
        Intent intent = new Intent(mOwner, RegistActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 注册协议
     */
    public void gotoRexieyiActivity() {
        Intent intent = new Intent(mOwner, RexieyiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     *  WebView
     */
    public void gotoWebViewActivity(String url) {
        Intent intent = new Intent(mOwner, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url",url);
        mOwner.startActivity(intent);
    }
    /**
     * 忘记密码
     */
    public void gotoForgetPassActivity() {
        Intent intent = new Intent(mOwner, ForgetPassActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 主页
     */
    public void gotoMainActivity() {
        Intent intent = new Intent(mOwner, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 设置
     */
    public void gotoShezhiActivity() {
        Intent intent = new Intent(mOwner, ShezhiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 提醒设置
     */
    public void gotoShezhiTxActivity() {
        Intent intent = new Intent(mOwner, ShezhiTxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 黑名单
     */
    public void gotoShezhiHmdActivity() {
        Intent intent = new Intent(mOwner, ShezhiHmdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 联系我们
     */
    public void gotoShezhiLxwmActivity() {
        Intent intent = new Intent(mOwner, ShezhiLxwmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 关于我们
     */
    public void gotoShezhiGywmActivity() {
        Intent intent = new Intent(mOwner, ShezhiGywmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 密码管理
     */
    public void gotoShezhiMmglActivity() {
        Intent intent = new Intent(mOwner, ShezhiMmglActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 社会公约
     */
    public void gotoShezhishgyActivity() {
        Intent intent = new Intent(mOwner, ShezhishgyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 隐私政策
     */
    public void gotoShezhiyszcActivity() {
        Intent intent = new Intent(mOwner, ShezhiyszcActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 服务条款
     */
    public void gotoShezhifwtkActivity() {
        Intent intent = new Intent(mOwner, ShezhifwtkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 登录密码
     */
    public void gotoShezhidlmmActivity() {
        Intent intent = new Intent(mOwner, ShezhidlmmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 交易密码
     */
    public void gotoShezhijymmActivity() {
        Intent intent = new Intent(mOwner, ShezhijymmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 关注管理
     */
    public void gotoGuanzhuActivity(String tag) {
        Intent intent = new Intent(mOwner, GuanzhuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tag",tag);
        mOwner.startActivity(intent);
    }
    /**
     * 修改资料
     */
    public void gotoXiugaiziliaoActivity() {
        Intent intent = new Intent(mOwner, XiugaiziliaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 修改签名
     */
    public void gotoXiugaiqmActivity(String qianming) {
        Intent intent = new Intent(mOwner, XiugaiqmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("signature",qianming);
        mOwner.startActivity(intent);
    }
    /**
     * 帮助反馈
     */
    public void gotoBangzhufkActivity() {
        Intent intent = new Intent(mOwner, BangzhufkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 意见反馈
     */
    public void gotoYijianfkActivity() {
        Intent intent = new Intent(mOwner, YijianfkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 合作中心
     */
    public void gotoHezuozxActivity() {
        Intent intent = new Intent(mOwner, HezuozxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 视频中心
     */
    public void gotoShipinzhongxinActivity() {
        Intent intent = new Intent(mOwner, ShipinzhongxinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 视频播放
     */
    public void gotoShipinbofangActivity(String url,String filename) {
        Intent intent = new Intent(mOwner, ShipinbofangActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url",url);//還沒有會話
        intent.putExtra("filename",filename);
        mOwner.startActivity(intent);
    }
    /**
     * 身份认证
     */
    public void gotoShenfenrzActivity() {
        Intent intent = new Intent(mOwner, ShenfenrzActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 主播协议
     */
    public void gotoZhuboxieyiActivity() {
        Intent intent = new Intent(mOwner, ZhuboxieyiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 协助
     */
    public void gotoXiezhuActivity() {
        Intent intent = new Intent(mOwner, XiezhuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 协助管理
     */
    public void gotoXiezhuglActivity() {
        Intent intent = new Intent(mOwner, XiezhuglActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 协助管理-预约列表
     */
    public void gotoXiezhuyyActivity() {
        Intent intent = new Intent(mOwner, XiezhuyyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 协助管理-直播申请
     */
    public void gotoXiezhuzhiboActivity() {
        Intent intent = new Intent(mOwner, XiezhuzhiboActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 添加守护
     */
    public void gotoTianjiashActivity() {
        Intent intent = new Intent(mOwner, TianjiashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 邀请码补登
     */
    public void gotoYaoqingmabdActivity() {
        Intent intent = new Intent(mOwner, YaoqingmabdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }

    /**
     * 搜索
     */
    public void gotoSousuoActivity() {
        Intent intent = new Intent(mOwner, SousuoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 红利
     */
    public void gotoHongliActivity() {
        Intent intent = new Intent(mOwner, HongliActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 红利管理
     */
    public void gotoHongliGlActivity() {
        Intent intent = new Intent(mOwner, HongliGlActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 提现绑定
     * @param totle 余额
     */
    public void gotoTixianBdActivity(String mode,String totle) {
        Intent intent = new Intent(mOwner, TixianBdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("mode", mode);
        intent.putExtra("totle", totle);
        mOwner.startActivity(intent);
    }
    /**
     * 提现
     */
    public void gotoTixianActivity(String mode,String type,String totle,String bankalipay) {
        Intent intent = new Intent(mOwner, TixianActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("mode", mode);
        intent.putExtra("type", type);
        intent.putExtra("totle", totle);
        intent.putExtra("bankalipay", bankalipay);
        mOwner.startActivity(intent);
    }
    /**
     * 我的消息
     */
    public void gotoWodemessActivity() {
        Intent intent = new Intent(mOwner, WodeMessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 我的消息
     */
    public void gotoWodeSixinActivity() {
        Intent intent = new Intent(mOwner, WodeSixinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 财务管理
     */
    public void gotoCaiwuGlActivity() {
        Intent intent = new Intent(mOwner, CaiwuGlActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 充值
     */
    public void gotoChongzhiActivity(String bzye) {
        Intent intent = new Intent(mOwner, ChongzhiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("bzye", bzye);
        mOwner.startActivity(intent);
    }
    /**
     * 选择充值方式
     */
    public void gotoXuanzhezhifuActivity(String czmon) {
        Intent intent = new Intent(mOwner, XuanzhezhifuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("czmon", czmon);
        mOwner.startActivity(intent);
    }
    /**
     * 到府收款
     */
    public void gotoDaoFUskActivity(String czmon) {
        Intent intent = new Intent(mOwner, DaoFUskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("czmon", czmon);
        mOwner.startActivity(intent);
    }
    /**
     * 收支明细
     */
    public void gotoShouzhimxActivity() {
        Intent intent = new Intent(mOwner, ShouzhimxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 粉丝贡献榜
     */
    public void gotoFensigxActivity() {
        Intent intent = new Intent(mOwner, FensigxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 查看用户信息
     */
    public void gotoUserMesActivity(String uid,String type) {
        Intent intent = new Intent(mOwner, UserMesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("yxid", uid);
        intent.putExtra("type", type);
        mOwner.startActivity(intent);
    }
    public void gotoUserMesActivity(String yxid,String type,String roomId) {
        Intent intent = new Intent(mOwner, UserMesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("yxid", yxid);
        intent.putExtra("type", type);
        intent.putExtra("roomId", roomId);
        mOwner.startActivity(intent);
    }
    /**
     * 举报
     */
    public void gotoJubaoActivity(String yxid) {
        Intent intent = new Intent(mOwner, JubaoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("yxid", yxid);
        mOwner.startActivity(intent);
    }
    /**
     * 创建直播
     */
    public void gotoBuildLiveActivity() {
//        Intent intent = new Intent(mOwner, LiveActivity3.class);
        Intent intent = new Intent(mOwner, BuildLiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 选择话题
     */
    public void gotoXuanzehuatiActivity() {
        Intent intent = new Intent(mOwner, XuanzehuatiActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 直播
     */
    public void gotoLiveActivity(String url,String roomid) {
        Intent intent = new Intent(mOwner, MainLiveActivity.class);
//        Intent intent = new Intent(mOwner, LiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("mediaPath", url);
        intent.putExtra("ROOM_ID", roomid);
        intent.putExtra("videoResolution", "SD");
//        intent.putExtra("videoResolution", "HD");
        intent.putExtra("filter", true);
        mOwner.startActivity(intent);
    }
    /**
     * 直播结束
     */
    public void gotoOverLiveActivity(String onlinepeo,String onlinefx) {
        Intent intent = new Intent(mOwner, OverLiveActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("onlinepeo", onlinepeo);
        intent.putExtra("onlinefx", onlinefx);
        mOwner.startActivity(intent);
    }
    /**
     * 观看直播
     */
    public void gotoAudienceActivity(String url,String roomid,String ucid,String head,String liveername) {
        Intent intent = new Intent(mOwner, MainAudienceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXTRA_URL", url);
        intent.putExtra("ucid", ucid);
        intent.putExtra("head", head);
        intent.putExtra("liveername", liveername);
        intent.putExtra("ROOM_ID", roomid);
        mOwner.startActivity(intent);
    }
    /**
     * 分享
     */
    public void gotoFenxiangActivity() {
        Intent intent = new Intent(mOwner, FenxiangActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mOwner.startActivity(intent);
    }
    /**
     * 私信主播
     */
    public void gotoSixinzhuboActivity(String zbid) {
        Intent intent = new Intent(mOwner, SixinzhuboActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("zbid", zbid);
        mOwner.startActivity(intent);
    }


}
