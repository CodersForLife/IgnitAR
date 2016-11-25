package com.vizy.ignitar.cloud;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.vizy.ignitar.appsession.base.SampleApplicationSession;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.TrackableResult;
import com.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.vuforia.Vuforia;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CloudRecoRenderer implements GLSurfaceView.Renderer {
    SampleApplicationSession vuforiaAppSession;

    private int shaderProgramID;
    private CloudReco mActivity;

    public CloudRecoRenderer(SampleApplicationSession session, CloudReco activity) {
        vuforiaAppSession = session;
        mActivity = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRendering();
        vuforiaAppSession.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }


    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        // Call our function to render content
        renderFrame();
    }


    // Function for initializing the renderer.
    private void initRendering() {
        // Define clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);
    }


    // The render function.
    private void renderFrame() {
        // Clear color and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Get the state from Vuforia and mark the beginning of a rendering
        // section
        State state = Renderer.getInstance().begin();
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW);  // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW);   // Back camera
        // Set the viewport
        int[] viewport = vuforiaAppSession.getViewport();
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
        // Did we find any trackables this frame?
        if (state.getNumTrackableResults() > 0) {
            // Gets current trackable result
            TrackableResult trackableResult = state.getTrackableResult(0);
            if (trackableResult == null) {
                return;
            }
            mActivity.stopFinderIfStarted();
        } else {
            mActivity.startFinderIfStopped();
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        Renderer.getInstance().end();
    }

}
