# PART 01 — Project Setup (cấu trúc dự án, build files, AndroidManifest)

## Mục tiêu

Tạo cấu trúc dự án Android hoàn chỉnh với:
- Cấu trúc thư mục chuẩn (Gradle, Android SDK)
- Build files: `settings.gradle.kts`, `build.gradle.kts` (root), `app/build.gradle.kts`
- `AndroidManifest.xml` với package `fengcalen`, app name "Lịch Âm - Feng Calen"
- Thư mục assets: `assets/alc/`, `assets/occasions/`, `assets/van-khan/`, `assets/regions/`

## Thực hiện

```bash
cd /home/ibm/rea-analyze
mkdir -p app/src/main/java/fengcalen
mkdir -p app/src/main/res/values
mkdir -p app/src/main/assets/alc
mkdir -p app/src/main/assets/occasions
mkdir -p app/src/main/assets/van-khan
mkdir -p app/src/main/assets/regions
mkdir -p app/src/main/java/fengcalen/ui
mkdir -p app/src/main/java/fengcalen/screen
mkdir -p app/src/main/java/fengcalen/data
mkdir -p app/src/main/java/fengcalen/native
```

## Tạo settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Feng Calendar"
include(":app")
```

## Tạo build.gradle.kts (root)

```kotlin
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}

task("clean") {
    delete("build")
}
```

## Tạo app/build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "fengcalen"
    compileSdk = 34

    defaultConfig {
        applicationId = "fengcalen"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("com.google.dagger:hilt-android:2.51")
    implementation("androidx.hilt:hilt-compiler:1.2.0")
    ksp("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.firebase:firebase-analytics:22.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

## Tạo AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:name=".FengCalendarApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Lịch Âm - Feng Calen"
        android:theme="@style/Theme.FengCalendar"
        tools:targetApi="34">

        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:label="Lịch Âm - Feng Calen"
            android:theme="@style/Theme.FengCalendar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".service.NotificationService"
            android:exported="false" />

        <receiver
            android:name=".receiver.AlarmReceiver"
            android:exported="false" />

        <receiver
            android:name=".receiver.LocaleReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.LOCALE_CHANGED" />
            </intent-filter>
        </receiver>

        <receiver
            android:name=".receiver.BootReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>

    <!-- LGPL Native Libraries -->
    <uses-library android:name="androidx.graphics.path" android:required="false" />
    <uses-library android:name="datastore_shared_counter" android:required="false" />

</manifest>
```

## Kiểm tra Part 01

Sau khi chạy, kiểm tra:
- [ ] `settings.gradle.kts` tồn tại
- [ ] `build.gradle.kts` (root) tồn tại
- [ ] `app/build.gradle.kts` tồn tại
- [ ] `AndroidManifest.xml` có `package="fengcalen"` và `label="Lịch Âm - Feng Calen"`
- [ ] Thư mục assets 4 cái tồn tại: `alc`, `occasions`, `van-khan`, `regions`
- [ ] Thư mục java 3 cái: `ui`, `screen`, `data`, `native`

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra cấu trúc ==="
ls -la app/src/main/java/fengcalen/ 2>/dev/null
find app/src/main/assets -type d | sort
echo "=== Kiểm tra Gradle ==="
test -f settings.gradle.kts && echo "settings OK"
test -f build.gradle.kts && echo "root build OK"
test -f app/build.gradle.kts && echo "app build OK"
test -f app/src/main/AndroidManifest.xml && echo "manifest OK"
```
