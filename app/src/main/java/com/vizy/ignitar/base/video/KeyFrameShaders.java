package com.vizy.ignitar.base.video;

public class KeyFrameShaders {

    public static final String KEY_FRAME_VERTEX_SHADER = " \n"
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

    public static final String KEY_FRAME_FRAGMENT_SHADER = " \n"
            + "\n"
            + "precision mediump float; \n"
            + "varying vec2 texCoord; \n"
            + "uniform sampler2D texSampler2D; \n"
            + " \n"
            + "void main() \n"
            + "{ \n"
            + "   gl_FragColor = texture2D(texSampler2D, texCoord); \n"
            + "} \n";

}
