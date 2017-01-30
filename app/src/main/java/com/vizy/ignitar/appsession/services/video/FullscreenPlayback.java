package com.vizy.ignitar.appsession.services.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.vizy.ignitar.R;
import com.vizy.ignitar.activities.HomeActivity;
import com.vizy.ignitar.ui.customviews.CameraView;

import java.util.concurrent.locks.ReentrantLock;

public class FullscreenPlayback extends Activity implements OnPreparedListener, SurfaceHolder.Callback,
        OnVideoSizeChangedListener, OnErrorListener {

    private final String TAG=this.getClass().getSimpleName();
    private Camera camera;
    private CameraView cameraView;
    private VideoView mVideoView = null;
    private MediaPlayer mMediaPlayer = null;
    private SurfaceHolder mHolder = null;
    private MediaController mMediaController = null;
    private String mMovieName = "";
    private int mSeekPosition = 0;
    private int mRequestedOrientation = 0;
    private GestureDetector mGestureDetector = null;
    private boolean mShouldPlayImmediately = false;
    private SimpleOnGestureListener mSimpleListener = null;
    private ReentrantLock mMediaPlayerLock = null;
    private ReentrantLock mMediaControllerLock = null;
    private Handler handler = new Handler();

    protected void prepareViewForMediaPlayer() {
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mVideoView.setZOrderMediaOverlay(true);
        mHolder = mVideoView.getHolder();
        mHolder.addCallback(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMediaControllerLock = new ReentrantLock();
        mMediaPlayerLock = new ReentrantLock();

        prepareViewForMediaPlayer();
        mSeekPosition = getIntent().getIntExtra("currentSeekPosition", 0);
        mMovieName = getIntent().getStringExtra("movieName");
        mRequestedOrientation = getIntent().getIntExtra("requestedOrientation", 0);
        mShouldPlayImmediately = getIntent().getBooleanExtra("shouldPlayImmediately", false);

        try{
            camera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(camera != null) {
            cameraView = new CameraView(this, camera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(cameraView);//add the SurfaceView to the layout
        }


        mSimpleListener = new SimpleOnGestureListener();
        mGestureDetector = new GestureDetector(getApplicationContext(), mSimpleListener);
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
            public boolean onDoubleTap(MotionEvent e) {
                return false;
            }
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean result = false;
                mMediaControllerLock.lock();
                if (mMediaController != null) {
                    if (mMediaController.isShowing())
                        mMediaController.hide();
                    else
                        mMediaController.show();
                    result = true;
                }
                mMediaControllerLock.unlock();
                return result;
            }
        });
    }

    public Runnable isOver = new Runnable() {

        @Override
        public void run() {
            if ((mMediaPlayer!=null)&&(!mMediaPlayer.isPlaying())) {
                Log.e("sr", "sgr");
                prepareForTermination();
                Intent i = getIntent();
                startActivity(new Intent(FullscreenPlayback.this, HomeActivity.class).putExtra("videoname", i.getStringExtra("videoname")));
                finish();
            } else
                handler.post(isOver);
        }
    };

    private void createMediaPlayer() {
        mMediaPlayerLock.lock();
        mMediaControllerLock.lock();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaController = new MediaController(this);
            // This example shows how to load the movie from the assets
            // folder of the app. However, if you would like to load the
            // movie from the SD card or from a network location, simply
            // comment the four lines below:
            //AssetFileDescriptor afd = getAssets().openFd(mMovieName);
            //mMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(), afd.getLength());
            //afd.close();
            // And uncomment this line:
            // mMediaPlayer.setDataSource("http://www.nudedreams.net/mp4s/Giselle01.mp4");
            mMediaPlayer.setDataSource(mMovieName);
            mMediaPlayer.setDisplay(mHolder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            DebugLog.LOGE("Error while creating the MediaPlayer: " + e.toString());
            prepareForTermination();
            destroyMediaPlayer();
            finish();
        }
        mMediaControllerLock.unlock();
        mMediaPlayerLock.unlock();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        mMediaControllerLock.lock();
        mMediaPlayerLock.lock();
        if ((mMediaController != null) && (mVideoView != null) && (mMediaPlayer != null)) {
            if (mVideoView.getParent() != null) {
                mMediaController.setMediaPlayer(player_interface);
                View anchorView = mVideoView.getParent() instanceof View ? (View) mVideoView.getParent() : mVideoView;
                mMediaController.setAnchorView(anchorView);
                mVideoView.setMediaController(mMediaController);
                mMediaController.setEnabled(true);
                try {
                    mMediaPlayer.seekTo(mSeekPosition);
                } catch (Exception e) {
                    mMediaPlayerLock.unlock();
                    mMediaControllerLock.unlock();
                    DebugLog.LOGE("Could not seek to a position");
                }
                if (mShouldPlayImmediately) {
                    try {
                        mMediaPlayer.start();
                        mShouldPlayImmediately = false;
                    } catch (Exception e) {
                        mMediaPlayerLock.unlock();
                        mMediaControllerLock.unlock();
                        DebugLog.LOGE("Could not start playback");
                    }
                }
                mMediaController.show();
            }
        }
        mMediaPlayerLock.unlock();
        mMediaControllerLock.unlock();
        handler.post(isOver);
    }

    private void destroyMediaPlayer() {
        mMediaControllerLock.lock();
        if (mMediaController != null) {
            mMediaController.removeAllViews();
            mMediaController = null;
        }
        mMediaControllerLock.unlock();
        mMediaPlayerLock.lock();
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (Exception e) {
                mMediaPlayerLock.unlock();
                DebugLog.LOGE("Could not stop playback");
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayerLock.unlock();
        Log.e("end", "end");
    }

    private void destroyView() {
        // Release the View and the Holder:
        mVideoView = null;
        mHolder = null;
    }

    protected void onDestroy() {
        prepareForTermination();
        super.onDestroy();
        destroyMediaPlayer();
        mMediaPlayerLock = null;
        mMediaControllerLock = null;
    }

    protected void onResume() {
        super.onResume();
        prepareViewForMediaPlayer();
    }

    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    private void prepareForTermination() {
        Log.e("l,o", "jnjn");
        mMediaControllerLock.lock();
        if (mMediaController != null) {
            mMediaController.hide();
            mMediaController.removeAllViews();
        }
        mMediaControllerLock.unlock();
        mMediaPlayerLock.lock();
        if (mMediaPlayer != null) {
            mSeekPosition = mMediaPlayer.getCurrentPosition();
            boolean wasPlaying = mMediaPlayer.isPlaying();
            if (wasPlaying) {
                try {
                    mMediaPlayer.pause();
                } catch (Exception e) {
                    mMediaPlayerLock.unlock();
                    DebugLog.LOGE("Could not pause playback");
                }
            }
            Intent i = new Intent();
            i.putExtra("movieName", mMovieName);
            i.putExtra("currentSeekPosition", mSeekPosition);
            i.putExtra("playing", wasPlaying);
            setResult(Activity.RESULT_OK, i);
        }
        mMediaPlayerLock.unlock();
    }

    public void onBackPressed() {
        prepareForTermination();
        super.onBackPressed();
    }

    protected void onPause() {
        super.onPause();
        prepareForTermination();
        destroyMediaPlayer();
        destroyView();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        createMediaPlayer();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private MediaController.MediaPlayerControl player_interface = new MediaController.MediaPlayerControl() {
                public int getBufferPercentage() {
                    return 100;
                }
                public int getCurrentPosition() {
                    int result = 0;
                    mMediaPlayerLock.lock();
                    if (mMediaPlayer != null)
                        result = mMediaPlayer.getCurrentPosition();
                    mMediaPlayerLock.unlock();
                    return result;
                }

                public int getDuration() {
                    int result = 0;
                    mMediaPlayerLock.lock();
                    if (mMediaPlayer != null)
                        result = mMediaPlayer.getDuration();
                    mMediaPlayerLock.unlock();
                    return result;
                }

                public boolean isPlaying() {
                    boolean result = false;
                    mMediaPlayerLock.lock();
                    if (mMediaPlayer != null)
                        result = mMediaPlayer.isPlaying();
                    mMediaPlayerLock.unlock();
                    return result;
                }

                public void pause() {
                    mMediaPlayerLock.lock();
                    if (mMediaPlayer != null) {
                        try {
                            mMediaPlayer.pause();
                        } catch (Exception e) {
                            mMediaPlayerLock.unlock();
                            DebugLog.LOGE("Could not pause playback");
                        }
                    }
                    mMediaPlayerLock.unlock();
                }

                public void seekTo(int pos) {
                    mMediaPlayerLock.lock();
                    if (mMediaPlayer != null) {
                        try {
                            mMediaPlayer.seekTo(pos);
                        } catch (Exception e) {
                            mMediaPlayerLock.unlock();
                            DebugLog.LOGE("Could not seek to position");
                        }
                    }
                    mMediaPlayerLock.unlock();
                }

                public void start() {
                    mMediaPlayerLock.lock();
                    if (mMediaPlayer != null) {
                        try {
                            mMediaPlayer.start();
                        } catch (Exception e) {
                            mMediaPlayerLock.unlock();
                            DebugLog.LOGE("Could not start playback");
                        }
                    }
                    mMediaPlayerLock.unlock();
                }

                public boolean canPause() {
                    return true;
                }

                public boolean canSeekBackward() {
                    return true;
                }

                public boolean canSeekForward() {
                    return true;
                }

                @Override
                public int getAudioSessionId() {
                    return 0;
                }
            };

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        mMediaPlayerLock.lock();
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mVideoView.getLayoutParams().height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
        mVideoView.getLayoutParams().width = screenWidth;
        mMediaPlayerLock.unlock();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mp == mMediaPlayer) {
            String errorDescription;
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    errorDescription = "The video is streamed and its container is not valid for progressive playback";
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    errorDescription = "Media server died";
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    errorDescription = "Unspecified media player error";
                    break;
                default:
                    errorDescription = "Unknown error " + what;
                    break;
            }
            DebugLog.LOGE("Error while opening the file for fullscreen. " + "Unloading the media player (" + errorDescription + ", " + extra + ")");
            prepareForTermination();
            destroyMediaPlayer();
            finish();
            return true;
        }
        return false;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Log.d(TAG,"RetainNonConfigurationInstance");
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    public Object getLastNonConfigurationInstance() {
        Log.d(TAG,"LastNonConfigurationInstance");
        return super.getLastNonConfigurationInstance();
    }
}
