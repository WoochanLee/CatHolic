# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.yanzhenjie.curban.**
-keep class com.yanzhenjie.curban.**{*;}

-dontwarn com.yanzhenjie.loading.**
-keep class com.yanzhenjie.loading.**{*;}

-keep class androidx.appcompat.widget.** { *; }

-keep class com.woody.cat.holic.framework.db.dao.** { *; }
-keepclassmembers class com.woody.cat.holic.framework.db.dao.** { *; }

-keep class com.woody.cat.holic.framework.db.model.** { *; }
-keepclassmembers class com.woody.cat.holic.framework.db.model.** { *; }

-keep class com.woody.cat.holic.framework.net.dto.** { *; }
-keepclassmembers class com.woody.cat.holic.framework.net.dto.** { *; }

-keep class com.woody.cat.holic.domain.** { *; }
-keepclassmembers class com.woody.cat.holic.domain.** { *; }

-keep class com.woody.cat.holic.data.common.** { *; }
-keepclassmembers class com.woody.cat.holic.data.common.** { *; }

-keepclassmembers enum * { *; }