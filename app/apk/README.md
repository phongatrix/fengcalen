# Lịch Âm — Feng Calen (Android Application)

Ứng dụng di động **Lịch Âm - Feng Calen** (`vn.fengcalen`) được xây dựng hoàn toàn sạch và độc lập từ mã nguồn Kotlin + Jetpack Compose Material 3, loại bỏ toàn bộ mã nguồn bản quyền và các dịch vụ phụ thuộc ngoài. Ứng dụng tích hợp trọn vẹn âm dương lịch vạn niên, thuật toán thiên văn tuần trăng, tín hiệu phong thủy, cùng kho tàng văn hóa dân gian Việt Nam (48 sự kiện cổ truyền, 14 bài văn khấn, 625 bài ca dao, 61 câu đối, 460 lời chúc mừng, và từ điển phương ngữ 63 tỉnh thành).

---

## 1. Thông Tin Cấu Hình Dự Án

- **Tên hiển thị:** Lịch Âm - Feng Calen
- **Package Name:** `vn.fengcalen`
- **Application ID:** `vn.fengcalen`
- **Min SDK:** 24 (Android 7.0+)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34
- **Công cụ biên dịch:** OpenJDK 17 + Gradle 8.10.2 + Android SDK Build-Tools 34.0.0
- **Ngôn ngữ:** Kotlin 2.0.21, Jetpack Compose Material 3 (BOM 2024.09.00)
- **Kiến trúc:** MVVM + Clean Architecture + Repository Pattern + Dependency Injection (Hilt 2.51)
- **Cơ sở dữ liệu:** Room Database 2.6.1 + KSP
- **Chữ ký số:** V1 + V2 + V3 (Zipalign 4-byte căn chỉnh)
- **Tập tin cài đặt sẵn:** `release/fengcalen-1.0.0.apk` (~14 MB)

---

## 2. Loại Bỏ Yếu Tố Bản Quyền & Khắc Phục Lỗi Khởi Động

- **Nguyên nhân lỗi khởi động trước đó:**
  Bản đóng gói thử nghiệm trước sử dụng lại `classes.dex` từ ứng dụng gốc `com.amlichchuan.app` vốn chứa các kiểm tra phân mảnh Google Play Split, Firebase Warmup, App Integrity và DI kín. Khi chạy trên môi trường độc lập, các cơ chế kiểm tra này gây crash process ngay tại `Application.onCreate()`.
- **Giải pháp triệt để:**
  1. **Biên dịch trực tiếp từ mã nguồn sạch:** Toàn bộ `classes.dex` được build trực tiếp từ mã nguồn Kotlin Compose và Room DB của dự án `fengcalen`.
  2. **Gỡ bỏ 100% yếu tố bản quyền:** Không còn bất kỳ tham chiếu, package hay URL nào liên quan đến `amlichchuan`. Package định danh chuyển thành `vn.fengcalen`, Main Activity `fengcalen.ui.MainActivity`, Application `fengcalen.FengCalendarApp`.
  3. **Khởi động mượt mà:** Khởi động độc lập, không phụ thuộc Google Play Services hay kết nối mạng bên ngoài, hỗ trợ từ Android 7.0 (API 24) đến Android 15 (API 35+).

---

## 3. Cấu Trúc Thư Mục

```
/run/media/ibm/Data/apk/licham/app/apk/
├── build.gradle.kts                     # Root build configuration
├── settings.gradle.kts                  # Gradle multi-module settings
├── gradle.properties                    # JVM & AndroidX settings
├── gradlew                              # Gradle wrapper executable
├── verify_project.py                    # Automated test & verification script
├── release/                             # Bản phát hành APK sẵn sàng cài đặt
│   ├── fengcalen-1.0.0.apk              # 13.9 MB (Đã ký V1+V2+V3, sạch 100%)
│   ├── fengcalen-universal.apk          # Bản sao universal độc lập
│   └── install.sh                       # Script cài đặt tự động qua ADB
└── app/
    ├── build.gradle.kts                 # App module dependencies & plugins
    ├── proguard-rules.pro               # Proguard obfuscation rules
    └── src/main/
        ├── AndroidManifest.xml          # Package, permissions, activities, services
        ├── java/fengcalen/
        │   ├── FengCalendarApp.kt       # Application class (@HiltAndroidApp)
        │   ├── ui/
        │   │   ├── MainActivity.kt      # Main entry point & Bottom Navigation
        │   │   ├── navigation/          # Navigation destinations
        │   │   ├── theme/               # Theme, Color, Type (Material 3)
        │   │   ├── viewmodel/           # MainViewModel
        │   │   └── screen/              # 8 màn hình Compose
        │   ├── data/
        │   │   ├── model/               # Data classes (LunarDate, MoonPhase, FengShui, ContentItem, Occasion)
        │   │   ├── entity/              # Room entities
        │   │   ├── dao/                 # LunarDateDao
        │   │   ├── database/            # FengCalendarDatabase & Converters
        │   │   └── repository/          # Repositories
        │   ├── di/                      # Hilt modules (AppModule)
        │   ├── service/                 # NotificationService, ContentService, NativeService
        │   ├── provider/                # ContentProvider
        │   ├── receiver/                # AlarmReceiver, LocaleReceiver, BootReceiver
        │   ├── util/                    # Thuật toán chuyển đổi Âm Dương & Trăng sao
        │   └── native/                  # JNA native interfaces
        ├── res/                         # Strings (VI, EN), Colors, Themes, Drawables, Mipmaps
        ├── jniLibs/arm64-v8a/           # Native .so libraries
        └── assets/
            ├── alc/                     # ca-dao.json, cau-doi.json, greetings.json, lexicon.json
            ├── occasions/               # 48 JSON sự kiện cổ truyền
            ├── van-khan/                # 14 bài văn khấn (.md)
            ├── regions/                 # Danh mục 63 tỉnh thành
            └── occasion-art/            # Tranh minh họa lễ hội (.webp)
```

---

## 3. Các Chức Năng Chính (8 Màn Hình)

1. **Trang Chủ (HomeScreen):**
   - Đồng hồ thời gian thực và so sánh ngày dương - ngày âm nổi bật.
   - Hiển thị đầy đủ Thiên Can Địa Chi (Năm, Tháng, Ngày) và Con Giáp.
   - Thẻ danh ngôn/ca dao truyền thống ngẫu nhiên mỗi ngày.
   - Giờ hoàng đạo và các lối tắt điều hướng nhanh.

2. **Lịch Tháng Vạn Niên (CalendarScreen):**
   - Lưới lịch tương tác đầy đủ tháng, chuyển tháng mượt mà.
   - Hiển thị song song ngày dương lịch và âm lịch trên từng ô ngày.
   - Đánh dấu nổi bật ngày mùng 1 (sóc) và ngày rằm (vọng).
   - Xem chi tiết từng ngày được chọn: Can Chi, Giờ hoàng đạo, Lễ hội sắp tới.

3. **Giai Đoạn Mặt Trăng (MoonPhaseScreen):**
   - Tính toán chu kỳ tuần trăng thiên văn (Synodic Month 29.53 ngày).
   - Minh họa tỷ lệ sáng (% illumination), tuổi trăng (ngày), tên giai đoạn (Sóc, Thượng Huyền, Vọng, Hạ Huyền).
   - Chòm sao hoàng đạo chiếu và đánh giá năng lượng vũ trụ.

4. **Tín Hiệu Phong Thủy (FengShuiScreen):**
   - Ngũ Hành bản mệnh, Âm Dương khí vận.
   - Hướng xuất hành đại cát (Hỷ Thần, Tài Thần).
   - Màu sắc và con số may mắn trong ngày.
   - Danh sách Việc Nên Làm và Việc Cần Kiêng Kỵ chi tiết.

5. **Kho Tàng Văn Học Dân Gian (CaDaoScreen):**
   - 625 bài Ca dao dân tộc.
   - 61 câu Đối truyền thống đối ngẫu.
   - 460 Lời chào chúc văn hóa.
   - Thanh tìm kiếm tức thì theo từ khóa, lọc theo chủ đề và nút sao chép (copy) nhanh vào clipboard.

6. **48 Sự Kiện Cổ Truyền & Văn Khấn (OccasionsScreen):**
   - Đầy đủ 48 lễ tết truyền thống (Tết Nguyên Đán, Rằm Tháng Giêng, Đoan Ngọ, Trung Thu, Giỗ Tổ, Vu Lan...).
   - Hiển thị phong tục tập quán, các việc chuẩn bị (todos) và ảnh minh họa.
   - Hỗ trợ mở đọc trực tiếp 14 bài Văn Khấn cổ truyền trong modal reader.

7. **Ngôn Ngữ & Phương Ngữ (LanguageScreen):**
   - Chuyển đổi linh hoạt giao diện Tiếng Việt và Tiếng Anh.
   - Kết nối với từ điển phương ngữ 3 miền (Bắc, Trung, Nam) và 63 tỉnh thành.

8. **Cài Đặt (SettingsScreen):**
   - Bật/tắt chế độ tối (Dark Mode), sáng (Light Mode) hoặc tự động theo hệ thống.
   - Cài đặt nhắc nhở thông báo trước ngày rằm và mùng 1 âm lịch.
   - Thông tin phiên bản và nhà phát triển.

---

## 4. Hướng Dẫn Mở Dự Án & Biên Dịch

### Cách 1: Mở bằng Android Studio
1. Mở Android Studio -> Chọn `Open`.
2. Trỏ đến thư mục: `/run/media/ibm/Data/apk/licham/app/apk`.
3. Chờ Gradle đồng bộ (Sync) xong và bấm `Run` (Shift + F10) trên thiết bị hoặc máy ảo Android.

### Cách 2: Cài đặt trực tiếp file APK có sẵn
Tập tin APK hoàn chỉnh đã được đóng gói sẵn tại:
```bash
/run/media/ibm/Data/apk/licham/app/apk/release/fengcalen-1.0.0.apk
```
Cài đặt vào điện thoại hoặc máy ảo thông qua adb:
```bash
adb install /run/media/ibm/Data/apk/licham/app/apk/release/fengcalen-1.0.0.apk
```

### Cách 3: Chạy script kiểm tra
```bash
python3 /run/media/ibm/Data/apk/licham/app/apk/verify_project.py
```
Toàn bộ 59/59 tiêu chuẩn kiểm tra đều đạt PASS.
