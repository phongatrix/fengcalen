# Proguard rules for Feng Calendar
-keep class fengcalen.data.model.** { *; }
-keep class fengcalen.data.entity.** { *; }
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.Library { public *; }
