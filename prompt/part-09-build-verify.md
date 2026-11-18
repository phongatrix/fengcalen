# PART 09 — Build & Verify (Build APK, REA Analysis, Report)

## Mục tiêu

- Biên dịch APK
- Chạy REA phân tích APK
- Sinh báo cáo cuối cùng
- Thu thập thông tin thiết lập

## Thực hiện

```bash
cd /home/ibm/rea-analyze

# 0. Sửa build.gradle để publish báo cáo REA
cat > settings.gradle << 'EOF'
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
EOF

# 1. Build APK
./gradlew clean assembleDebug

# 2. Kiểm tra APK
ls -la app/build/outputs/apk/debug/

# 3. Chạy REA trên APK
rea app/build/outputs/apk/debug/app-debug.apk

# 4. Thông tin REA
echo "=== REA Report ==="
ls app/build/outputs/apk/debug/re-*.md 2>/dev/null
ls app/build/outputs/apk/debug/re-*.pdf 2>/dev/null

# 5. Kiểm tra REA JSON
echo "=== REA JSON Summary ==="
cat app/build/outputs/apk/debug/re-*.json 2>/dev/null | head -50
```

## Kiểm tra Part 09

Sau khi chạy, kiểm tra:
- [ ] APK x86_64/ARM64 tồn tại
- [ ] REA đã chạy và tạo report
- [ ] Báo cáo HTML/PDF cuối cùng có

## Lệnh kiểm tra

```bash
cd /home/ibm/rea-analyze
echo "=== Kiểm tra Part 09 ==="
ls app/build/outputs/apk/debug/*.apk 2>/dev/null
ls app/build/outputs/apk/debug/re-*.md 2>/dev/null
ls app/build/outputs/apk/debug/re-*.pdf 2>/dev/null
```
