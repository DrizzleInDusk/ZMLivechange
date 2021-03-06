# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in d:\Program Files\Android\sdk/tools/proguard/proguard-android.txt
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
#网易云信的混淆代码
-keep class jp.co.cyberagent.android.gpuimage.** { *; }
-dontwarn com.netease.**
-dontwarn io.netty.**
-keep class com.netease.** {*;}