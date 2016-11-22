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
    VideoPlaybackRenderer.java

@brief
    Sample for VideoPlayback

==============================================================================*//*



package com.vizy.ignitar.video;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import com.vuforia.*;
import com.vizy.*;

import com.qualcomm.QCAR.QCAR;
import com.vizy.ignitar.video.VideoPlayerHelper.MEDIA_STATE;
import com.vizy.ignitar.video.VideoPlayerHelper.MEDIA_TYPE;



*/
/** The renderer class for the VideoPlayback sample. *//*

public class VideoPlaybackRenderer implements GLSurfaceView.Renderer
{
    public boolean mIsActive                            = false;

    private float[][] mTexCoordTransformationMatrix     = null;
    private VideoPlayerHelper mVideoPlayerHelper     = null;
    private String mMovieName                        = null;
    private MEDIA_TYPE mCanRequestType                = null;
    private int mSeekPosition                         = 0;
    private boolean mShouldPlayImmediately          = true;
    private long mLostTrackingSince                   ;
    private boolean mLoadRequested                    = false;

    public VideoPlaybackRenderer() {

        // Creat an array of the size of the number of targets we have
        mVideoPlayerHelper = new VideoPlayerHelper();
        mMovieName = new String();
      //  mCanRequestType = new MEDIA_TYPE[VideoPlayback.NUM_TARGETS];
        mSeekPosition =-1;
        mShouldPlayImmediately = true;
       */
/* mLostTrackingSince = new Integer();
        mLoadRequested = new boolean();*//*

        mTexCoordTransformationMatrix = new float[0][16];

        // Initialize the arrays to default values

            mVideoPlayerHelper = null;
            mMovieName = "";
            mCanRequestType = MEDIA_TYPE.ON_TEXTURE_FULLSCREEN;
            mSeekPosition = 0;
            mShouldPlayImmediately = false;
            mLostTrackingSince = -1;
            mLoadRequested = false;

    }

    */
/** Store the Player Helper object passed from the main activity *//*

    public void setVideoPlayerHelper(int target, VideoPlayerHelper newVideoPlayerHelper)
    {
        mVideoPlayerHelper = newVideoPlayerHelper;
    }

    public void requestLoad(int target, String movieName, int seekPosition, boolean playImmediately)
    {
        mMovieName= movieName;
        mSeekPosition = seekPosition;
        mShouldPlayImmediately = playImmediately;
        mLoadRequested = true;
    }

    */
/** Native function for initializing the renderer. *//*

    public native void initRendering();

    */
/** Native function to update the renderer *//*

    public native void updateRendering(int width, int height);

    */
/** Native function to retrieve the OpenGL texture *//*

    public native int getVideoTextureID(int target);

    */
/** Native function to pass down the dimensions of the video *//*

    public native void setVideoDimensions(int target, float videoWidth, float videoHeight, float[] textureCoordMatrix);

    */
/** Native call to indicate whether the rendering should display a keyframe or the video data  *//*

    private native void setStatus(int target, int value);

    */
/** Native function that informs whether the target is currently being tracked. *//*

    public native boolean isTracking(int target);

    */
/** Called when the surface is created or recreated. *//*

    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        // Call native function to initialize rendering:
        // The video texture is also created on this step
        initRendering();

        // Call QCAR function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        QCAR.onSurfaceCreated();

            if (mVideoPlayerHelper != null)
            {
                // The VideoPlayerHelper needs to setup a surface texture given the texture id created in native
                // Here we inform the video player that we would like to play the movie
                // both on texture and on full screen
                // Notice that this does not mean that the platform will be able to do what we request
                // After the file has been loaded one must always check with isPlayableOnTexture() whether
                // this can be played embedded in the AR scene
                if (!mVideoPlayerHelper.setupSurfaceTexture(getVideoTextureID(i)))
                    mCanRequestType = MEDIA_TYPE.FULLSCREEN;
                else
                    mCanRequestType = MEDIA_TYPE.ON_TEXTURE_FULLSCREEN;

                // And now check if a load has been requested with the parameters passed from the main activity
                if (mLoadRequested)
                {
                    mVideoPlayerHelper.load(mMovieName[i], mCanRequestType[i], mShouldPlayImmediately[i], mSeekPosition[i]);
                    mLoadRequested = false;
                }

            }
    }


    */
/** Called when the surface changed size. *//*

    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // Call native function to update rendering when render surface parameters have changed:
        updateRendering(width, height);

        // Call QCAR function to handle render surface size changes:
        QCAR.onSurfaceChanged(width, height);

        // Upon every on pause the movie had to be unloaded to release resources
        // Thus, upon every surface create or surface change this has to be reloaded
        // See: http://developer.android.com/reference/android/media/MediaPlayer.html#release()

            if (mLoadRequested && mVideoPlayerHelper != null)
            {
                mVideoPlayerHelper.load(mMovieName, mCanRequestType, mShouldPlayImmediately, mSeekPosition);
                mLoadRequested = false;
            }

    }


    */
/** The native render function. *//*

    public native void renderFrame();

    */
/** Called to draw the current frame. *//*

    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;


            if (mVideoPlayerHelper != null)
            {
                if (mVideoPlayerHelper.isPlayableOnTexture())
                {
                    // First we need to update the video data. This is a built in Android call
                    // Here, the decoded data is uploaded to the OES texture
                    // We only need to do this if the movie is playing
                    if (mVideoPlayerHelper.getStatus() == MEDIA_STATE.PLAYING)
                        mVideoPlayerHelper.updateVideoData();

                    // According to the Android API (http://developer.android.com/reference/android/graphics/SurfaceTexture.html)
                    // transforming the texture coordinates needs to happen every frame.
                    mVideoPlayerHelper.getSurfaceTextureTransformMatrix(mTexCoordTransformationMatrix[i]);
                    setVideoDimensions(0, mVideoPlayerHelper.getVideoWidth(), mVideoPlayerHelper.getVideoHeight(), mTexCoordTransformationMatrix[i]);
                }

                setStatus(0, mVideoPlayerHelper.getStatus().getNumericType());
            }


        // Call our native function to render content
        renderFrame();


            // Ask whether the target is currently being tracked and if so react to it
            if (isTracking(0))
            {
                // If it is tracking reset the timestamp for lost tracking
                mLostTrackingSince= -1;
            }
            else
            {
                // If it isn't tracking
                // check whether it just lost it or if it's been a while
                if (mLostTrackingSince < 0)
                    mLostTrackingSince = SystemClock.uptimeMillis();
                else
                {
                    // If it's been more than 2 seconds then pause the player
                    if ((SystemClock.uptimeMillis()-mLostTrackingSince) > 2000)
                    {
                        if (mVideoPlayerHelper != null)
                            mVideoPlayerHelper.pause();
                    }
                }
            }


        // If you would like the video to start playing as soon as it starts tracking
        // and pause as soon as tracking is lost you can do that here by commenting
        // the for-loop above and instead checking whether the isTracking() value has
        // changed since the last frame. Notice that you need to be careful not to
        // trigger automatic playback for fullscreen since that will be inconvenient
        // for your users.

    }
}
*/
