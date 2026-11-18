#!/bin/bash
# Script cài đặt trực tiếp APK lên thiết bị Android qua ADB
APK_DIR="$(cd "$(dirname "$0")" && pwd)"
APK_FILE="$APK_DIR/fengcalen-1.0.0.apk"

if ! command -v adb &> /dev/null; then
    echo "[!] Không tìm thấy lệnh 'adb'. Hãy đảm bảo Android platform-tools đã được cài đặt."
    echo "[*] Bạn có thể chép trực tiếp file '$APK_FILE' vào điện thoại và nhấn Cài đặt (Install)."
    exit 1
fi

echo "[*] Đang kiểm tra thiết bị Android kết nối..."
adb devices

echo "[*] Bắt đầu cài đặt $APK_FILE..."
adb install -r "$APK_FILE"

if [ $? -eq 0 ]; then
    echo "[✓] Cài đặt ứng dụng thành công!"
else
    echo "[!] Cài đặt thất bại. Kiểm tra quyền 'Cài đặt ứng dụng không rõ nguồn gốc' trên điện thoại."
fi
