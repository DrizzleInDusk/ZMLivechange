package com.android.zmlive.fragment.liveing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.zmlive.R;
import com.android.zmlive.activity.MainAudienceActivity;
import com.android.zmlive.livevideo.NEVideoView;
import com.android.zmlive.tool.utils.BaseFragment;
import com.netease.neliveplayer.NELivePlayer;


/**
 * 该Fragment用于对直播或观看直播的初始化
 * 直播的控件的创建以及销毁等等都可以在这里进行操作，这样就与我们自己的交互代码分离了
 */
public class AudienceViewFragment extends BaseFragment{

    private final static String EXTRA_URL = "EXTRA_URL";

    private int bufferStrategy = 0; //0:直播低延时；1:点播抗抖动
    private boolean isHardWare = false;
    private boolean pauseInBackgroud = false;
    private String url; // 拉流地址
    private static final String Tag = "AudienceViewFragment";
    private NEVideoView videoView;
    private View loadingView;
    private String decodeType = "software";  //解码类型，默认软件解码
    private String mediaType = "livestream"; //媒体类型，默认网络直播

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_audienceview, container, false);
        url = activity.getIntent().getStringExtra(EXTRA_URL);
        videoView = (NEVideoView) contentView.findViewById(R.id.video_view);
        loadingView = contentView.findViewById(R.id.loading_view);

        videoView.setBufferStrategy(bufferStrategy); //点播抗抖动
        videoView.setBufferingIndicator(loadingView);
        videoView.setMediaType(mediaType);
        videoView.setHardwareDecoder(isHardWare);
        videoView.setPauseInBackground(pauseInBackgroud);
        videoView.setOnErrorListener(onVideoErrorListener);
        videoView.setOnCompletionListener(onCompletionListener);
        videoView.setOnInfoListener(onInfoListener);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.start();
        return contentView;
    }

    // onError
    private NELivePlayer.OnErrorListener onVideoErrorListener = new NELivePlayer.OnErrorListener() {
        @Override
        public boolean onError(NELivePlayer neLivePlayer, int i, int i1) {
            showMessage(getResources().getString(R.string.errlive2));
            ((MainAudienceActivity) context).audiencefinish();
            return true;
        }
    };
    // onCompletion
    private NELivePlayer.OnCompletionListener onCompletionListener = new NELivePlayer.OnCompletionListener() {
        @Override
        public void onCompletion(NELivePlayer neLivePlayer) {
            dialogmess();
        }
    };
    private NELivePlayer.OnInfoListener onInfoListener = new NELivePlayer.OnInfoListener() {
        @Override
        public boolean onInfo(NELivePlayer neLivePlayer, int what, int i1) {
            // web端推流的时候，不报onPrepared上来，只能用onInfo处理了。哼
            if (what == NELivePlayer.NELP_FIRST_VIDEO_RENDERED) {
                videoView.start();
            }
            return false;
        }
    };
    @Override
    public void onResume() {
        if (pauseInBackgroud && !videoView.isPaused()) {
            videoView.start(); //锁屏打开后恢复播放
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (pauseInBackgroud)
            videoView.pause(); //锁屏时暂停
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // 释放资源
        videoView.release_resource();
        super.onDestroy();
    }


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
                    // 释放资源
                    videoView.release_resource();
                    ((MainAudienceActivity) context).audiencefinish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}