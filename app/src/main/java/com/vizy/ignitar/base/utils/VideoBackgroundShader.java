package com.vizy.ignitar.base.utils;

public class VideoBackgroundShader
{
    
    public static final String VB_VERTEX_SHADER =
        "attribute vec4 vertexPosition;\n" +
        "attribute vec2 vertexTexCoord;\n" +
        "uniform mat4 projectionMatrix;\n" +
    
        "varying vec2 texCoord;\n" +
       
        "void main()\n" +
        "{\n" +
        "    gl_Position = projectionMatrix * vertexPosition;\n" +
        "    texCoord = vertexTexCoord;\n" +
        "}\n";
    
    public static final String VB_FRAGMENT_SHADER =
        "precision mediump float;\n" +
        "varying vec2 texCoord;\n" +
        "uniform sampler2D texSampler2D;\n" +
        "void main ()\n" +
        "{\n" +
        "    gl_FragColor = texture2D(texSampler2D, texCoord);\n" +
        "}\n";

}
