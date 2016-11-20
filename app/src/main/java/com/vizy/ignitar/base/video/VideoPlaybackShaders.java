package com.vizy.ignitar.base.video;

public class VideoPlaybackShaders {

    public static final String VIDEO_PLAYBACK_VERTEX_SHADER = " \n"
            + "attribute vec4 vertexPosition; \n"
            + "attribute vec2 vertexTexCoord; \n"
            + "varying vec2 texCoord; \n"
            + "uniform mat4 modelViewProjectionMatrix; \n"
            + "\n"
            + "void main() \n"
            + "{ \n"
            + "   gl_Position = modelViewProjectionMatrix * vertexPosition; \n"
            + "   texCoord = vertexTexCoord; \n"
            + "} \n";
    
    /*
     * 
     * IMPORTANT:
     * 
     * The SurfaceTexture functionality from ICS provides the video frames from
     * the movie in an unconventional format. So we cant use Texture2D but we
     * need to use the ExternalOES extension.
     * 
     * Two things that are important in the shader below. The first is the
     * extension declaration (first line). The second is the type of the
     * texSamplerOES uniform.
     */

    public static final String VIDEO_PLAYBACK_FRAGMENT_SHADER = " \n"
            + "#extension GL_OES_EGL_image_external : require \n"
            + "precision mediump float; \n"
            + "varying vec2 texCoord; \n"
            + "uniform samplerExternalOES texSamplerOES; \n" + " \n"
            + "void main() \n"
            + "{ \n"
            + "   gl_FragColor = texture2D(texSamplerOES, texCoord); \n"
            + "} \n";

}
