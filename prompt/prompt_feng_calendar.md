# Prompt: Xây dựng ứng dụng Lịch Âm - Feng Calendar

## Mô tả dự án

Bạn là một senior Android developer. Hãy xây dựng ứng dụng di động **Feng Calendar** — một ứng dụng lịch âm theo phong thủy, dạy math và văn chương cho học sinh THCS.

---

## THÔNG TIN CẤU HÌNH

- **Tên hiển thị:** Lịch Âm - Feng Calen
- **Package name:** `fengcalen`
- **Developer:** Hong
- **Website:** (để trống, hoặc `hong.edu.vn`)
- **Mô tả ngắn:** Ứng dụng lịch âm, phong thủy, bài hát, biệt cảm và sự kiện văn hóa Việt Nam

---

## CÔNG NGHỆ CĂN BẢN (Tech Stack)

- **Framework:** Jetpack Compose (Material3, Glance, Navigation Compose)
- **Dependency Injection:** Hilt
- **Database:** Room
- **Preferences:** DataStore
- **Networking:** Retrofit 3.0 + OkHttp 5.4 (hoặc không dùng lần bắc nếu app nhẹ)
- **Firebase:** Analytics + Remote Config
- **Build Tool:** Gradle, Kotlin 2.x
- **Native:** Dùng JNA / JNIEasy để tải `libandroidx.graphics.path.so` (Cocos2d-x paths kernel) và `libdatastore_shared_counter.so` nếu cần native processing
- **Compose BOM:** 2026.06.01
- **Minimum SDK:** 24+ (Android 7.0)

---

## CẤU TRÚC TÀI NGUYÊN (assets/)

### 1. `assets/alc/`

- **`ca-dao.json`** — 625 ca dao chân dung (danh sách JSON: id, title, lyrics, theme, tags)
- **`cau-doi.json`** — 61 câu đối (danh sách JSON: id, left, right, theme)
- **`greetings.json`** — 460 lời chào văn chương (danh sách JSON: id, greeting, occasion, tone)
- **`lexicon.json`** — Từ điển theo vùng: `{"regions": {"Bac": [...], "Trung": [...], "Nam": [...]}}` (63 tỉnh)

### 2. `assets/occasions/` — 48 file JSON

Mỗi file một sự kiện, cấu trúc: `{ "title": "...", "date": "...", "description": "...", "fengshui": "...", "activities": [...], "todos": [...] }`

Các sự kiện bao gồm:
- Tết (Tết Nguyên Đán, Tết Dương, Tết Dương Âm, Tết Hàn Thực, Tết Duong Lịch, Tết Doan Ngô, Tết Nguyen Dan)
- Rằm (Rằm 1-12 dương, Rằm Tháng 7, Rằm Giêng, Rằm Giáng)
- Đoan Ngọ
- Trung Thu
- Giỗ Tổ (Giỗ Tổ Hùng Vương, Giỗ Tổ Chủ tịch Hồ)
- Giỗ Tổ, Giỗ Tổ Hùng Vương
- Giao Thừa (Giao Thừa Nam, Giao Thừa Bắc, Giao Thừa Trung)
- Mừng 1/1 (Dương, Âm), Mừng 1/2, 1/3, 1/4, 1/5, 1/6, 1/7, 1/8, 1/9, 1/10, 1/11, 1/12
- Các lễ động_chi, lễ nhà, lễ quốc khánh, lễ phat-giao, lễ quốc tế, lễ nhân văn

### 3. `assets/van-khan/` — 14 văn khúc dương nhạc

Files `.md`, mỗi file một văn khúc: `cung-chung-sinh-thang-7.md`, `giao-thua-ngoai-troi.md`, `giao-thua-trong-nha.md`, `gio-thuong.md`, `hoa-vang.md`, `mung-1-tet-gia-tien.md`, `ong-cong-ong-tao.md`, `ram-mung-1-hang-thang.md`, `ram-thang-7-gia-tien.md`, `ram-thang-gieng.md`, `tat-nien.md`, `tet-doan-ngo.md`, `thanh-minh-tao-mo.md`, `tien-thuong.md`

### 4. `assets/regions/provinces.json` — 63 tỉnh/cấp

Danh sách 63 tỉnh kinh doanh.

---

## CẤU TRÚC MẬT GỒM

- **Package:** `fengcalen`
- **Activities:** Home, Calendar, MoonPhase (lịch âm, lunar), FengShui (tính phong thủy), CaDao (bài hát), Language (từ điển, lời chào), Occasions (sự kiện), Settings
- **Services:** Notification service (lịch sự kiện, rằm, tết)
- **BroadcastReceivers:** AlarmReceiver, LocaleReceiver, UpdateReceiver
- **ContentProviders:** AssetProvider (dùng `file://` scheme cho nội dung cấu hình)

---

## TÍNH NĂNG NỔI BẬT

1. **Lịch âm + Tín hiệu phong thủy** — Hiển thị vị trí mặt trăng, öz, ngày lễ, thời điểm hành, điểm âm mệnh, sự kiện văn hóa
2. **Danh sách 48 sự kiện văn hóa** — Bao gồm tất cả các lễ, tết, rằm, đoan-ngọ, trung thu, mừng 1/1
3. **Bài hát + ca dao** — Truy cập 625 ca dao, 61 câu đối, 460 lời chào
4. **Từ điển đa vùng** — Dịch vùng Bắc/Trung/Nam, 63 tỉnh
5. **Bản đồ sự kiện** — Hiển thị xác định ngày lễ theo địa điểm
6. **Thông báo** — Thông báo lễ, tết, rằm, ngày âm lịch tự động tải dựa trên DataStore

---

## TÀI NGUYÊN NẮM (Dùng khi triển khai)

Tất cả nội dung sau nằm trong `assets/alc/`, `assets/occasions/`, `assets/van-khan/`, `assets/regions/`:

- `ca-dao.json` (625 items)
- `cau-doi.json` (61 items)
- `greetings.json` (460 items)
- `lexicon.json` (từ điển theo vùng)
- `occasions/` (48 JSON files)
- `van-khan/` (14 markdown docs)
- `regions/provinces.json` (63 provinces)

---

## PHÂN TÍCH BỒ DÀO (Dựa trên file đã phân tích)

- **Các mục chính:** `ca-dao.json`, `cau-doi.json`, `greetings.json`, `lexicon.json`, `occasions/*.json`, `van-khan/*.md`, `regions/provinces.json`
- **Structure:** JSON với cấu trúc nhất quán, sẵn sàng cho DataStore/SharedPreferences
- **Encoding:** UTF-8, JSON chuẩn
- **Content:** Văn hóa, âm mệnh, lịch âm, phong thủy, lời chào, câu đối, sân khấu âm nhạc truyền thống

---

## TẠO RẤT CHÁY

1. Tạo dự án Android Kotlin Compose với Gradle, TravisCI/CI
2. Cấu hình app tên, package, developer theo trên
3. Copy vào `assets/` tất cả file cấu hình
4. Xây dựng Activities, Services, BroadcastReceivers, ContentProviders
5. Sửa `AndroidManifest.xml` với các thành phần: 5 activities, 2 services, 6 receivers, 2 providers
6. Build APK x86_64/ARM64
7. Kiểm tra native libs và tính năng offline
8. Xuất báo cáo REA đầy đủ

---

## HƯỚNG DẪN

Lead with Renderscript, Kotlin DSL, Gradle Plugins, Hilt, Room, DataStore, Compose Navigation, AAPT2, DX. Use 64-bit, target XXHDPI, include the 48 occasion files, 14 vanKhan files, and 63 province regions.