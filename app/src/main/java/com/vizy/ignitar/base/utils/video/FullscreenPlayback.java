/*==============================================================================
            Copyright (c) 2012-2013 QUALCOMM Austria Research Center GmbH.
            All Rights Reserved.
            Qualcomm Confidential and Proprietary

This  Vuforia(TM) sample application in source code form ("Sample Code") for the
Vuforia Software Development Kit and/or Vuforia Extension for Unity
(collectively, the "Vuforia SDK") may in all cases only be used in conjunction
with use of the Vuforia SDK, and is subject in all respects to all of the terms
and conditions of the Vuforia SDK License Agreement, which may be found at
https://developer.vuforia.com/legal/license.

By retaining or using the Sample Code in any manner, you confirm your agreement
to all the terms and conditions of the Vuforia SDK License Agreement.  If you do
not agree to all the terms and conditions of the Vuforia SDK License Agreement,
then you may not retain or use any of the Sample Code in any manner.


@file
    FullscreenPlayback.java

@brief
    FullscreenPlayback Activity. This activity contains a MediaPlayer class
    to play the movie as a full screen. It also holds a MediaController to help
    play/pause and skip with on screen buttons. This activitiy should be started
    for result, meaning that it will return the seek position of the movie
    as well as whether it was currently playing or not. It is meant to be used
    together with the VideoPlayerHelper

==============================================================================*/


package com.vizy.ignitar.base.utils.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.MediaController;
import android.widget.VideoView;

import com.vizy.ignitar.R;
import com.vizy.ignitar.activities.HomeActivity;

import java.util.concurrent.locks.ReentrantLock;


public class FullscreenPlayback extends Activity implements
                                                    OnPreparedListener,
                                                    SurfaceHolder.Callback,
                                                    OnVideoSizeChangedListener,
                                                    OnErrorListener
{
    private VideoView               mVideoView                      = null;
    private MediaPlayer             mMediaPlayer                    = null;
    private SurfaceHolder           mHolder                         = null;
    private MediaController         mMediaController                = null;
    private String                  mMovieName                      = "";
    private int                     mSeekPosition                   = 0;
    private int                     mRequestedOrientation           = 0;
    private GestureDetector         mGestureDetector                = null;
    private boolean                 mShouldPlayImmediately          = false;
    private SimpleOnGestureListener mSimpleListener                 = null;
    private ReentrantLock           mMediaPlayerLock                = null;
    private ReentrantLock           mMediaControllerLock            = null;

    private Handler handler = new Handler();
    /** This is called when we need to prepare the view for the media player */
    protected void prepareViewForMediaPlayer()
    {
        // Create the view: 
        mVideoView = (VideoView) findViewById(R.id.surface_view);

        // The orientation was passed as an extra by the launching activity:
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mHolder = mVideoView.getHolder();
        mHolder.addCallback(this);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        // DebugLog.LOGD("Fullscreen::onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_layout);
      //  setupWindowAnimations();
        // Create the locks:
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mMediaControllerLock = new ReentrantLock();
        mMediaPlayerLock = new ReentrantLock();

        // Request a view to be used by the media player:
        prepareViewForMediaPlayer();

        // Collect all of the data passed by the launching activity:
        mSeekPosition = getIntent().getIntExtra("currentSeekPosition", 0);
        mMovieName = getIntent().getStringExtra("movieName");
        mRequestedOrientation = getIntent().
            getIntExtra("requestedOrientation", 0);
        mShouldPlayImmediately = getIntent().
            getBooleanExtra("shouldPlayImmediately", false);

        // Create a gesture detector that will handle single and double taps:
        mSimpleListener = new SimpleOnGestureListener();
        mGestureDetector = new GestureDetector(
            getApplicationContext(), mSimpleListener);

        // We assign the actions for the single and double taps:
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener()
        {
            public boolean onDoubleTap(MotionEvent e)
            {
                return false;
            }

            public boolean onDoubleTapEvent(MotionEvent e)
            {
                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                boolean result = false;
                mMediaControllerLock.lock();
                    // This simply toggles the MediaController visibility:
                    if (mMediaController != null)
                    {
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
    public Runnable isOver=new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(!mMediaPlayer.isPlaying()){
                Log.e("sr","sgr");
                prepareForTermination();
                Intent i=getIntent();
                startActivity(new Intent(FullscreenPlayback.this, HomeActivity.class).putExtra("videoname",i.getStringExtra("videoname")));
                finish();
            }
            else
                handler.post(isOver);

        }
    };

    /** This is the call that actually creates the media player */
    private void createMediaPlayer()
    {
        mMediaPlayerLock.lock();
        mMediaControllerLock.lock();

            try
            {
                // Create the MediaPlayer and its controller:
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

            }
            catch (Exception e)
            {
                DebugLog.LOGE("Error while creating the MediaPlayer: "
                    + e.toString());

                // If something failed then prepare for termination:
                prepareForTermination();

                // Release the resources of the media player:
                destroyMediaPlayer();

                // Then terminate this activity:
                finish();
            }

        mMediaControllerLock.unlock();
        mMediaPlayerLock.unlock();
    }
    private void setupWindowAnimations() {
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(fade);
    }

    /** Handle the touch event */
    public boolean onTouchEvent(MotionEvent event)
    {
        // The touch event is actually handled by the gesture detector
        // so we just forward the event to it:
        return mGestureDetector.onTouchEvent(event);
    }

    /** This is a callback we receive when the media player is ready to start playing */
    public void onPrepared(MediaPlayer mediaplayer)
    {
        // DebugLog.LOGD("Fullscreen::onPrepared");

        mMediaControllerLock.lock();
        mMediaPlayerLock.lock();

            if ( (mMediaController != null) &&
                 (mVideoView != null) &&
                 (mMediaPlayer != null))
            {
                if (mVideoView.getParent() != null)
                {
                    // We attach the media controller to the player:
                    mMediaController.setMediaPlayer(player_interface);

                    // Add the media controller to the view:
                    View anchorView = mVideoView.getParent() instanceof View ?
                        (View)mVideoView.getParent() : mVideoView;
                    mMediaController.setAnchorView(anchorView);
                    mVideoView.setMediaController(mMediaController);
                    mMediaController.setEnabled(true);

                    // Move to a given position:
                    try
                    {
                        mMediaPlayer.seekTo(mSeekPosition);
                    }
                    catch (Exception e)
                    {
                        mMediaPlayerLock.unlock();
                        mMediaControllerLock.unlock();
                        DebugLog.LOGE("Could not seek to a position");
                    }

                    // If the client requests that we play immediately
                    // we tell the media player to start:
                    if (mShouldPlayImmediately)
                    {
                        try
                        {
                            mMediaPlayer.start();
                            mShouldPlayImmediately = false;

                        }
                        catch (Exception e)
                        {
                            mMediaPlayerLock.unlock();
                            mMediaControllerLock.unlock();
                            DebugLog.LOGE("Could not start playback");
                        }
                    }

                    // Show briefly the controls:
                    mMediaController.show();
                }
            }

        mMediaPlayerLock.unlock();
        mMediaControllerLock.unlock();
        handler.post(isOver);
    }

    /** Called when we wish to release the resources of the media player */
    private void destroyMediaPlayer()
    {
        // Release the Media Controller:
        mMediaControllerLock.lock();
            if (mMediaController != null)
            {
                mMediaController.removeAllViews();
                mMediaController = null;
            }
        mMediaControllerLock.unlock();

        // Release the MediaPlayer:
        mMediaPlayerLock.lock();
            if (mMediaPlayer != null) {
                try
                {
                    mMediaPlayer.stop();
                }
                catch (Exception e)
                {
                    mMediaPlayerLock.unlock();
                    DebugLog.LOGE("Could not stop playback");
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        mMediaPlayerLock.unlock();
        Log.e("end","end");
    }

    /** Called when we wish to destroy the view used by the Media player */
    private void destroyView()
    {
        // Release the View and the Holder:
        mVideoView = null;
        mHolder = null;
    }

    /** Called when the app is destroyed */
    protected void onDestroy()
    {
        // DebugLog.LOGD("Fullscreen::onDestroy");

        // Prepare the media player for termination:
        prepareForTermination();

        super.onDestroy();

        // Release the resources of the media player:
        destroyMediaPlayer();

        mMediaPlayerLock = null;
        mMediaControllerLock = null;
    }

    /** Called when the app is resumed */
    protected void onResume()
    {
        // DebugLog.LOGD("Fullscreen::onResume");
        super.onResume();

        // Prepare a view that the media player can use:
        prepareViewForMediaPlayer();
    }

    /** Called when the activity configuration has changed */
    public void onConfigurationChanged(Configuration config)
    {
        super.onConfigurationChanged(config);
    }

    /** This is called when we should prepare the media player and the
     * activity for termination
     */
    private void prepareForTermination()
    {
        // First we prepare the controller:
        Log.e("l,o","jnjn");
        mMediaControllerLock.lock();
            if (mMediaController != null)
            {
                mMediaController.hide();
                mMediaController.removeAllViews();
            }
        mMediaControllerLock.unlock();

        // Then the MediaPlayer:
        mMediaPlayerLock.lock();
            if (mMediaPlayer != null)
            {
                // We store the position where it was currently playing:
                mSeekPosition = mMediaPlayer.getCurrentPosition();

                // We store the playback mode of the movie:
                boolean wasPlaying = mMediaPlayer.isPlaying();
                if (wasPlaying)
                {
                    try
                    {
                        mMediaPlayer.pause();
                    }
                    catch (Exception e)
                    {
                        mMediaPlayerLock.unlock();
                        DebugLog.LOGE("Could not pause playback");
                    }
                }

                // This activity was started for result, thus we need to return
                // whether it was playing and in which position:
                Intent i = new Intent();
                i.putExtra("movieName", mMovieName);
                i.putExtra("currentSeekPosition", mSeekPosition);
                i.putExtra("playing", wasPlaying);
                setResult(Activity.RESULT_OK, i);

            }
        mMediaPlayerLock.unlock();
    }

    public void onBackPressed()
    {
        // Request the media player to prepare for termination:
        prepareForTermination();
        super.onBackPressed();
    }

    /** Called when the activity is paused */
    protected void onPause()
    {
        // DebugLog.LOGD("Fullscreen::onPause");
        super.onPause();

        // We first prepare for termination:
        prepareForTermination();

        // Request the release of resource of the media player:
        destroyMediaPlayer();

        // Request the release of resource of the view:
        destroyView();
    }

    /** Called when the surface is changed*/
    public void surfaceCreated(SurfaceHolder holder)
    {
        // Request the creation of a media player:
        createMediaPlayer();

    }

    /** Called when the surface is changed */
    public void surfaceChanged(SurfaceHolder holder, int format,
        int width, int height) {}

    /** Called when the surface is destroyed */
    public void surfaceDestroyed(SurfaceHolder holder) {}

    /** The following are the predefined methods of the MediaPlayerController
     *  We simply forward the values to/from the MediaPlayer
     */
    private MediaController.MediaPlayerControl player_interface =
        new MediaController.MediaPlayerControl()
        {
            /** Returns the current buffering percentage */
            public int getBufferPercentage()
            {
                return 100;
            }

            /** Returns the current seek position */
            public int getCurrentPosition()
            {
                int result = 0;
                mMediaPlayerLock.lock();
                    if (mMediaPlayer!=null)
                        result = mMediaPlayer.getCurrentPosition();
                mMediaPlayerLock.unlock();
                return result;
            }

            /** Returns the duration of the movie */
            public int getDuration()
            {
                int result = 0;
                mMediaPlayerLock.lock();
                    if (mMediaPlayer!=null)
                        result = mMediaPlayer.getDuration();
                mMediaPlayerLock.unlock();
                return result;
            }

            /** Returns whether the movie is currently playing */
            public boolean isPlaying()
            {
                boolean result = false;

                mMediaPlayerLock.lock();
                    if (mMediaPlayer!=null)
                        result = mMediaPlayer.isPlaying();
                mMediaPlayerLock.unlock();
                return result;
            }

            /** Pauses the current playback */
            public void pause()
            {
                mMediaPlayerLock.lock();
                    if (mMediaPlayer!=null)
                    {
                        try
                        {
                            mMediaPlayer.pause();
                        }
                        catch (Exception e)
                        {
                            mMediaPlayerLock.unlock();
                            DebugLog.LOGE("Could not pause playback");
                        }
                    }
                mMediaPlayerLock.unlock();
            }

            /** Seeks to the required position */
            public void seekTo(int pos)
            {
                mMediaPlayerLock.lock();
                    if (mMediaPlayer!=null)
                    {
                        try
                        {
                            mMediaPlayer.seekTo(pos);
                        }
                        catch (Exception e)
                        {
                            mMediaPlayerLock.unlock();
                            DebugLog.LOGE("Could not seek to position");
                        }
                    }
                mMediaPlayerLock.unlock();
            }

            /** Starts the playback of the movie */
            public void start()
            {
                mMediaPlayerLock.lock();
                    if (mMediaPlayer!=null)
                    {
                        try
                        {
                            mMediaPlayer.start();
                        }
                        catch (Exception e)
                        {
                            mMediaPlayerLock.unlock();
                            DebugLog.LOGE("Could not start playback");
                        }
                    }
                mMediaPlayerLock.unlock();
            }

            /** Returns whether the movie can be paused */
            public boolean canPause()
            {
                return true;
            }

            /** Returns whether the movie can seek backwards */
            public boolean canSeekBackward()
            {
                return true;
            }

            /** Returns whether the movie can seek forwards */
            public boolean canSeekForward()
            {
                return true;
            }

            @Override
            public int getAudioSessionId() {
                return 0;
            }
        };

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height)
    {
        mMediaPlayerLock.lock();

            // Get video and screen dimensions:
            int videoWidth = mMediaPlayer.getVideoWidth();
            int videoHeight = mMediaPlayer.getVideoHeight();
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

            // Apply aspect ratio:
            mVideoView.getLayoutParams().height = (int) (((float) videoHeight /
                                   (float) videoWidth ) * (float) screenWidth );
            mVideoView.getLayoutParams().width = screenWidth;
        mMediaPlayerLock.unlock();
    }

    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        if (mp == mMediaPlayer)
        {
            String errorDescription;

            switch (what)
            {
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

            DebugLog.LOGE("Error while opening the file for fullscreen. " +
                "Unloading the media player (" + errorDescription + ", " +
                extra + ")");

            // If something failed then prepare for termination and
            // request a finish:
            prepareForTermination();

            // Release the resources of the media player:
            destroyMediaPlayer();

            // Then terminate this activity:
            finish();

            return true;
        }

        return false;
    }
}
