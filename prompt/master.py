#!/usr/bin/env python3
"""
MASTER EXECUTOR — Xây dựng app Feng Calendar

Chạy tuần tự 9 phần, mỗi phần:
1. Parse phần markdown
2. Trích xuất bash blocks và thực thi
3. Kiểm tra logic sau khi thực thi
4. Report kết quả
"""

import os
import subprocess
import sys
import re
import shutil
from pathlib import Path
from datetime import datetime

# ========================== CONFIG ==========================
PROJECT_DIR = Path("/home/ibm/rea-analyze")
PARTS = [
    "part-01-project-setup.md",
    "part-02-base-ui.md",
    "part-03-data-layer.md",
    "part-04-content-data.md",
    "part-05-lunar-calendar.md",
    "part-06-native-libs.md",
    "part-07-services-notifications.md",
    "part-08-ui-screens.md",
    "part-09-build-verify.md",
]

REPORT_FILE = PROJECT_DIR / "master_report.md"
PROMPT_DIR = Path("/run/media/ibm/Data/apk/licham/prompt")

# ========================== UTILITIES ==========================

def log(msg: str, level: str = "INFO"):
    stamp = datetime.now().strftime("%H:%M:%S")
    print(f"[{stamp}] [{level}] {msg}")

def run_command(cmd: str, cwd: str = None):
    """Run shell command, return (rc, stdout, stderr)."""
    log("  $ " + cmd)
    try:
        result = subprocess.run(
            cmd, shell=True, cwd=cwd or str(PROJECT_DIR),
            capture_output=True, text=True, timeout=180
        )
        out = result.stdout.strip()
        err = result.stderr.strip()
        if out:
            for line in out.splitlines()[:5]:
                log("    " + line)
        if err:
            for line in err.splitlines()[:3]:
                log("    [stderr] " + line)
        return result.returncode, out, err
    except subprocess.TimeoutExpired:
        log("  TIMEOUT", "ERROR")
        return -1, "", "Timeout"
    except Exception as e:
        log("  ERROR: " + str(e), "ERROR")
        return -1, "", str(e)

def extract_bash_blocks(md_path: Path) -> list:
    """Extract bash code blocks from markdown."""
    if not md_path.exists():
        return []
    text = md_path.read_text(encoding="utf-8")
    blocks = re.findall(r"```bash\s*(.*?)\n```", text, re.DOTALL)
    return blocks

def extract_checklist(md_path: Path) -> list:
    """Extract checklist items (format: [x] or [ ])."""
    if not md_path.exists():
        return []
    text = md_path.read_text(encoding="utf-8")
    checks = []
    for line in text.splitlines():
        line = line.strip()
        m = re.match(r'^[-*]\s*\[([ x])\]\s*(.+)$', line)
        if m:
            checks.append((m.group(1) == 'x', m.group(2).strip()))
    return checks

def verify_logic(md_path: Path) -> bool:
    """Post-run verification: check each checklist item against the file system."""
    if not md_path.exists():
        return False
    text = md_path.read_text(encoding="utf-8")
    all_ok = True
    for is_checked, desc in extract_checklist(md_path):
        if is_checked:
            # For [x] (check to verify), check the filesystem
            keywords = desc.lower().replace('[x]', '').strip()
            if 'n/a' not in keywords and 'n/a' not in desc.lower():
                if 'tồn tại' in desc:
                    for f in re.findall(r'`([^`]+)`', desc):
                        p = PROJECT_DIR / f
                        if not p.exists():
                            log("  [" + f + "] DID NOT FIND: " + desc, "WARNING")
                            all_ok = False
    return all_ok

def execute_part(part_num: int, part_file: str) -> bool:
    """Execute a part: extract bash, run, verify."""
    log("\n" + "=" * 60, "INFO")
    log("PART " + str(part_num) + ": " + part_file, "INFO")
    log("=" * 60, "INFO")

    part_path = PROJECT_DIR / part_file
    if not part_path.exists():
        log("  ERROR: Not found: " + str(part_path), "ERROR")
        return False

    content = part_path.read_text(encoding="utf-8")
    log("  Size: " + str(len(content)) + " chars")

    # Extract bash blocks
    bash_blocks = extract_bash_blocks(part_path)
    log("  Found " + str(len(bash_blocks)) + " bash block(s)")

    for idx, block in enumerate(bash_blocks):
        log("  Block " + str(idx+1) + ": running...")
        rc, stdout, stderr = run_command(block.strip())
        if rc != 0:
            log("  Block " + str(idx+1) + ": exit code " + str(rc) + " (non-zero)", "WARNING")

    # Verify
    passed = verify_logic(part_path)
    status = "PASS" if passed else "FAIL"
    log("\nPART " + str(part_num) + ": " + status, "SUCCESS" if passed else "ERROR")
    return passed

def create_structure():
    """Create directory structure."""
    log("\n>>> Creating project structure...")
    dirs = [
        "app/src/main/java/fengcalen",
        "app/src/main/java/fengcalen/ui",
        "app/src/main/java/fengcalen/ui/screen",
        "app/src/main/java/fengcalen/ui/screen/calendar",
        "app/src/main/java/fengcalen/ui/screen/moon",
        "app/src/main/java/fengcalen/ui/screen/feng",
        "app/src/main/java/fengcalen/ui/screen/cadao",
        "app/src/main/java/fengcalen/ui/screen/language",
        "app/src/main/java/fengcalen/ui/screen/occasions",
        "app/src/main/java/fengcalen/ui/screen/settings",
        "app/src/main/java/fengcalen/data",
        "app/src/main/java/fengcalen/data/entity",
        "app/src/main/java/fengcalen/data/dao",
        "app/src/main/java/fengcalen/data/repository",
        "app/src/main/java/fengcalen/data/model",
        "app/src/main/java/fengcalen/service",
        "app/src/main/java/fengcalen/receiver",
        "app/src/main/java/fengcalen/native",
        "app/src/main/java/fengcalen/util",
        "app/src/main/res/values",
        "app/src/main/res/xml",
        "app/src/main/res/mipmap-hdpi",
        "app/src/main/res/mipmap-mdpi",
        "app/src/main/res/mipmap-xhdpi",
        "app/src/main/res/mipmap-xxhdpi",
        "app/src/main/res/mipmap-xxxhdpi",
        "app/src/main/jniLibs/arm64-v8a",
        "app/src/main/jniLibs/x86_64",
    ]
    for d in dirs:
        (PROJECT_DIR / d).mkdir(parents=True, exist_ok=True)
    log("  Done.")

def copy_assets():
    """Copy assets from REA analysis to app/src/main/assets."""
    log("\n>>> Copying assets...")
    src = PROJECT_DIR / "base_apk" / "extracted" / "assets"
    dst = PROJECT_DIR / "app" / "src" / "main" / "assets"
    if src.exists():
        for item in src.iterdir():
            if item.is_dir():
                shutil.copytree(item, dst / item.name, dirs_exist_ok=True)
            else:
                shutil.copy2(item, dst / item.name)
        log("  Copied assets to " + str(dst))
    else:
        log("  WARNING: " + str(src) + " not found")

def write_master_report(results: dict):
    """Write master report file."""
    with open(REPORT_FILE, "w", encoding="utf-8") as f:
        f.write("# Master Report — Feng Calendar Build\n\n")
        f.write("Generated: " + datetime.now().isoformat() + "\n\n")
        f.write("## Execution Summary\n\n")
        f.write("| Part | File | Status | Notes |\n")
        f.write("|------|------|--------|-------|\n")
        for i in range(1, len(PARTS) + 1):
            f.write("| Part " + str(i) + " | " + PARTS[i-1] + " | " + results[i] + " | See tool output |\n")
    log("\nReport saved to: " + str(REPORT_FILE))

# ========================== EXPORT TO PROMPT DIRECTORY ==========================

def export_to_prompt_dir():
    """Copy all prompt files and reference materials to the target directory."""
    target = PROMPT_DIR
    target.mkdir(parents=True, exist_ok=True)
    log(">>> Exporting prompt files to " + str(target), "INFO")

    # Copy main prompt files
    # Copy individual part files
    for part in PARTS:
        src = PROJECT_DIR / part
        if src.exists():
            shutil.copy2(src, target / src.name)
            log("  Copied: " + part, "DEBUG")
    for f in ["master.md", "master.py", "prompt_feng_calendar.md"]:
        src = PROJECT_DIR / f
        if src.exists():
            shutil.copy2(src, target / src.name)
            log("  Copied: " + f, "DEBUG")

    # Create reference materials directory
    ref_dir = target / "reference"
    ref_dir.mkdir(exist_ok=True)

    # Reference materials from APK analysis
    refs = [
        ("alc", "alc"),        # dictionary, couples, greetings
        ("occasions", "occasions"),   # 48 occasion JSON files
        ("van-khan", "van-khan"),     # 14 vanKhan markdown docs
        ("regions", "regions"),       # 63 provinces
        ("occasion-art", "occasion-art"),  # art.json + images
    ]

    src_app = PROJECT_DIR / "base_apk" / "extracted" / "assets"
    if src_app.exists():
        for name, folder in refs:
            src = src_app / folder
            if src.exists():
                dst = ref_dir / name
                if dst.exists():
                    shutil.rmtree(dst)
                shutil.copytree(src, dst)
                count = len(list(dst.rglob("*")))
                log("  Copied reference: " + name + "/ (" + str(count) + " files)", "DEBUG")

    log(">>> Export complete: " + str(target), "SUCCESS")

def main():
    log("=" * 60, "INFO")
    log("Feng Calendar Build Master Executor", "INFO")
    log("=" * 60, "INFO")
    log("\nProject: " + str(PROJECT_DIR))

    # Setup structure
    create_structure()
    copy_assets()

    # Run parts sequentially
    results = {}
    for i, part_file in enumerate(PARTS, 1):
        part_path = PROJECT_DIR / part_file
        if not part_path.exists():
            log("\n>>> PART " + str(i) + " NOT FOUND: " + part_file, "ERROR")
            results[i] = "FAIL"
            break

        log("\n>>> Part " + str(i) + ": " + part_file)
        ok = execute_part(i, part_file)
        results[i] = "PASS" if ok else "FAIL"

        # Write to report
        with open(REPORT_FILE, "a", encoding="utf-8") as f:
            f.write("| Part " + str(i) + " | " + part_file + " | " + results[i] + " | See tool output |\n")

        if not ok:
            log("\n>>> PART " + str(i) + " FAILED. Stopping.", "CRITICAL")
            break

    # Summary
    log("\n" + "=" * 60, "INFO")
    log("BUILD SUMMARY", "INFO")
    log("=" * 60, "INFO")
    for i in range(1, len(PARTS) + 1):
        s = "✓" if results.get(i) == "PASS" else "✗"
        log("  Part " + str(i) + ": " + s + " " + PARTS[i-1])

    all_ok = all(v == "PASS" for v in results.values())
    log("\nOVERALL: " + ("ALL PASSED ✅" if all_ok else "SOME FAILED ❌"), "INFO")
    return 0 if all_ok else 1

if __name__ == "__main__":
    sys.exit(main())
