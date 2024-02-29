# The Android Gradle plugin allows to define ProGuard rules which get embedded in the AAR.
# These ProGuard rules are automatically applied when a consumer app sets minifyEnabled to true.
# The custom rule file must be defined using the 'consumerProguardFiles' property in your
# build.gradle file.

# Maintain JavascriptInterfaces attached to webviews
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers class dev.hotwire.strada.Bridge {
   public *;
}

-keep class dev.hotwire.strada.** { *; }
