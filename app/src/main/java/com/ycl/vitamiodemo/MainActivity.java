package com.ycl.vitamiodemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity  implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener{
    private String path="http://flv2.bn.netease.com/tvmrepo/2016/5/N/3/EBMTJBGN3/SD/EBMTJBGN3-mobile.mp4";
    //private String path= Environment.getExternalStorageDirectory()+"/xihuanni.mp4";
    private Uri uri;
    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private FrameLayout fl_controller;
    boolean isPortrait=true;
    private long mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(this)){
            return;
        }
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mVideoView = (VideoView) findViewById(R.id.buffer);
        fl_controller= (FrameLayout) findViewById(R.id.fl_controller);
        pb = (ProgressBar) findViewById(R.id.probar);

        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);
        if (path == "") {
            // Tell the user to provide a media file URL/path.
            return;
        } else {
      /*
       * Alternatively,for streaming media you can use
       * mVideoView.setVideoURI(Uri.parse(URLstring));
       */
            uri = Uri.parse(path);

            mVideoView.setVideoURI(uri);
            MediaController mc = new MediaController(this, true, fl_controller);
            mc.setOnControllerClick(new MediaController.OnControllerClick() {
                @Override
                public void OnClick(int type) {
                    //type 0 全屏。type1 分享
                    if (type == 0) {
                        if (isPortrait) {
                            LinearLayout.LayoutParams fl_lp = new LinearLayout.LayoutParams(
                                    getHeightPixel(MainActivity.this),
                                    getWidthPixel(MainActivity.this) - getStatusBarHeight(MainActivity.this)
                            );

                            fl_controller.setLayoutParams(fl_lp);
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                            isPortrait = false;
                        } else {
                            LinearLayout.LayoutParams fl_lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    DensityUtil.dip2px(200,MainActivity.this)
                            );

                            fl_controller.setLayoutParams(fl_lp);

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            isPortrait = true;
                        }

                    }

                }
            });
            mVideoView.setMediaController(mc);
            mc.setVisibility(View.GONE);
            //  mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

        }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);

                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!isPortrait){
                LinearLayout.LayoutParams fl_lp=new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        DensityUtil.dip2px(200,MainActivity.this)
                );
                fl_controller.setLayoutParams(fl_lp);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isPortrait=true;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

    @Override
    protected void onPause() {
        mPosition = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mPosition > 0) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();


    }


    public  int getHeightPixel(Activity activity)
    {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }
    public  int getWidthPixel(Activity activity)
    {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }
    public  int getStatusBarHeight(Activity activity){
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;
        return statusBarHeight;
    }
}
