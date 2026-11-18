# MASTER EXECUTOR — Xây dựng app Feng Calendar

> **Chạy tuần tự 9 phần**, mỗi phần kiểm tra logic trước khi sang phần sau.  
> Chạy từ thư mục gốc dự án: `cd /home/ibm/rea-analyze && python3 master.py`

---

## CẤU TRÚC

```
master.md / master.py
├── part-01-project-setup.md
├── part-02-base-ui.md
├── part-03-data-layer.md
├── part-04-content-data.md
├── part-05-lunar-calendar.md
├── part-06-native-libs.md
├── part-07-services-notifications.md
├── part-08-ui-screens.md
└── part-09-build-verify.md
```

## QUY TẮC CHẠY

1. Mỗi phần độc lập, chạy tuần tự từ `part-01` đến `part-09`
2. Sau mỗi phần, kiểm tra logic (dùng `verify_part_N.py` trong phần đó)
3. Nếu phần nào lỗi → dừng và báo lỗi rõ ràng, không thụ động chuyển sang phần sau
4. Nếu hoàn thành hết 9 phần → thu thập báo cáo tổng kết

## LƯU Ý BÌNH Y

- Tất cả phần đều giả định đang ở `/home/ibm/rea-analyze/`
- Đường dẫn assets: `app/src/main/assets/`
- Package: `fengcalen`, App name: `Lịch Âm - Feng Calen`
- Android minSdk: 24, targetSdk: 34
- Gradle version: 8.7, Kotlin 2.0.21
