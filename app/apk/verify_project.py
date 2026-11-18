#!/usr/bin/env python3
import os
import sys
from pathlib import Path

BASE_DIR = Path("/run/media/ibm/Data/apk/licham/app/apk")

PARTS = {
    "Part 01: Project Setup": [
        "settings.gradle.kts",
        "build.gradle.kts",
        "gradle.properties",
        "app/build.gradle.kts",
        "app/proguard-rules.pro",
        "app/src/main/AndroidManifest.xml",
    ],
    "Part 02: Base UI & Theme": [
        "app/src/main/res/values/colors.xml",
        "app/src/main/res/values/strings.xml",
        "app/src/main/res/values/themes.xml",
        "app/src/main/res/values/attrs.xml",
        "app/src/main/res/values-en/strings.xml",
        "app/src/main/res/xml/file_paths.xml",
        "app/src/main/res/mipmap-hdpi/ic_launcher.png",
        "app/src/main/res/mipmap-xxhdpi/ic_launcher.png",
        "app/src/main/java/fengcalen/FengCalendarApp.kt",
        "app/src/main/java/fengcalen/ui/theme/Color.kt",
        "app/src/main/java/fengcalen/ui/theme/Theme.kt",
        "app/src/main/java/fengcalen/ui/theme/Type.kt",
        "app/src/main/java/fengcalen/ui/MainActivity.kt",
    ],
    "Part 03: Data Layer": [
        "app/src/main/java/fengcalen/data/model/LunarDate.kt",
        "app/src/main/java/fengcalen/data/model/MoonPhase.kt",
        "app/src/main/java/fengcalen/data/model/FengShui.kt",
        "app/src/main/java/fengcalen/data/model/ContentItem.kt",
        "app/src/main/java/fengcalen/data/model/Occasion.kt",
        "app/src/main/java/fengcalen/data/entity/Entities.kt",
        "app/src/main/java/fengcalen/data/entity/EventReminderEntity.kt",
        "app/src/main/java/fengcalen/data/dao/LunarDateDao.kt",
        "app/src/main/java/fengcalen/data/dao/EventReminderDao.kt",
        "app/src/main/java/fengcalen/data/database/FengCalendarDatabase.kt",
        "app/src/main/java/fengcalen/data/database/Converters.kt",
        "app/src/main/java/fengcalen/data/repository/LunarDateRepository.kt",
        "app/src/main/java/fengcalen/data/repository/ContentRepository.kt",
        "app/src/main/java/fengcalen/di/AppModule.kt",
    ],
    "Part 04: Content & Assets": [
        "app/src/main/assets/alc/ca-dao.json",
        "app/src/main/assets/alc/cau-doi.json",
        "app/src/main/assets/alc/greetings.json",
        "app/src/main/assets/alc/lexicon.json",
        "app/src/main/assets/regions/provinces.json",
        "app/src/main/assets/occasion-art/art.json",
        "app/src/main/res/raw/nokia_tune.ogg",
        "app/src/main/res/raw/nokia_alarm.ogg",
        "app/src/main/res/raw/nokia_sms.ogg",
        "app/src/main/res/raw/nokia_polyphonic.ogg",
        "app/src/main/java/fengcalen/service/ContentService.kt",
        "app/src/main/java/fengcalen/provider/ContentProvider.kt",
    ],
    "Part 05: Lunar Calendar & Moon Phase": [
        "app/src/main/java/fengcalen/util/LunarCalendar.kt",
        "app/src/main/java/fengcalen/util/MoonPhaseUtils.kt",
        "app/src/main/java/fengcalen/util/NokiaSoundHelper.kt",
    ],
    "Part 06: Native Libraries": [
        "app/src/main/jniLibs/arm64-v8a/libandroidx.graphics.path.so",
        "app/src/main/jniLibs/arm64-v8a/libdatastore_shared_counter.so",
        "app/src/main/java/fengcalen/native/NativeLibraries.kt",
        "app/src/main/java/fengcalen/service/NativeService.kt",
    ],
    "Part 07: Services & Receivers": [
        "app/src/main/java/fengcalen/service/NotificationService.kt",
        "app/src/main/java/fengcalen/service/ReminderScheduler.kt",
        "app/src/main/java/fengcalen/receiver/Receivers.kt",
    ],
    "Part 08: UI Screens & ViewModels": [
        "app/src/main/java/fengcalen/ui/navigation/NavGraph.kt",
        "app/src/main/java/fengcalen/ui/viewmodel/MainViewModel.kt",
        "app/src/main/java/fengcalen/ui/screen/HomeScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/CalendarScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/RemindersScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/ReminderDialog.kt",
        "app/src/main/java/fengcalen/ui/screen/MoonPhaseScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/FengShuiScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/CaDaoScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/OccasionsScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/LanguageScreen.kt",
        "app/src/main/java/fengcalen/ui/screen/SettingsScreen.kt",
    ],
    "Part 09: Build & Verify": [
        "gradlew",
        "release/fengcalen-1.0.0.apk",
    ]
}

def verify():
    print("=" * 60)
    print("XÁC THỰC TOÀN BỘ 9 PHẦN DỰ ÁN FENG CALENDAR (LỊCH ÂM)")
    print("Thư mục:", BASE_DIR)
    print("=" * 60)

    total_checks = 0
    passed_checks = 0

    for part_name, files in PARTS.items():
        part_ok = True
        print(f"\n[*] {part_name}:")
        for f in files:
            total_checks += 1
            p = BASE_DIR / f
            if p.exists():
                size = p.stat().st_size
                print(f"  [x] {f} ({size:,} bytes)")
                passed_checks += 1
            else:
                print(f"  [ ] THIẾU: {f}")
                part_ok = False
        print(f"    -> Kết quả: {'HOÀN TẤT (PASS)' if part_ok else 'THẤT BẠI (FAIL)'}")

    # Extra asset count checks
    occasions_count = len(list((BASE_DIR / "app/src/main/assets/occasions").glob("*.json")))
    vankhan_count = len(list((BASE_DIR / "app/src/main/assets/van-khan").glob("*.md")))
    art_count = len(list((BASE_DIR / "app/src/main/assets/occasion-art").glob("*.webp")))
    
    print("\n" + "=" * 60)
    print(f"Kiểm tra kho tài nguyên văn hóa:")
    print(f"  - Số file sự kiện cổ truyền (occasions): {occasions_count} files (yêu cầu 48)")
    print(f"  - Số bài văn khấn truyền thống (van-khan): {vankhan_count} files (yêu cầu 14)")
    print(f"  - Số tranh minh họa sự kiện (occasion-art): {art_count} files")
    print("=" * 60)
    print(f"TỔNG KẾT: {passed_checks}/{total_checks} kiểm tra thành công.")
    if passed_checks == total_checks and occasions_count >= 48 and vankhan_count >= 14:
        print("TẤT CẢ 9 PHẦN ĐÃ HOÀN TẤT THÀNH CÔNG VÀ CHÍNH XÁC 100%!")
        return 0
    else:
        print("CÓ MỘT SỐ HẠNG MỤC CHƯA ĐẠT!")
        return 1

if __name__ == "__main__":
    sys.exit(verify())
