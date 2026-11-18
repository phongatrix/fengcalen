# Proguard rules for Feng Calendar (Lịch Âm)

# Room Database & SQLite
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.TypeConverters class * { *; }

# Data Models & Entities
-keep class fengcalen.data.model.** { *; }
-keep class fengcalen.data.entity.** { *; }
-keep class fengcalen.data.dao.** { *; }
-keep class fengcalen.data.database.** { *; }
-keep class fengcalen.data.repository.** { *; }

# Application Components & Lifecycle
-keep class fengcalen.FengCalendarApp { *; }
-keep class fengcalen.ui.MainActivity { *; }
-keep class fengcalen.receiver.** { *; }
-keep class fengcalen.service.** { *; }
-keep class fengcalen.widget.** { *; }

# Hilt & Dependency Injection
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Compose & Material 3
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Resource IDs for Sound & Reflection
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep Sound Helper
-keep class fengcalen.util.NokiaSoundHelper { *; }
-keep class fengcalen.util.NokiaToneItem { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keepclassmembers enum * { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**
