package com.android.zmlive.fragment.liveing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainLiveActivity;
import com.android.zmlive.liveutils.AlertService;
import com.android.zmlive.liveutils.OpenGLSurfaceView;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.utils.BaseFragment;
import com.android.zmlive.tool.utils.LogUtil;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMessageHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.co.cyberagent.android.gpuimage.GPUImageFaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * 该Fragment用于对直播或观看直播的初始化
 * 直播的控件的创建以及销毁等等都可以在这里进行操作，这样就与我们自己的交互代码分离了
 */
public class LiveViewFragment extends BaseFragment implements lsMessageHandler {
    //伴音开关Receiver
    private MsgReceiver msgReceiver;
    //伴音音量Receiver
    private audioMixVolumeMsgReceiver audioMixVolumeMsgReceiver;
    //intent推流视频大小
    private int mVideoPreviewWidth, mVideoPreviewHeight;
    //intent数据
    private String mliveStreamingURL = null, mVideoResolution = null;
    private boolean mUseFilter = false;
    //状态变量
    private boolean m_liveStreamingOn = false, m_liveStreamingInit = false, m_liveStreamingInitFinished = false,
            m_tryToStopLivestreaming = false, m_startVideoCamera = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_liveview, container, false);
        //动态注册广播接收器，接收Service的消息
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("AudioMix");
        context.registerReceiver(msgReceiver, intentFilter);

        //动态注册广播接收器，接收Service的消息
        audioMixVolumeMsgReceiver = new audioMixVolumeMsgReceiver();
        IntentFilter audioMixVolumeIntentFilter = new IntentFilter();
        audioMixVolumeIntentFilter.addAction("AudioMixVolume");
        context.registerReceiver(audioMixVolumeMsgReceiver, audioMixVolumeIntentFilter);
        //应用运行时，保持屏幕高亮，不锁屏
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getintent(contentView);

        return contentView;
    }
    private String roomId;
    //获取数据
    private void getintent(View v) {
        roomId = ((MainLiveActivity) context).getRoomId();
        mliveStreamingURL = activity.getIntent().getStringExtra("mediaPath");
        mVideoResolution = activity.getIntent().getStringExtra("videoResolution");
        mUseFilter = activity.getIntent().getBooleanExtra("filter", true);
        if (mVideoResolution.equals("HD")) {
            mVideoPreviewWidth = 1280;
            mVideoPreviewHeight = 720;
        } else if (mVideoResolution.equals("SD")) {
            mVideoPreviewWidth = 640;
            mVideoPreviewHeight = 480;
        } else {
            mVideoPreviewWidth = 320;
            mVideoPreviewHeight = 240;
        }
        m_liveStreamingOn = false;
        m_tryToStopLivestreaming = false;

        //伴音相关操作，获取设备音频播放service
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        //拷贝MP3文件到APP目录
//        handleMP3();
        //创建直播
        buildliveview(v);
    }

    //滤镜模式的view
    private OpenGLSurfaceView mSurfaceView;
    private TextView gl_tv;

    private void buildliveview(View v) {
        //1、创建直播实例
        mLSMediaCapture = new lsMediaCapture(this, context, mVideoPreviewWidth, mVideoPreviewHeight, mUseFilter);
        //2、设置直播参数
        paraSet();
        //3、发送统计数据到网络信息界面
        //4、根据是否使用滤镜模式和推流方式（音视频或者音频或者视频）选择surfaceview类型
        mSurfaceView = (OpenGLSurfaceView) v.findViewById(R.id.liveview_surfaceview);
        //5、设置日志级别和日志文件路径
        //6、设置视频预览参数
        mSurfaceView.setPreviewSize(mVideoPreviewWidth, mVideoPreviewHeight);
        if (mLSMediaCapture != null) {
            boolean ret = false;
            //7、开启视频预览
            mLSMediaCapture.startVideoPreviewOpenGL(mSurfaceView, mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition);
            m_startVideoCamera = true;
            //8、初始化直播推流
            ret = mLSMediaCapture.initLiveStream(mliveStreamingURL, mLSLiveStreamingParaCtx);
            if (ret) {
                m_liveStreamingInit = true;
                m_liveStreamingInitFinished = true;
            } else {
                m_liveStreamingInit = true;
                m_liveStreamingInitFinished = false;
            }
        }
        //10、给广播绑定响应的过滤器（Demo层实现，用户不需要添加该操作）
        mHeadsetPlugReceiver = new HeadsetPlugReceiver();
        mHeadsetPlugReceiver.register();

        gl_tv = (TextView) v.findViewById(R.id.gl_tv);//滤镜
    }

    private Intent mAlertServiceIntent;

    @Override
    public void onDestroy() {
        if (mSurfaceView != null) {
            mSurfaceView.setVisibility(View.GONE);
        }
        if (m_liveStreamingInit) {
            m_liveStreamingInit = false;
        }
        //伴音相关Receiver取消注册
        context.unregisterReceiver(msgReceiver);
        context.unregisterReceiver(audioMixVolumeMsgReceiver);
        context.unregisterReceiver(mHeadsetPlugReceiver);

        //Demo层报警信息操作的销毁
        if (mAlertServiceOn) {
            mAlertServiceIntent = new Intent(context, AlertService.class);
            context.stopService(mAlertServiceIntent);
            mAlertServiceOn = false;
        }
        //停止直播调用相关API接口
        if (mLSMediaCapture != null && m_liveStreamingOn) {
            //停止直播，释放资源
            mLSMediaCapture.stopLiveStreaming();

            //如果音视频或者单独视频直播，需要关闭视频预览
            if (m_startVideoCamera) {
                mLSMediaCapture.stopVideoPreview();
                mLSMediaCapture.destroyVideoPreview();
            }
            //反初始化推流实例，当它与stopLiveStreaming连续调用时，参数为false
            mLSMediaCapture.uninitLsMediaCapture(false);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 2);
            context.sendBroadcast(mIntentLiveStreamingStopFinished);
        } else if (mLSMediaCapture != null && m_startVideoCamera) {
            mLSMediaCapture.stopVideoPreview();
            mLSMediaCapture.destroyVideoPreview();

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            context.sendBroadcast(mIntentLiveStreamingStopFinished);
        } else if (!m_liveStreamingInitFinished) {
            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            context.sendBroadcast(mIntentLiveStreamingStopFinished);

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
        }
        if (m_liveStreamingOn) {
            m_liveStreamingOn = false;
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (mLSMediaCapture != null) {
            if (!m_tryToStopLivestreaming && m_liveStreamingOn) {
                if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV
                        || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
                    //推最后一帧图像
                    mLSMediaCapture.backgroundVideoEncode();
                } else if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AUDIO) {
                    //释放音频采集资源
                    mLSMediaCapture.stopAudioRecord();
                }
            }
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mLSMediaCapture != null) {
            if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV
                    || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
                //关闭推流固定图像，正常推流
                mLSMediaCapture.resumeVideoEncode();
            } else if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AUDIO) {
                //关闭推流静音帧
                mLSMediaCapture.resumeAudioEncode();
            }
        }
        super.onResume();
    }

    public static final int CAMERA_POSITION_BACK = 0;
    public static final int CAMERA_POSITION_FRONT = 1;

    public static final int CAMERA_ORIENTATION_PORTRAIT = 0;
    public static final int CAMERA_ORIENTATION_LANDSCAPE = 1;
    public static final int CAMERA_ORIENTATION_PORTRAIT_UPSIDEDOWN = 2;
    public static final int CAMERA_ORIENTATION_LANDSCAPE_LEFTSIDERIGHT = 3;

    public static final int LS_VIDEO_CODEC_AVC = 0;
    public static final int LS_VIDEO_CODEC_VP9 = 1;
    public static final int LS_VIDEO_CODEC_H265 = 2;

    public static final int LS_AUDIO_STREAMING_LOW_QUALITY = 0;
    public static final int LS_AUDIO_STREAMING_HIGH_QUALITY = 1;

    public static final int LS_AUDIO_CODEC_AAC = 0;
    public static final int LS_AUDIO_CODEC_SPEEX = 1;
    public static final int LS_AUDIO_CODEC_MP3 = 2;
    public static final int LS_AUDIO_CODEC_G711A = 3;
    public static final int LS_AUDIO_CODEC_G711U = 4;

    public static final int FLV = 0;
    public static final int RTMP = 1;

    public static final int HAVE_AUDIO = 0;
    public static final int HAVE_VIDEO = 1;
    public static final int HAVE_AV = 2;

    public static final int OpenQoS = 0;
    public static final int CloseQoS = 1;

    //音视频参数设置
    private void paraSet() {
        //创建参数实例
        mLSLiveStreamingParaCtx = mLSMediaCapture.new LSLiveStreamingParaCtx();
        mLSLiveStreamingParaCtx.eHaraWareEncType = mLSLiveStreamingParaCtx.new HardWareEncEnable();
        mLSLiveStreamingParaCtx.eOutFormatType = mLSLiveStreamingParaCtx.new OutputFormatType();
        mLSLiveStreamingParaCtx.eOutStreamType = mLSLiveStreamingParaCtx.new OutputStreamType();
        mLSLiveStreamingParaCtx.sLSAudioParaCtx = mLSLiveStreamingParaCtx.new LSAudioParaCtx();
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec = mLSLiveStreamingParaCtx.sLSAudioParaCtx.new LSAudioCodecType();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx = mLSLiveStreamingParaCtx.new LSVideoParaCtx();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new LSVideoCodecType();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraPosition();
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraOrientation();
        mLSLiveStreamingParaCtx.sLSQoSParaCtx = mLSLiveStreamingParaCtx.new LSQoSParaCtx();

        if (!mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable && mWaterMarkOn) {
            waterMark();
        }

        if (!mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable && mGraffitiOn) {
            Graffiti();
        }

        //设置摄像头信息，并开始本地视频预览
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = CAMERA_POSITION_FRONT;//默认后置摄像头，用户可以根据需要调整

        //输出格式：视频、音频和音视频
        mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = HAVE_AV;//默认音视频推流

        //输出封装格式
        mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = RTMP;//默认RTMP推流

        //摄像头参数配置
        mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation.interfaceOrientation = CAMERA_ORIENTATION_PORTRAIT;//默认竖屏

        //音频编码参数配置
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;
        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = LS_AUDIO_CODEC_AAC;

        //硬件编码参数设置
        mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable = mHardWareEncEnable;//默认关闭硬件编码

        //网络QoS开关
        mLSLiveStreamingParaCtx.sLSQoSParaCtx.qosType = OpenQoS;//默认打开QoS

        //视频编码参数配置，视频码率可以由用户任意设置

        //如下是编码分辨率等信息的设置
        if (mVideoResolution.equals("HD")) {
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 1500000;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 1280;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 720;
        } else if (mVideoResolution.equals("SD")) {
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 640;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 480;
        } else {
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 15;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 250000;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = 320;
            mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = 240;
        }
    }

    private lsMediaCapture mLSMediaCapture = null;

    private String mMixAudioFilePath = null;
    private File mMP3AppFileDirectory = null;

    //处理伴音MP3文件
    public void handleMP3() {

        AssetManager assetManager = context.getAssets();

        String[] files = null;
        try {
            files = assetManager.list("mixAudio");
        } catch (IOException e) {
            LogUtil.showELog("tag", "Failed to get asset file list.>>>"+ e);
        }

        mMP3AppFileDirectory = context.getExternalFilesDir(null);
        if (mMP3AppFileDirectory == null) {
            mMP3AppFileDirectory = context.getFilesDir();
        }

        for (String filename : files) {
            try {
                InputStream in = assetManager.open("mixAudio/" + filename);
                File outFile = new File(mMP3AppFileDirectory, filename);
                mMixAudioFilePath = outFile.toString();
                if (!outFile.exists()) {
                    FileOutputStream out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                LogUtil.showELog("tag", "Failed to copy MP3 file", e);
            }
        }
    }

    //视频水印相关方法(1)
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //视频水印相关方法(2)
    public void waterMark() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mWaterMarkAppFileDirectory = context.getExternalFilesDir(null);
        } else {
            mWaterMarkAppFileDirectory = context.getFilesDir();
        }

        AssetManager assetManager = context.getAssets();

        //拷贝水印文件到APP目录
        String[] files = null;
        File fileDirectory;

        try {
            files = assetManager.list("waterMark");
        } catch (IOException e) {
            LogUtil.showELog("tag", "Failed to get asset file list.", e);
        }

        if (mWaterMarkAppFileDirectory != null) {
            fileDirectory = mWaterMarkAppFileDirectory;
        } else {
            fileDirectory = Environment.getExternalStorageDirectory();
            mWaterMarkFilePath = fileDirectory + "/" + mWaterMarkFileName;
        }

        for (String filename : files) {
            try {
                InputStream in = assetManager.open("waterMark/" + filename);
                File outFile = new File(fileDirectory, filename);
                FileOutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                mWaterMarkFilePath = outFile.toString();
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                LogUtil.showELog("tag", "Failed to copy asset file", e);
            }
        }
    }

    //视频涂鸦相关方法
    public void Graffiti() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mGraffitiAppFileDirectory = context.getExternalFilesDir(null);
        } else {
            mGraffitiAppFileDirectory = context.getFilesDir();
        }

        AssetManager assetManager = context.getAssets();

        //拷贝涂鸦文件到APP目录
        String[] files = null;
        File fileDirectory;

        try {
            files = assetManager.list("graffiti");
        } catch (IOException e) {
            LogUtil.showELog("tag", "Failed to get asset file list.", e);
        }

        if (mGraffitiAppFileDirectory != null) {
            fileDirectory = mGraffitiAppFileDirectory;
        } else {
            fileDirectory = Environment.getExternalStorageDirectory();
            mGraffitiFilePath = fileDirectory + "/" + mGraffitiFileName;
        }

        for (String filename : files) {
            try {
                InputStream in = assetManager.open("graffiti/" + filename);
                File outFile = new File(fileDirectory, filename);
                FileOutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                mGraffitiFilePath = outFile.toString();
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                LogUtil.showELog("tag", "Failed to copy asset file", e);
            }
        }
    }

    //伴音相关
    private AudioManager mAudioManager;
    private int m_mixAudioVolume;
    private HeadsetPlugReceiver mHeadsetPlugReceiver = null;
    private int mRouteMode = AUDIO_ROUTE_LOUDERSPEAKER;

    //伴音相关宏定义：
    //AUDIO_ROUTE_EARPIECE：有线耳机模式
    //AUDIO_ROUTE_LOUDERSPEAKER：外放模式
    //AUDIO_ROUTE_BLUETOOTH：蓝牙耳机模式
    public static final int AUDIO_ROUTE_EARPIECE = 0, AUDIO_ROUTE_LOUDERSPEAKER = 1, AUDIO_ROUTE_BLUETOOTH = 2;
    private static final String TAG = "NeteaseLiveStream";

    private boolean mAlertServiceOn = false;
    private long mLastAudioProcessErrorAlertTime = 0;
    private long mLastVideoProcessErrorAlertTime = 0;
    private lsMediaCapture.LSLiveStreamingParaCtx mLSLiveStreamingParaCtx = null;
    private Intent mIntentLiveStreamingStopFinished = new Intent("LiveStreamingStopFinished");

    @Override
    public void handleMessage(int i, Object object) {
        switch (i) {
            case MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
            case MSG_INIT_LIVESTREAMING_VIDEO_ERROR:
            case MSG_INIT_LIVESTREAMING_AUDIO_ERROR: {
                if (m_liveStreamingInit) {
                    Bundle bundle = new Bundle();
                    bundle.putString("alert", "MSG_INIT_LIVESTREAMING_ERROR");
                    Intent intent = new Intent(context, AlertService.class);
                    intent.putExtras(bundle);
                    context.startService(intent);
                    mAlertServiceOn = true;
                }
                break;
            }
            case MSG_START_LIVESTREAMING_ERROR: {//开始直播出错

                break;
            }
            case MSG_STOP_LIVESTREAMING_ERROR: {//停止直播出错
                if (m_liveStreamingOn) {
                    Bundle bundle = new Bundle();
                    bundle.putString("alert", "MSG_STOP_LIVESTREAMING_ERROR");
                    Intent intent = new Intent(context, AlertService.class);
                    intent.putExtras(bundle);
                    context.startService(intent);
                    mAlertServiceOn = true;
                }
                break;
            }
            case MSG_AUDIO_PROCESS_ERROR: {//音频处理出错
                if (m_liveStreamingOn && System.currentTimeMillis() - mLastAudioProcessErrorAlertTime >= 10000) {
                    Bundle bundle = new Bundle();
                    bundle.putString("alert", "MSG_AUDIO_PROCESS_ERROR");
                    Intent intent = new Intent(context, AlertService.class);
                    intent.putExtras(bundle);
                    context.startService(intent);
                    mAlertServiceOn = true;
                    mLastAudioProcessErrorAlertTime = System.currentTimeMillis();
                }

                break;
            }
            case MSG_VIDEO_PROCESS_ERROR: {//视频处理出错
                if (m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime >= 10000) {
                    Bundle bundle = new Bundle();
                    bundle.putString("alert", "MSG_VIDEO_PROCESS_ERROR");
                    Intent intent = new Intent(context, AlertService.class);
                    intent.putExtras(bundle);
                    context.startService(intent);
                    mAlertServiceOn = true;
                    mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
                }
                break;
            }
            case MSG_START_PREVIEW_ERROR: {//视频预览出错，可能是获取不到camera的使用权限
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_START_PREVIEW_ERROR");
                
                showMessage("视频预览出错，获取不到camera的使用权限");
                break;
            }
            case MSG_AUDIO_RECORD_ERROR: {//音频采集出错，获取不到麦克风的使用权限
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_AUDIO_RECORD_ERROR");
                showMessage("视频预览出错，获取不到麦克风的使用权限");
                break;
            }
            case MSG_RTMP_URL_ERROR: {//断网消息
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_RTMP_URL_ERROR");
                showMessage("网络已断开，请检查网络");
                ActivityJumpControl.getInstance(activity).gotoOverLiveActivity(MainLiveActivity.onlinenum + "", MainLiveActivity.onlinefx + "");
                activity.finish();
                break;
            }
            case MSG_URL_NOT_AUTH: {//直播URL非法，URL格式不符合视频云要求
                if (m_liveStreamingInit) {
                    Bundle bundle = new Bundle();
                    bundle.putString("alert", "MSG_URL_NOT_AUTH");
                    Intent intent = new Intent(context, AlertService.class);
                    intent.putExtras(bundle);
                    context.startService(intent);
                    mAlertServiceOn = true;
                }
                break;
            }
            case MSG_SEND_STATICS_LOG_ERROR: {//发送统计信息出错
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_ERROR");
                break;
            }
            case MSG_SEND_HEARTBEAT_LOG_ERROR: {//发送心跳信息出错
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_SEND_HEARTBEAT_LOG_ERROR");
                break;
            }
            case MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR: {//音频采集参数不支持
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR: {//音频参数不支持
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR");
                break;
            }
            case MSG_NEW_AUDIORECORD_INSTANCE_ERROR: {//音频实例初始化出错
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_NEW_AUDIORECORD_INSTANCE_ERROR");
                break;
            }
            case MSG_AUDIO_START_RECORDING_ERROR: {//音频采集出错
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_AUDIO_START_RECORDING_ERROR");
                break;
            }
            case MSG_QOS_TO_STOP_LIVESTREAMING: {//网络QoS极差，视频码率档次降到最低
                showMessage("网络状况极差,视频质量降到最低");
                break;
            }
            case MSG_HW_VIDEO_PACKET_ERROR: {//视频硬件编码出错反馈消息
                if (m_liveStreamingOn) {
                    Bundle bundle = new Bundle();
                    bundle.putString("alert", "MSG_HW_VIDEO_PACKET_ERROR");
                    Intent intent = new Intent(context, AlertService.class);
                    intent.putExtras(bundle);
                    context.startService(intent);
                    mAlertServiceOn = true;
                }
                break;
            }
            case MSG_WATERMARK_INIT_ERROR: {//视频水印操作初始化出错
                break;
            }
            case MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR: {//视频水印图像超出原始视频出错
                LogUtil.showILog(TAG, "test: in handleMessage: 视频水印图像超出原始视频出错");
                break;
            }
            case MSG_WATERMARK_PARA_ERROR: {//视频水印参数设置出错
                LogUtil.showILog(TAG, "test: in handleMessage: 视频水印参数设置出错");
                break;
            }
            case MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR: {//camera采集分辨率不支持
                LogUtil.showILog(TAG, "test: in handleMessage: camera采集分辨率不支持");
                break;
            }
            case MSG_START_PREVIEW_FINISHED: {//camera采集预览完成
                LogUtil.showILog(TAG, "test: camera采集预览完成");
                //如果是音视频直播或者视频直播，视频preview之后才允许开始直播
                if (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO) {
//                    startPauseResumeBtn.setEnabled(true);
                    if (!m_liveStreamingOn) {
                        if (mliveStreamingURL.isEmpty())
                            return;
                        startAV();
                    }
                }
                break;
            }
            case MSG_START_LIVESTREAMING_FINISHED: {//开始直播完成

                break;
            }
            case MSG_STOP_LIVESTREAMING_FINISHED: {//停止直播完成
                LogUtil.showILog(TAG, "test: 停止直播完成");
                {
                    mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
                    context.sendBroadcast(mIntentLiveStreamingStopFinished);
                }
                break;
            }
            case MSG_STOP_VIDEO_CAPTURE_FINISHED: {
                LogUtil.showILog(TAG, "test: in handleMessage: 继续视频推流");
                if (!m_tryToStopLivestreaming && mLSMediaCapture != null) {
                    //继续视频推流，推最后一帧图像
                    mLSMediaCapture.backgroundVideoEncode();
                }
                break;
            }
            case MSG_STOP_RESUME_VIDEO_CAPTURE_FINISHED: {
                LogUtil.showILog(TAG, "test: in handleMessage: 开启视频preview");
                //开启视频preview
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.resumeVideoPreview();
                    m_liveStreamingOn = true;
                    //开启视频推流，推正常帧
                    mLSMediaCapture.startVideoLiveStream();
                }
                break;
            }
            case MSG_STOP_AUDIO_CAPTURE_FINISHED: {
                LogUtil.showILog(TAG, "test: in handleMessage: 继续音频推流");
                if (!m_tryToStopLivestreaming && mLSMediaCapture != null) {
                    //继续音频推流，推静音帧
                    mLSMediaCapture.backgroundAudioEncode();
                }
                break;
            }
            case MSG_STOP_RESUME_AUDIO_CAPTURE_FINISHED: {
                LogUtil.showILog(TAG, "test: in handleMessage: 开启音频推流");
                //开启音频推流，推正常帧
                mLSMediaCapture.startAudioLiveStream();
                break;
            }
            case MSG_SWITCH_CAMERA_FINISHED: {//切换摄像头完成
                int cameraId = (Integer) object;//切换之后的camera id
                break;
            }
            case MSG_SEND_STATICS_LOG_FINISHED: {//发送统计信息完成
//                LogUtil.showILog(TAG, "test: in handleMessage, 发送统计信息完成");
                break;
            }
            case MSG_SERVER_COMMAND_STOP_LIVESTREAMING: {//服务器下发停止直播的消息反馈，暂时不使用
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_SERVER_COMMAND_STOP_LIVESTREAMING");
                break;
            }
            case MSG_GET_STATICS_INFO: {//获取统计信息的反馈消息
                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_GET_STATICS_INFO");
                //TODO: 显示统计信息，用户可以从统计信息中掌握网络发送数据的情况
                break;
            }
            case MSG_BAD_NETWORK_DETECT: {//如果连续一段时间（10s）实际推流数据为0，会反馈这个错误消息
                LogUtil.showILog(TAG, "test: in handleMessage, 10s内 实际推流数据为0");
                break;
            }
            case MSG_SCREENSHOT_FINISHED: {//视频截图完成后的消息反馈

                //LogUtil.showILog(TAG, "test: in handleMessage, MSG_SCREENSHOT_FINISHED, buffer is " + (byte[]) object);
                getScreenShotByteBuffer((byte[]) object);

                break;
            }
            case MSG_SET_CAMERA_ID_ERROR: {//设置camera出错（对于只有一个摄像头的设备，如果调用了不存在的摄像头，会反馈这个错误消息）
                LogUtil.showILog(TAG, "test: in handleMessage, 设置camera出错");
                break;
            }
            case MSG_SET_GRAFFITI_ERROR: {//设置涂鸦出错消息反馈
                LogUtil.showILog(TAG, "test: in handleMessage, 设置涂鸦出错");
                break;
            }
            case MSG_MIX_AUDIO_FINISHED: {//伴音一首MP3歌曲结束后的反馈
                LogUtil.showILog(TAG, "test: in handleMessage, 伴音歌曲结束");
                break;
            }
            case MSG_URL_FORMAT_NOT_RIGHT: {//推流url格式不正确
                LogUtil.showILog(TAG, "test: in handleMessage, 推流url格式不正确");
                break;
            }
            case MSG_URL_IS_EMPTY: {//推流url为空
                LogUtil.showILog(TAG, "test: in handleMessage, 推流url为空");
                break;
            }
        }
    }

    private boolean mHardWareEncEnable = false;
    //视频水印相关变量
    private Bitmap mBitmap;
    private boolean mWaterMarkOn = false;//视频水印开关，默认关闭，需要视频水印的用户可以开启此开关
    private String mWaterMarkFilePath;//视频水印文件路径
    private String mWaterMarkFileName = "logo.png";//视频水印文件名
    private File mWaterMarkAppFileDirectory = null;
    private int mWaterMarkPosX = 10;//视频水印坐标(X)
    private int mWaterMarkPosY = 10;//视频水印坐标(Y)
    //视频涂鸦相关变量
    private boolean mGraffitiOn = false;//视频涂鸦开关，默认关闭，需要视频涂鸦的用户可以开启此开关
    private String mGraffitiFilePath;//视频涂鸦文件路径
    private String mGraffitiFileName = "vcloud1.bmp";//视频涂鸦文件名
    private File mGraffitiAppFileDirectory = null;
    private int mGraffitiPosX = 0;
    private int mGraffitiPosY = 0;

    //开始直播
    private void startAV() {
        if (mLSMediaCapture != null && m_liveStreamingInitFinished) {

            //7、设置视频水印参数（可选）
            if (!mHardWareEncEnable && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
                mLSMediaCapture.setWaterMarkPara(mWaterMarkOn, mWaterMarkFilePath, mWaterMarkPosX, mWaterMarkPosY);
            }

            //8、开始直播
            mLSMediaCapture.startLiveStreaming();
            m_liveStreamingOn = true;

            //9、设置视频涂鸦参数（可选）
            if (!mHardWareEncEnable && mGraffitiOn && (mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO)) {
                FileInputStream is = null;
                try {
                    is = new FileInputStream(mGraffitiFilePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                int mWidth = bitmap.getWidth();
                int mHeight = bitmap.getHeight();
                int[] mIntArray = new int[mWidth * mHeight];

                // Copy pixel data from the Bitmap into the 'intArray' array
                bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LogUtil.showELog(TAG, "error : ", e);
                }

                mLSMediaCapture.setGraffitiPara(mIntArray, mWidth, mHeight, mGraffitiPosX, mGraffitiPosY, mGraffitiOn);
//                addRobot();
            }
        }
    }
    //切换前后摄像头
    public void switchCamera() {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.switchCamera();
        }
    }

    private boolean isFlashOn = false;
    //闪光灯
    public void switchcameraParameters() {
        if (mLSMediaCapture != null) {
            if (isFlashOn) {
                isFlashOn = false;
                mLSMediaCapture.setCameraFlashPara(false);
            } else {
                isFlashOn = true;
                mLSMediaCapture.setCameraFlashPara(true);
            }
        }
    }

    //new GPUImageFaceFilter()美顔
    //new GPUImageFilter()無
    private boolean canfto = true;

    //美颜
    public void doit() {
        if (canfto) {
            switchFilterTo(new GPUImageFaceFilter(), "美颜");
        } else {
            switchFilterTo(new GPUImageFilter(), "普通");
        }
    }

    private void switchFilterTo(final GPUImageFilter filter, String name) {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.setFilterType(filter);
            gl_tv.setVisibility(View.VISIBLE);
            gl_tv.setText(name);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gl_tv.setVisibility(View.GONE);
                    canfto = !canfto;
                }
            }, 1000);
        }
    }

    //视频截图相关变量
    private String mScreenShotFilePath = "/sdcard/";//视频截图文件路径
    private String mScreenShotFileName = "test.jpg";//视频截图文件名

    //获取截屏图像的数据
    public void getScreenShotByteBuffer(byte[] screenShotByteBuffer) {
        FileOutputStream outStream = null;
        String screenShotFilePath = mScreenShotFilePath + mScreenShotFileName;
        if (screenShotFilePath != null) {
            try {
                if (screenShotFilePath != null) {

                    outStream = new FileOutputStream(String.format(screenShotFilePath));
                    outStream.write(screenShotByteBuffer);
                    outStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    //监听设备耳机插拔的广播消息，支持有线耳机和外放模式
    private class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.getIntExtra("state", 0) == 0) {
                    mRouteMode = AUDIO_ROUTE_LOUDERSPEAKER;//外放
                } else if (intent.getIntExtra("state", 0) == 1) {
                    mRouteMode = AUDIO_ROUTE_EARPIECE;//耳机
                }
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.setAudioRouteMode(mRouteMode);
                }
            }

        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");
            context.registerReceiver(mHeadsetPlugReceiver, intentFilter);
        }

        public void unRegister() {
            context.unregisterReceiver(HeadsetPlugReceiver.this);
        }
    }
    public void setmusic(String musicpath){
        if (mLSMediaCapture != null) {
            mLSMediaCapture.pausePlayMusic();
            mLSMediaCapture.stopPlayMusic();
            mLSMediaCapture.setMixIntensity(m_mixAudioVolume);
            mLSMediaCapture.setAudioRouteMode(mRouteMode);
            mLSMediaCapture.startPlayMusic(musicpath);
            LogUtil.showDLog(TAG,"开始伴音>>>>>>"+musicpath);
        }
    }
    public void pausemusic(){
        if (mLSMediaCapture != null) {
            mLSMediaCapture.pausePlayMusic();
        }
    }
    public void resumemusic(){
        if (mLSMediaCapture != null) {
            mLSMediaCapture.resumePlayMusic();
        }
    }
    public void stopmusic(){
        if (mLSMediaCapture != null) {
            mLSMediaCapture.stopPlayMusic();
        }
    }
    //用于接收Service发送的消息，伴音开关
    private class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int audioMixMsg = intent.getIntExtra("AudioMixMSG", 0);
            mMixAudioFilePath = mMP3AppFileDirectory.toString() + "/" + intent.getStringExtra("AudioMixFilePathMSG");
            //伴音开关的控制
            if (audioMixMsg == 1) {
                if (mMixAudioFilePath.isEmpty()) {
                    return;
                }
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.setMixIntensity(m_mixAudioVolume);
                    mLSMediaCapture.setAudioRouteMode(mRouteMode);
                    mLSMediaCapture.startPlayMusic(mMixAudioFilePath);
                }
            } else if (audioMixMsg == 2) {
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.resumePlayMusic();
                }
            } else if (audioMixMsg == 3) {
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.pausePlayMusic();
                }
            } else if (audioMixMsg == 4) {
                if (mLSMediaCapture != null) {
                    mLSMediaCapture.stopPlayMusic();
                }
            }
        }
    }

    //用于接收Service发送的消息，伴音音量
    private class audioMixVolumeMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int audioMixVolumeMsg = intent.getIntExtra("AudioMixVolumeMSG", 0);
            //伴音音量的控制
            if (mRouteMode == AUDIO_ROUTE_LOUDERSPEAKER) {//外放模式
                int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                streamVolume = audioMixVolumeMsg * maxStreamVolume / 10;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, 1);
            } else if (mRouteMode == AUDIO_ROUTE_EARPIECE) { //耳机模式
                mLSMediaCapture.setMixIntensity(audioMixVolumeMsg);
            }
        }
    }
}