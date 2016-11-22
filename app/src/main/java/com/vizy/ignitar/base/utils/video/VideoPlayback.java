package com.vizy.ignitar.base.utils.video;/*==============================================================================
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
    VideoPlayback.java

@brief
    This sample application shows how to play a video in AR mode.
    Devices that support video on texture can play the video directly
    on the image target.

    Other devices will play the video in full screen mode.
==============================================================================*//*



package com.vizy.ignitar.video;

import java.util.Vector;


import android.app.Activity;
import com.vizy.ignitar.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.qualcomm.QCAR.QCAR;
*/
/** The AR activity for the VideoPlayback sample. *//*

public class VideoPlayback extends Activity
{
	// Menu item string constants:
    private static final String MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS =
        "Activate Cont. Auto Focus";
    private static final String MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS =
        "Deactivate Cont. Auto Focus";
    private static final String MENU_ITEM_TRIGGER_AUTO_FOCUS = 
    		"Trigger Auto Focus";
    
	// Focus mode constants:
    private static final int FOCUS_MODE_NORMAL = 0;
    private static final int FOCUS_MODE_CONTINUOUS_AUTO = 1;
    
    // Application status constants:
    private static final int APPSTATUS_UNINITED         = -1;
    private static final int APPSTATUS_INIT_APP         = 0;
    private static final int APPSTATUS_INIT_QCAR        = 1;
    private static final int APPSTATUS_INIT_TRACKER     = 2;
    private static final int APPSTATUS_INIT_APP_AR      = 3;
    private static final int APPSTATUS_LOAD_TRACKER     = 4;
    private static final int APPSTATUS_INITED           = 5;
    private static final int APPSTATUS_CAMERA_STOPPED   = 6;
    private static final int APPSTATUS_CAMERA_RUNNING   = 7;

    // Name of the native dynamic libraries to load:
    private static final String NATIVE_LIB_SAMPLE       = "VideoPlayback";
    private static final String NATIVE_LIB_QCAR         = "QCAR";

    // Helpers to detect events such as double tapping:
    private GestureDetector mGestureDetector            = null;
    private SimpleOnGestureListener mSimpleListener     = null;

    // Pointer to the current activity:
    private Activity mCurrentActivity                   = null;

    // Movie for the Targets:
    public static final int NUM_TARGETS                 = 2;
    public static final int STONES                      = 0;
    public static final int CHIPS                       = 1;
    private VideoPlayerHelper mVideoPlayerHelper[]      = null;
    private int mSeekPosition[]                         = null;
    private boolean mWasPlaying[]                       = null;
    private String mMovieName[]                         = null;

    // A boolean to indicate whether we come from full screen:
    private boolean mReturningFromFullScreen            = false;

    // Our OpenGL view:
    private QCARSampleGLView mGlView;

    // The StartupScreen view and the start button:
    private View mStartupView                           = null;
    private ImageView mStartButton                      = null;
    private boolean mStartScreenShowing                 = false;

    // The view to display the sample splash screen:
    private ImageView mSplashScreenView;

    // The handler and runnable for the splash screen time out task:
    private Handler mSplashScreenHandler;
    private Runnable mSplashScreenRunnable;

    // The minimum time the splash screen should be visible:
    private static final long MIN_SPLASH_SCREEN_TIME    = 2000;

    // The time when the splash screen has become visible:
    long mSplashScreenStartTime = 0;

    // Our renderer:
    private VideoPlaybackRenderer mRenderer;

    // Display size of the device:
    private int mScreenWidth                            = 0;
    private int mScreenHeight                           = 0;

    // The current application status:
    private int mAppStatus                              = APPSTATUS_UNINITED;

    // The async tasks to initialize the QCAR SDK:
    private InitQCARTask mInitQCARTask;
    private LoadTrackerTask mLoadTrackerTask;

    // An object used for synchronizing QCAR initialization, dataset loading and
    // the Android onDestroy() life cycle event. If the application is destroyed
    // while a data set is still being loaded, then we wait for the loading
    // operation to finish before shutting down QCAR.
    private Object mShutdownLock = new Object();

    // QCAR initialization flags:
    private int mQCARFlags = 0;

    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    private int mSplashScreenImageResource = 0;
    
    // Current focus mode:
    private int mFocusMode;

    */
/** Static initializer block to load native libraries on start-up. *//*

    static
    {
        loadLibrary(NATIVE_LIB_QCAR);
        loadLibrary(NATIVE_LIB_SAMPLE);
    }

    */
/** An async task to initialize QCAR asynchronously. *//*

    private class InitQCARTask extends AsyncTask<Void, Integer, Boolean>
    {
        // Initialize with invalid value:
        private int mProgressValue = -1;

        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap with initialization:
            synchronized (mShutdownLock)
            {
                QCAR.setInitParameters(VideoPlayback.this, mQCARFlags);

                do
                {
                    // QCAR.init() blocks until an initialization step is
                    // complete, then it proceeds to the next step and reports 
                    // progress in percents (0 ... 100%).
                    // If QCAR.init() returns -1, it indicates an error.
                    // Initialization is done when progress has reached 100%.
                    mProgressValue = QCAR.init();

                    // Publish the progress value:
                    publishProgress(mProgressValue);

                    // We check whether the task has been canceled in the
                    // meantime (by calling AsyncTask.cancel(true))
                    // and bail out if it has, thus stopping this thread.
                    // This is necessary as the AsyncTask will run to completion
                    // regardless of the status of the component that 
                    // started is.
                } while (!isCancelled() && mProgressValue >= 0 
                         && mProgressValue < 100);

                return (mProgressValue > 0);
            }
        }


        protected void onProgressUpdate(Integer... values)
        {
            // Do something with the progress value "values[0]", e.g. update
            // splash screen, progress bar, etc.
        }


        protected void onPostExecute(Boolean result)
        {
            // Done initializing QCAR, proceed to next application
            // initialization status:
            if (result)
            {
                DebugLog.LOGD("InitQCARTask::onPostExecute: QCAR " +
                              "initialization successful");

                updateApplicationStatus(APPSTATUS_INIT_TRACKER);
            }
            else
            {
                // Create dialog box for display error:
                AlertDialog dialogError = new AlertDialog.Builder(VideoPlayback.this).create();
                dialogError.setButton
                (
                    DialogInterface.BUTTON_POSITIVE,
                    "Close",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Exiting application:
                            System.exit(1);
                        }
                    }
                );

                String logMessage;

                // NOTE: Check if initialization failed because the device is
                // not supported. At this point the user should be informed
                // with a message.
                if (mProgressValue == QCAR.INIT_DEVICE_NOT_SUPPORTED)
                {
                    logMessage = "Failed to initialize QCAR because this " +
                        "device is not supported.";
                }
                else
                {
                    logMessage = "Failed to initialize QCAR.";
                }

                // Log error:
                DebugLog.LOGE("InitQCARTask::onPostExecute: " + logMessage +
                                " Exiting.");

                // Show dialog box with error message:
                dialogError.setMessage(logMessage);
                dialogError.show();
            }
        }
    }


    */
/** An async task to load the tracker data asynchronously. *//*

    private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean>
    {
        protected Boolean doInBackground(Void... params)
        {
            // Prevent the onDestroy() method to overlap:
            synchronized (mShutdownLock)
            {
                // Load the tracker data set:
                return (loadTrackerData() > 0);
            }
        }

        protected void onPostExecute(Boolean result)
        {
            DebugLog.LOGD("LoadTrackerTask::onPostExecute: execution " +
                        (result ? "successful" : "failed"));

            if (result)
            {
                // Done loading the tracker, update application status:
                updateApplicationStatus(APPSTATUS_INITED);
            }
            else
            {
                // Create dialog box for display error:
                AlertDialog dialogError = new AlertDialog.Builder
                (
                    VideoPlayback.this
                ).create();

                dialogError.setButton
                (
                    DialogInterface.BUTTON_POSITIVE,
                    "Close",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Exiting application
                            System.exit(1);
                        }
                    }
                );

                // Show dialog box with error message:
                dialogError.setMessage("Failed to load tracker data.");
                dialogError.show();
            }
        }
    }

    private void storeScreenDimensions()
    {
        // Query display dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }

    */
/** Called when the activity first starts or the user navigates back
     * to an activity. *//*

    protected void onCreate(Bundle savedInstanceState)
    {
        DebugLog.LOGD("VideoPlayback::onCreate");
        super.onCreate(savedInstanceState);

        // Set the splash screen image to display during initialization:
        mSplashScreenImageResource = R.drawable.splash_screen_video_playback;

        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();

        // Query the QCAR initialization flags:
        mQCARFlags = getInitializationFlags();

        // Update the application status to start initializing application
        updateApplicationStatus(APPSTATUS_INIT_APP);

        // Create the gesture detector that will handle the single and 
        // double taps:
        mSimpleListener = new SimpleOnGestureListener();
        mGestureDetector = new GestureDetector(
            getApplicationContext(), mSimpleListener);

        mVideoPlayerHelper = new VideoPlayerHelper[NUM_TARGETS];
        mSeekPosition = new int[NUM_TARGETS];
        mWasPlaying = new boolean[NUM_TARGETS];
        mMovieName = new String[NUM_TARGETS];

        // Create the video player helper that handles the playback of the movie
        // for the targets:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            mVideoPlayerHelper[i] = new VideoPlayerHelper();
            mVideoPlayerHelper[i].init();
            mVideoPlayerHelper[i].setActivity(this);
        }

        //mMovieName[STONES] = "VuforiaSizzleReel_1.m4v";
        //mMovieName[CHIPS] = "VuforiaSizzleReel_2.m4v";

        mMovieName[STONES] = "https://dl.dropbox.com/u/78179375/Iron_Maiden_1983_Full_Concert.mp4";
        mMovieName[CHIPS] = "http://www.nudedreams.net/mp4s/dance.mp4";
        
        mCurrentActivity = this;

        // Set the double tap listener:
        mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener()
        {
            */
/** Handle the double tap *//*

            public boolean onDoubleTap(MotionEvent e)
            {
                // Do not react if the StartupScreen is being displayed:
                if (mStartScreenShowing)
                    return false;

                for (int i = 0; i < NUM_TARGETS; i++)
                {
                    // Verify that the tap happens inside the target:
                    if (isTapOnScreenInsideTarget(i, e.getX(), e.getY()))
                    {
                        // Check whether we can play full screen at all:
                        if (mVideoPlayerHelper[i].isPlayableFullscreen())
                        {
                            // Pause all other media:
                            pauseAll(i);

                            // Request the playback in fullscreen:
                            mVideoPlayerHelper[i].play(true,VideoPlayerHelper.CURRENT_POSITION);
                        }

                        // Even though multiple videos can be loaded only one 
                        // can be playing at any point in time. This break 
                        // prevents that, say, overlapping videos trigger 
                        // simultaneously playback.
                        break;
                    }
                }

                return true;
            }

            public boolean onDoubleTapEvent(MotionEvent e)
            {
                // We do not react to this event
                return false;
            }

            */
/** Handle the single tap *//*

            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                // Do not react if the StartupScreen is being displayed
                if (mStartScreenShowing)
                    return false;

                for (int i = 0; i < NUM_TARGETS; i++)
                {
                    // Verify that the tap happened inside the target
                    if (isTapOnScreenInsideTarget(i, e.getX(), e.getY()))
                    {
                        // Check if it is playable on texture
                        if (mVideoPlayerHelper[i].isPlayableOnTexture())
                        {
                            // We can play only if the movie was paused, ready or stopped
                            if ((mVideoPlayerHelper[i].getStatus() == VideoPlayerHelper.MEDIA_STATE.PAUSED) ||
                                (mVideoPlayerHelper[i].getStatus() == VideoPlayerHelper.MEDIA_STATE.READY)  ||
                                (mVideoPlayerHelper[i].getStatus() == VideoPlayerHelper.MEDIA_STATE.STOPPED) ||
                                (mVideoPlayerHelper[i].getStatus() == VideoPlayerHelper.MEDIA_STATE.REACHED_END))
                            {
                                // Pause all other media
                                pauseAll(i);

                                // If it has reached the end then rewind
                                if ((mVideoPlayerHelper[i].getStatus() == VideoPlayerHelper.MEDIA_STATE.REACHED_END))
                                    mSeekPosition[i] = 0;

                                mVideoPlayerHelper[i].play(false, mSeekPosition[i]);
                                mSeekPosition[i] = VideoPlayerHelper.CURRENT_POSITION;
                            }
                            else if (mVideoPlayerHelper[i].getStatus() == VideoPlayerHelper.MEDIA_STATE.PLAYING)
                            {
                                // If it is playing then we pause it
                                mVideoPlayerHelper[i].pause();
                            }
                        }
                        else if (mVideoPlayerHelper[i].isPlayableFullscreen())
                        {
                            // If it isn't playable on texture
                            // Either because it wasn't requested or because it
                            // isn't supported then request playback fullscreen.
                            mVideoPlayerHelper[i].play(true,VideoPlayerHelper.CURRENT_POSITION);
                        }

                        // Even though multiple videos can be loaded only one 
                        // can be playing at any point in time. This break 
                        // prevents that, say, overlapping videos trigger 
                        // simultaneously playback.
                        break;
                    }
                }

                return true;
            }
        });
    }


    */
/** We want to load specific textures from the APK, which we will later
     * use for rendering. *//*

    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk("numnum.jpg",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("VuforiaSizzleReel_2.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("play.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("busy.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("error.png",
                getAssets()));
    }


    */
/** Configure QCAR with the desired version of OpenGL ES. *//*

    private int getInitializationFlags()
    {
        return QCAR.GL_20;
    }


    */
/** Native tracker initialization and deinitialization. *//*

    public native int initTracker();
    public native void deinitTracker();

    */
/** Native functions to load and destroy tracking data. *//*

    public native int loadTrackerData();
    public native void destroyTrackerData();

    */
/** Native sample initialization. *//*

    public native void onQCARInitializedNative();

    */
/** Native methods for starting and stopping the camera. *//*

    private native void startCamera();
    private native void stopCamera();

    */
/** Native method for setting / updating the projection matrix for
     * AR content rendering *//*

    private native void setProjectionMatrix();

    private native boolean isTapOnScreenInsideTarget(
        int target, float x, float y);

   */
/** Called when the activity will start interacting with the user.*//*

    protected void onResume()
    {
        DebugLog.LOGD("VideoPlayback::onResume");
        super.onResume();

        // QCAR-specific resume operation
        QCAR.onResume();

        // We may start the camera only if the QCAR SDK has already been
        // initialized:
        if (mAppStatus == APPSTATUS_CAMERA_STOPPED)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
        }

        // Setup the start button:
        setupStartButton();

        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }

        // Do not show the startup screen if we're returning from full screen:
        if (!mReturningFromFullScreen)
            showStartupScreen();

        // Reload all the movies
        if (mRenderer != null)
        {
            for (int i = 0; i < NUM_TARGETS; i++)
            {
                if (!mReturningFromFullScreen)
                {
                    mRenderer.requestLoad(
                        i, mMovieName[i], mSeekPosition[i], false);
                }
                else
                {
                    mRenderer.requestLoad(
                        i, mMovieName[i], mSeekPosition[i], mWasPlaying[i]);
                }
            }
        }

        mReturningFromFullScreen = false;
    }

    */
/** Called when returning from the full screen player *//*

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                // The following values are used to indicate the position in 
                // which the video was being played and whether it was being 
                // played or not:
                String movieBeingPlayed = data.getStringExtra("movieName");
                mReturningFromFullScreen = true;

                // Find the movie that was being played full screen
                for (int i = 0; i < NUM_TARGETS; i++)
                {
                    if (movieBeingPlayed.compareTo(mMovieName[i]) == 0)
                    {
                        mSeekPosition[i] = data.getIntExtra("currentSeekPosition", 0);
                        mWasPlaying[i] = data.getBooleanExtra("playing", false);
                    }
                }
            }
        }
    }

    public void onConfigurationChanged(Configuration config)
    {
        DebugLog.LOGD("VideoPlayback::onConfigurationChanged");
        super.onConfigurationChanged(config);

        storeScreenDimensions();

        // Set projection matrix:
        if (QCAR.isInitialized() && (mAppStatus == APPSTATUS_CAMERA_RUNNING))
            setProjectionMatrix();
    }


    */
/** Called when the system is about to start resuming a previous activity.*//*

    protected void onPause()
    {
        DebugLog.LOGD("VideoPlayback::onPause");
        super.onPause();

        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        if (mAppStatus == APPSTATUS_CAMERA_RUNNING)
        {
            updateApplicationStatus(APPSTATUS_CAMERA_STOPPED);
        }

        // Store the playback state of the movies and unload them:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // If the activity is paused we need to store the position in which 
            // this was currently playing:
            if (mVideoPlayerHelper[i].isPlayableOnTexture())
            {
                mSeekPosition[i] = mVideoPlayerHelper[i].getCurrentPosition();
                mWasPlaying[i] = mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING ? true : false;
            }

            // We also need to release the resources used by the helper, though
            // we don't need to destroy it:
            if (mVideoPlayerHelper[i]!= null)
                mVideoPlayerHelper[i].unload();
        }

        // Hide the Startup View:
        hideStartupScreen();

        mReturningFromFullScreen = false;

        // QCAR-specific pause operation:
        QCAR.onPause();
    }


    */
/** Native function to deinitialize the application.*//*

    private native void deinitApplicationNative();


    */
/** The final call you receive before your activity is destroyed.*//*

    protected void onDestroy()
    {
        DebugLog.LOGD("VideoPlayback::onDestroy");
        super.onDestroy();

        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // If the activity is destroyed we need to release all resources:
            if (mVideoPlayerHelper[i] != null)
                mVideoPlayerHelper[i].deinit();
            mVideoPlayerHelper[i] = null;
        }

        // Dismiss the splash screen time out handler:
        if (mSplashScreenHandler != null)
        {
            mSplashScreenHandler.removeCallbacks(mSplashScreenRunnable);
            mSplashScreenRunnable = null;
            mSplashScreenHandler = null;
        }

        // Cancel potentially running tasks:
        if (mInitQCARTask != null &&
            mInitQCARTask.getStatus() != InitQCARTask.Status.FINISHED)
        {
            mInitQCARTask.cancel(true);
            mInitQCARTask = null;
        }

        if (mLoadTrackerTask != null &&
            mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED)
        {
            mLoadTrackerTask.cancel(true);
            mLoadTrackerTask = null;
        }

        // Ensure that all asynchronous operations to initialize QCAR and 
        // loading the tracker datasets do not overlap:
        synchronized (mShutdownLock) {

            // Do application deinitialization in native code:
            deinitApplicationNative();

            // Unload texture:
            mTextures.clear();
            mTextures = null;

            // Destroy the tracking data set:
            destroyTrackerData();

            // Deinit the tracker:
            deinitTracker();

            // Deinitialize QCAR SDK:
            QCAR.deinit();
        }

        System.gc();
    }


    */
/** NOTE: this method is synchronized because of a potential concurrent
     * access by VideoPlayback::onResume() and InitQCARTask::onPostExecute(). *//*

    private synchronized void updateApplicationStatus(int appStatus)
    {
        // Exit if there is no change in status:
        if (mAppStatus == appStatus)
            return;

        // Store new status value:
        mAppStatus = appStatus;

        // Execute application state-specific actions:
        switch (mAppStatus)
        {
            case APPSTATUS_INIT_APP:
                // Initialize application elements that do not rely on QCAR
                // initialization:
                initApplication();

                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_INIT_QCAR);
                break;

            case APPSTATUS_INIT_QCAR:
                // Initialize QCAR SDK asynchronously to avoid blocking the
                // main (UI) thread.
                //
                // NOTE: This task instance must be created and invoked on the
                // UI thread and it can be executed only once!
                try
                {
                    mInitQCARTask = new InitQCARTask();
                    mInitQCARTask.execute();
                }
                catch (Exception e)
                {
                    DebugLog.LOGE("Initializing QCAR SDK failed");
                }
                break;

            case APPSTATUS_INIT_TRACKER:
                // Initialize the ImageTracker:
                if (initTracker() > 0)
                {
                    // Proceed to next application initialization status:
                    updateApplicationStatus(APPSTATUS_INIT_APP_AR);
                }
                break;

            case APPSTATUS_INIT_APP_AR:
                // Initialize Augmented Reality-specific application elements
                // that may rely on the fact that the QCAR SDK has been
                // already initialized:
                initApplicationAR();

                // Proceed to next application initialization status:
                updateApplicationStatus(APPSTATUS_LOAD_TRACKER);
                break;

            case APPSTATUS_LOAD_TRACKER:
                // Load the tracking data set:
                //
                // NOTE: This task instance must be created and invoked on the 
                // UI thread and it can be executed only once!
                try
                {
                    mLoadTrackerTask = new LoadTrackerTask();
                    mLoadTrackerTask.execute();
                }
                catch (Exception e)
                {
                    DebugLog.LOGE("Loading tracking data set failed");
                }
                break;

            case APPSTATUS_INITED:
                // Hint to the virtual machine that it would be a good time to
                // run the garbage collector.
                //
                // NOTE: This is only a hint. There is no guarantee that the
                // garbage collector will actually be run.
                System.gc();

                // Native post initialization:
                onQCARInitializedNative();

                // The elapsed time since the splash screen was visible:
                long splashScreenTime = System.currentTimeMillis() -
                                            mSplashScreenStartTime;
                long newSplashScreenTime = 0;
                if (splashScreenTime < MIN_SPLASH_SCREEN_TIME)
                {
                    newSplashScreenTime = MIN_SPLASH_SCREEN_TIME -
                                            splashScreenTime;
                }

                // Request a callback function after a given timeout to dismiss
                // the splash screen:
                mSplashScreenHandler = new Handler();
                mSplashScreenRunnable =
                    new Runnable() {
                        public void run()
                        {
                            // Hide the splash screen:
                            mSplashScreenView.setVisibility(View.INVISIBLE);

                            // Activate the renderer:
                            mRenderer.mIsActive = true;

                            // Now add the GL surface view. It is important
                            // that the OpenGL ES surface view gets added
                            // BEFORE the camera is started and video
                            // background is configured.
                            addContentView(mGlView, new LayoutParams(
                                            LayoutParams.MATCH_PARENT,
                                            LayoutParams.MATCH_PARENT));

                            // Setup the start screen:
                            setupStartScreen();

                            // Setup the start button:
                            setupStartButton();

                            // Start the camera:
                            updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
                        }
                };

                mSplashScreenHandler.postDelayed(mSplashScreenRunnable,
                                                    newSplashScreenTime);
                break;

            case APPSTATUS_CAMERA_STOPPED:
                // Call the native function to stop the camera:
                stopCamera();
                break;

            case APPSTATUS_CAMERA_RUNNING:
                // Call the native function to start the camera:
                startCamera();
                setProjectionMatrix();
                
                // Set continuous auto-focus if supported by the device,
                // otherwise default back to regular auto-focus mode.
                mFocusMode = FOCUS_MODE_CONTINUOUS_AUTO;
                if(!setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO))
                {
                    mFocusMode = FOCUS_MODE_NORMAL;
                    setFocusMode(FOCUS_MODE_NORMAL);
                }

                break;

            default:
                throw new RuntimeException("Invalid application state");
        }
    }

    */
/** This call sets the start screen up, adds it to the view and pads the
     * text to something nice *//*

    private void setupStartScreen()
    {
        // Inflate the view from the xml file:
        mStartupView = getLayoutInflater().inflate(
            R.layout.startup_screen, null);

        // Add it to the content view:
        addContentView(mStartupView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        // Align and center the background container for the description:
        ImageView background_view = (ImageView) findViewById(
            R.id.background_view);

        if (background_view != null)
        {
            int paddingHeight = (int) (mScreenHeight*0.10);
            background_view.setPadding(0, paddingHeight, 0, paddingHeight);
            background_view.getLayoutParams().height=(int) (mScreenHeight*0.80);
        }

        // Align and pad the text
        TextView description_text = (TextView)findViewById(
            R.id.description_text);

        if (description_text != null)
        {
            int paddingWidth = (int) (mScreenWidth*0.10);
            int paddingHeight = (int) (mScreenHeight*0.10);
            description_text.setPadding(paddingWidth, paddingHeight*2, 
                paddingWidth, paddingHeight);
            description_text.getLayoutParams().height=(int)(mScreenHeight*0.75);
        }

        // Align and center the background container for the description:
        ImageView start_button = (ImageView) findViewById(R.id.start_button);
        if (start_button != null)
        {
            int paddingWidth = (int) (mScreenWidth*0.05);
            int paddingHeight = (int) (mScreenHeight*0.10);
            start_button.setPadding(0, 0, paddingWidth, paddingHeight);
        }

        mStartScreenShowing = true;
    }

    */
/** This call sets the start button variable up *//*

    private void setupStartButton()
    {
        mStartButton = (ImageView) findViewById(R.id.start_button);

        if (mStartButton != null)
        {
            // Setup a click listener that hides the StartupScreen:
            mStartButton.setOnClickListener(new ImageView.OnClickListener() {
                    public void onClick(View arg0) {
                        hideStartupScreen();
                    }
            });
        }
    }

    */
/** Show the startup screen *//*

    private void showStartupScreen()
    {
        if (mStartupView != null)
        {
            mStartupView.setVisibility(View.VISIBLE);
            mStartScreenShowing = true;
        }
    }

    */
/** Hide the startup screen *//*

    private void hideStartupScreen()
    {
        if (mStartupView != null)
        {
            mStartupView.setVisibility(View.INVISIBLE);
            mStartScreenShowing = false;
        }
    }

    */
/** Pause all movies except one
        if the value of 'except' is -1 then
        do a blanket pause *//*

    private void pauseAll(int except)
    {
        // And pause all the playing videos:
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            // We can make one exception to the pause all calls:
            if (i != except)
            {
                // Check if the video is playable on texture
                if (mVideoPlayerHelper[i].isPlayableOnTexture())
                {
                    // If it is playing then we pause it
                    mVideoPlayerHelper[i].pause();
                }
            }
        }
    }

    */
/** Do not exit immediately and instead show the startup screen *//*

    public void onBackPressed() {

        // If this is the first time the back button is pressed
        // show the StartupScreen and pause all media:
        if (!mStartScreenShowing)
        {
            // Show the startup screen:
            showStartupScreen();

            pauseAll(-1);
        }
        else // if this is the second time the user pressed the back button
        {
            // Hide the Startup View:
            hideStartupScreen();

            // And exit:
            super.onBackPressed();
        }
    }


    */
/** Tells native code whether we are in portait or landscape mode *//*

    private native void setActivityPortraitMode(boolean isPortrait);


    */
/** Initialize application GUI elements that are not related to AR. *//*

    private void initApplication()
    {
        // Set the screen orientation:
        int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        // Apply screen orientation:
        setRequestedOrientation(screenOrientation);

        // Pass on screen orientation info to native code:
        setActivityPortraitMode(
            screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        storeScreenDimensions();

        // As long as this window is visible to the user, keep the device's
        // screen turned on and bright:
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Create and add the splash screen view:
        mSplashScreenView = new ImageView(this);
        mSplashScreenView.setImageResource(mSplashScreenImageResource);
        addContentView(mSplashScreenView, new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mSplashScreenStartTime = System.currentTimeMillis();

    }


    */
/** Native function to initialize the application. *//*

    private native void initApplicationNative(int width, int height);


    */
/** Initializes AR application components. *//*

    private void initApplicationAR()
    {
        // Do application initialization in native code (e.g. registering
        // callbacks, etc.):
        initApplicationNative(mScreenWidth, mScreenHeight);

        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = QCAR.requiresAlpha();

        mGlView = new QCARSampleGLView(this);
        mGlView.init(mQCARFlags, translucent, depthSize, stencilSize);

        mRenderer = new VideoPlaybackRenderer();

        // The renderer comes has the OpenGL context, thus, loading to texture
        // must happen when the surface has been created. This means that we
        // can't load the movie from this thread (GUI) but instead we must
        // tell the GL thread to load it once the surface has been created.
        for (int i = 0; i < NUM_TARGETS; i++)
        {
            mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
            mRenderer.requestLoad(i, mMovieName[i], 0, false);
        }

        mGlView.setRenderer(mRenderer);
    }
    
    */
/** Invoked every time before the options menu gets displayed to give
     *  the Activity a chance to populate its Menu with menu items. *//*

    public boolean onPrepareOptionsMenu(Menu menu) 
    {
        super.onPrepareOptionsMenu(menu);
        
        menu.clear();

        if(mFocusMode == FOCUS_MODE_CONTINUOUS_AUTO)
            menu.add(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS);
        else
            menu.add(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS);

        menu.add(MENU_ITEM_TRIGGER_AUTO_FOCUS);

        return true;
    }

    */
/** Invoked when the user selects an item from the Menu *//*

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getTitle().equals(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS))
        {
            if(setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO))
            {
                mFocusMode = FOCUS_MODE_CONTINUOUS_AUTO;
                item.setTitle(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS);
            }
            else
            {
                Toast.makeText
                (
                    this,
                    "Unable to activate Continuous Auto-Focus",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        else if(item.getTitle().equals(MENU_ITEM_DEACTIVATE_CONT_AUTO_FOCUS))
        {
            if(setFocusMode(FOCUS_MODE_NORMAL))
            {
                mFocusMode = FOCUS_MODE_NORMAL;
                item.setTitle(MENU_ITEM_ACTIVATE_CONT_AUTO_FOCUS);
            }
            else
            {
                Toast.makeText
                (
                    this,
                    "Unable to deactivate Continuous Auto-Focus",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
        else if(item.getTitle().equals(MENU_ITEM_TRIGGER_AUTO_FOCUS))
        {
            boolean result = autofocus();
            
            // Autofocus action resets focus mode
            mFocusMode = FOCUS_MODE_NORMAL;
            
            DebugLog.LOGI
            (
                "Autofocus requested" +
                (result ?
                    " successfully." :
                    ". Not supported in current mode or on this device.")
            );
        }

        return true;
    }
    
    private native boolean autofocus();
    private native boolean setFocusMode(int mode);

    */
/** Returns the number of registered textures. *//*

    public int getTextureCount()
    {
        return mTextures.size();
    }


    */
/** Returns the texture object at the specified index. *//*

    public Texture getTexture(int i)
    {
        return mTextures.elementAt(i);
    }


    */
/** A helper for loading native libraries stored in "libs/armeabi*". *//*

    public static boolean loadLibrary(String nLibName)
    {
        try
        {
            System.loadLibrary(nLibName);
            DebugLog.LOGI("Native library lib" + nLibName + ".so loaded");
            return true;
        }
        catch (UnsatisfiedLinkError ulee)
        {
            DebugLog.LOGE("The library lib" + nLibName +
                            ".so could not be loaded");
        }
        catch (SecurityException se)
        {
            DebugLog.LOGE("The library lib" + nLibName +
                            ".so was not allowed to be loaded");
        }

        return false;
    }

    */
/** We do not handle the touch event here, we just forward it to the
     * gesture detector *//*

    public boolean onTouchEvent(MotionEvent event)
    {
        return mGestureDetector.onTouchEvent(event);
    }
}
*/
