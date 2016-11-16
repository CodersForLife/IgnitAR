# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Users\Myrefers\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-ignorewarnings
-keepattributes Signature
#Crashlytics
-keepattributes SourceFile, LineNumberTable, *Annotation*
-keep public class * extends java.lang.Exception
#firebase
-keep class com.firebase.** { *; }
-keep class com.google.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn com.firebase.**
-dontwarn com.google.firebase.**
#Gson
-keepattributes EnclosingMethod
-keep class com.google.gson.** { *; }
#okhttp v3
-keep class com.squareup.okhttp.** {*;}
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.internal
#default android http class
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
#jackson
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keep class org.codehaus.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }
#vuforia
#-keep class com.vuforia.** { *; }
#-dontnote class com.vuforia.** { *; }
#-keep class com.vuforia.ar.pl.** { *; }
#-dontwarn class com.vuforia.ar.pl.ODGR7Controller
#-dontwarn class com.vuforia.** { *; }
#-dontwarn class com.vuforia.ar.pl.** { *; }
#-dontwarn interface com.vuforia.ar.pl.** { *; }
#-keep class com.ti.s3d.** { *; }
#-dontwarn class com.ti.s3d.** { *; }
#-do class com.ti.s3d.** { *; }
#-keep class com.osterhoutgroup.api.ext.** { *; }
#-dontwarn class com.osterhoutgroup.api.ext.** { *; }