package com.android.zmlive.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.livevideo.NEMediaController;
import com.android.zmlive.livevideo.NEVideoView;
import com.android.zmlive.tool.ActivityJumpControl;
import com.android.zmlive.tool.Futile;
import com.netease.neliveplayer.NELivePlayer;

public class ShipinbofangActivity extends Activity {
    private final String TAG = "ShipinbofangActivity";
    public String url = null;//播放路径
    public String filename = null;//播放路径

    private int bufferStrategy = 1; //0:直播低延时；1:点播抗抖动
    private boolean isHardWare = false;
    private boolean pauseInBackgroud = false;
    private Activity activity ;
    private Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipinbofang_activity);
        activity=this;
        context=this;
        url = getIntent().getStringExtra("url");
        filename = getIntent().getStringExtra("filename");
        ActivityJumpControl.getInstance(activity).pushActivity(activity);
        findview();
    }

    private TextView mFileName = null;
    private RelativeLayout mPlayToolbar;
    private ImageView mPlayBack;
    private NEVideoView videoView;
    private NEMediaController mMediaController; //用于控制播放
    private String mediaType = "videoondemand"; //媒体类型，默认网络直播

    private void findview() {
//        mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);
        mFileName = (TextView) findViewById(R.id.file_name);
        mPlayToolbar = (RelativeLayout) findViewById(R.id.play_toolbar);
        mPlayToolbar.setVisibility(View.INVISIBLE);
        mPlayBack = (ImageView) findViewById(R.id.spbf_back);//退出播放
        mMediaController = new NEMediaController(this);
        videoView = (NEVideoView) findViewById(R.id.spbfvideo_view);
        videoView.setMediaController(mMediaController);
        videoView.setBufferStrategy(bufferStrategy); //点播抗抖动
        videoView.setMediaType(mediaType);
        videoView.setHardwareDecoder(isHardWare);
        videoView.setPauseInBackground(pauseInBackgroud);
        videoView.setOnErrorListener(onVideoErrorListener);
        videoView.setOnCompletionListener(onCompletionListener);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.start();

        mPlayBack.setOnClickListener(mOnClickEvent); //监听退出播放的事件响应
        mMediaController.setOnShownListener(mOnShowListener); //监听mediacontroller是否显示
        mMediaController.setOnHiddenListener(mOnHiddenListener); //监听mediacontroller是否隐藏
        if (mFileName != null) {
            mFileName.setText(filename);
        }
        mFileName.setGravity(Gravity.CENTER);

    }

    // onError
    private NELivePlayer.OnErrorListener onVideoErrorListener = new NELivePlayer.OnErrorListener() {
        @Override
        public boolean onError(NELivePlayer neLivePlayer, int i, int i1) {
            Futile.showMessage(context,"播放出错！！");
            videoView.release_resource();
            finish();
            return true;
        }
    };
    // onCompletion
    private NELivePlayer.OnCompletionListener onCompletionListener = new NELivePlayer.OnCompletionListener() {
        @Override
        public void onCompletion(NELivePlayer neLivePlayer) {
            Futile.showMessage(context,"播放結束");
            videoView.release_resource();
            finish();
        }
    };
    View.OnClickListener mOnClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.spbf_back) {
                videoView.release_resource();
                finish();
            }
        }
    };

    NEMediaController.OnShownListener mOnShowListener = new NEMediaController.OnShownListener() {
        @Override
        public void onShown() {
            mPlayToolbar.setVisibility(View.VISIBLE);
            mPlayToolbar.requestLayout();
            videoView.invalidate();
            mPlayToolbar.postInvalidate();
        }
    };

    NEMediaController.OnHiddenListener mOnHiddenListener = new NEMediaController.OnHiddenListener() {
        @Override
        public void onHidden() {
            mPlayToolbar.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityJumpControl.getInstance(activity).popActivity(activity);
    }

    @Override
    protected void onResume() {
        if (pauseInBackgroud && !videoView.isPaused()) {
            videoView.start(); //锁屏打开后恢复播放
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        if (pauseInBackgroud)
            videoView.pause(); //锁屏时暂停
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // 释放资源
        videoView.release_resource();
        super.onDestroy();
    }

}
