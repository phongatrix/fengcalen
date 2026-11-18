# Reverse Engineering Report: base.apk

_ZIP container | 26,938,702 bytes (25.6 MiB) | SHA-256 64c0e6aebc55d1d7e9ad03b9f26af78595f1e5ee8ee7416b191f47ce1b951a7b_

## Executive Summary

Observed: base.apk is ZIP container (26,938,702 bytes (25.6 MiB)); native code analysis does not apply.

- Inferred: the file is not packed (packing score 2).
- Observed: 23 indicators extracted, including 6 URLs.

Evidence labels: Observed facts are read directly from the file; Derived facts are computed deterministically from observed data; Inferred statements are interpretations that an analyst should confirm.

## Identity and Fingerprints

| Property | Value |
| --- | --- |
| File name | base.apk |
| Size | 26,938,702 bytes (25.6 MiB) |
| Format | ZIP container |
| Entropy (bits/byte) | 7.74 |
| MD5 | d6167893950315207b1cd693ba5e83f6 |
| SHA-1 | ab24420eafb929f22a9c4fb105127b560ebf7920 |
| SHA-256 | 64c0e6aebc55d1d7e9ad03b9f26af78595f1e5ee8ee7416b191f47ce1b951a7b |

## Strings and Indicators

Observed: 50,034 printable strings (50000 ASCII, 34 UTF-16LE).

### Indicators

| Type | Value | File offset |
| --- | --- | --- |
| Windows path | \\(3I | 0x71b42 |
| Windows path | m,D:\B_ | 0xf423b |
| Windows path | \\ZA0" | 0x27c901 |
| Email address | p@Kc.Cv | 0x2dc10a |
| Windows path | Y[&\:\#e:\!f: | 0x346b3d |
| Windows path | [,\:[%]:\&e:\#f:\!g: | 0x346c48 |
| Windows path | [,\:[.]:[-^:[!_:[+`:\'e:\&f:\#g: | 0x346cc0 |
| Windows path | ['\:[.]:[-^:[,_:[)`:[!a:\%e:\&f:\#g: | 0x346d18 |
| Windows path | [%\:[.]:[,^:[%_:[%`:[%a:[+b:[*c:[$d:\#e: | 0x347032 |
| Windows path | \&f:\/g: | 0x34705e |
| Email address | T@h.TAe | 0x5249f0 |
| Email address | q@3.Tg | 0x55e3b2 |
| URL | https://developer.android.com/reference/com/google/android/play/core/review/model/ReviewErrorCode.html# | 0x78a4d3 |
| URL | https://developers.google.com/tink/faq/registration_errors | 0x78b004 |
| Email address | contacts@group.v.calendar.google.com | 0x78f967 |
| URL | https://goo.gle/compose-feedback | 0x791472 |
| Unix path | /proc/ | 0x798df3 |
| Email address | android@android.com | 0x799910 |
| Email address | 1@com.google.firebase.encoders.proto.Protobuf | 0x79d071 |
| Email address | F@kotlinx.serialization.json.JsonClassDiscriminator | 0x79d0a4 |
| URL | https://developer.apple.com/weatherkit/data-source-attribution/) | 0x7a0253 |
| URL | https://issuetracker.google.com/issues/241760537 | 0x7a72c2 |
| URL | https://youtrack.jetbrains.com/issue/KT-55980 | 0x7bb1a0 |

### String Sample

````text
0000001e  AndroidManifest.xml
00001b46  DebugProbesKt.bin
00001e7b  META-INF/NOTICE.md
000020ab  META-INF/androidx/annotation/annotation/LICENSE.txt
00002ee7  META-INF/androidx/collection/collection/LICENSE.txt
00003d23  META-INF/androidx/collection/collection-ktx/LICENSE.txt
00004b63  META-INF/androidx/datastore/datastore-core-okio/LICENSE.txt
000059a7  META-INF/androidx/datastore/datastore-preferences-external-protobuf/LICENSE.txt
00005cf8  META-INF/androidx/datastore/datastore-preferences-proto/LICENSE.txt
00006b44  META-INF/androidx/glance/glance-appwidget-external-protobuf/LICENSE.txt
00006e8d  META-INF/androidx/glance/glance-appwidget-proto/LICENSE.txt
00007cd1  META-INF/androidx/hilt/hilt-common/LICENSE.txt
0000a790  META-INF/androidx/room/room-common/LICENSE.txt
0000b5c7  META-INF/androidx.activity_activity-compose.version
0000b621  META-INF/androidx.activity_activity-ktx.version1.13.0
0000b675  META-INF/androidx.activity_activity.version1.13.0
0000b6c5  META-INF/androidx.annotation_annotation-experimental.version
0000b728  META-INF/androidx.appcompat_appcompat-resources.version
0000b784  META-INF/androidx.appcompat_appcompat.version
0000b863  META-INF/androidx.autofill_autofill.version
0000b8b4  META-INF/androidx.biometric_biometric.version
0000b908  META-INF/androidx.browser_browser.version
0000b959  META-INF/androidx.compose.animation_animation-core.version
0000b9b9  META-INF/androidx.compose.animation_animation.version
0000ba15  META-INF/androidx.compose.foundation_foundation-layout.version
0000ba79  META-INF/androidx.compose.foundation_foundation.version1.11.4
0000bad5  META-INF/androidx.compose.material3_material3.version
0000bb30  META-INF/androidx.compose.material_material-ripple.version
0000bb91  META-INF/androidx.compose.runtime_runtime-annotation.version
0000bbf5  META-INF/androidx.compose.runtime_runtime-retain.version
0000bc55  META-INF/androidx.compose.runtime_runtime-saveable.version
0000bcb5  META-INF/androidx.compose.runtime_runtime.version
0000bd0d  META-INF/androidx.compose.ui_ui-geometry.version
0000bd65  META-INF/androidx.compose.ui_ui-graphics.version
0000bdbd  META-INF/androidx.compose.ui_ui-text.version
0000be11  META-INF/androidx.compose.ui_ui-tooling-preview.version1.11.4
0000be6d  META-INF/androidx.compose.ui_ui-unit.version
0000bec1  META-INF/androidx.compose.ui_ui-util.version
0000bf15  META-INF/androidx.compose.ui_ui.version1.11.4
0000bf61  META-INF/androidx.core_core-ktx.version1.18.0
0000bfad  META-INF/androidx.core_core-remoteviews.version1.1.0
0000c054  META-INF/androidx.core_core-viewtree.version1.0.0
0000c0a4  META-INF/androidx.core_core.version
0000c0ed  META-INF/androidx.credentials_credentials-play-services-auth.version
0000c158  META-INF/androidx.credentials_credentials.version
0000c1b0  META-INF/androidx.cursoradapter_cursoradapter.version
0000c20c  META-INF/androidx.customview_customview-poolingcontainer.version1.0.0
0000c270  META-INF/androidx.customview_customview.version
0000c2c4  META-INF/androidx.datastore_datastore-core.version
0000c31c  META-INF/androidx.datastore_datastore-preferences-core.version
0000c380  META-INF/androidx.datastore_datastore-preferences.version
0000c3e0  META-INF/androidx.datastore_datastore.version
0000c434  META-INF/androidx.documentfile_documentfile.version
0000c48c  META-INF/androidx.drawerlayout_drawerlayout.version
0000c4e4  META-INF/androidx.dynamicanimation_dynamicanimation.version
0000c544  META-INF/androidx.emoji2_emoji2-views-helper.version1.4.0
0000c59c  META-INF/androidx.emoji2_emoji2.version
0000c5e8  META-INF/androidx.fragment_fragment.version
0000c638  META-INF/androidx.glance_glance-appwidget.version
0000c690  META-INF/androidx.glance_glance.version
0000c6dc  META-INF/androidx.graphics_graphics-path.version1.0.1
0000c7f0  META-INF/androidx.hilt_hilt-navigation-compose.version
0000c84c  META-INF/androidx.hilt_hilt-work.version1.3.0
0000c898  META-INF/androidx.interpolator_interpolator.version
0000c8f0  META-INF/androidx.legacy_legacy-support-core-utils.version
0000cdd1  META-INF/androidx.loader_loader.version1.1.0
0000ce1c  META-INF/androidx.localbroadcastmanager_localbroadcastmanager.version
0000ce88  META-INF/androidx.navigation_navigation-common.version
0000cee4  META-INF/androidx.navigation_navigation-compose.version
0000cf40  META-INF/androidx.navigation_navigation-runtime.version
0000cf9c  META-INF/androidx.navigationevent_navigationevent-compose.version
0000d004  META-INF/androidx.navigationevent_navigationevent.version
0000d064  META-INF/androidx.print_print.version
0000d0b0  META-INF/androidx.profileinstaller_profileinstaller.version
0000d110  META-INF/androidx.room_room-ktx.version
0000d15c  META-INF/androidx.room_room-runtime.version
0000d1ac  META-INF/androidx.savedstate_savedstate-compose.version
0000d208  META-INF/androidx.savedstate_savedstate-ktx.version
0000d260  META-INF/androidx.savedstate_savedstate.version
0000d2b4  META-INF/androidx.sqlite_sqlite-framework.version
0000d30c  META-INF/androidx.sqlite_sqlite.version
0000d358  META-INF/androidx.startup_startup-runtime.version
0000d3b0  META-INF/androidx.tracing_tracing-ktx.version
0000d404  META-INF/androidx.tracing_tracing.version
0000d454  META-INF/androidx.transition_transition.version
0000d4a8  META-INF/androidx.vectordrawable_vectordrawable-animated.version1.1.0
0000d50c  META-INF/androidx.vectordrawable_vectordrawable.version
0000d568  META-INF/androidx.versionedparcelable_versionedparcelable.version
0000d5d0  META-INF/androidx.viewpager_viewpager.version
0000d624  META-INF/androidx.window_window-core.version1.5.0
0000d674  META-INF/androidx.window_window.version
0000d6c0  META-INF/androidx.work_work-runtime-ktx.version
0000d715  META-INF/androidx.work_work-runtime.version2.12.0
0000d7ef  META-INF/com.google.dagger_dagger-lint-aar.version
0000d849  META-INF/com.google.dagger_dagger.version
0000d899  META-INF/com.google.dagger_hilt-android.version2.60.1
0000d8ed  META-INF/com.google.dagger_hilt-core.version
0000d941  META-INF/kotlinx_coroutines_android.version1.11.0PK
0000d990  META-INF/kotlinx_coroutines_core.version1.11.0PK
0000d9dc  META-INF/kotlinx_coroutines_play_services.version
0000da34  META-INF/services/r71
0000da6d  META-INF/services/su
0000db5f  assets/PublicSuffixDatabase.list
00018191  assets/alc/ca-dao.json
0001cb60  IIGFOWHG
0001f8b2  assets/alc/cau-doi.json
00021634  assets/alc/greetings.json
00026379  assets/alc/lexicon.json
00026783  assets/dexopt/baseline.prof
00029c53  assets/dexopt/baseline.profm
0002a2ec  assets/occasion-art/art.json
0002a9ae  assets/occasion-art/giang-sinh-1200.webp
0004faaa  assets/occasion-art/giang-sinh-600.webp
0005a1fe  assets/occasion-art/tat-nien-1200.webpRIFF
0007e508  assets/occasion-art/tat-nien-600.webp
0008943a  assets/occasion-art/tet-doan-vien-1200.webp
000a5c40  assets/occasion-art/tet-doan-vien-600.webp
000af4e0  assets/occasion-art/tet-du-xuan-1200.webp
000b0b21  eI/[IC-h
000b947c  ,do	Idp6a
000d334e  7 tYa-16
000d78d6  assets/occasion-art/tet-du-xuan-600.webp
000e3d10  assets/occasion-art/tet-duong-1200.webp
00103cbc  assets/occasion-art/tet-duong-600.webp
0010d356  assets/occasion-art/via-than-tai-1200.webpRIFF
0012083d  "Jm._4oA$0
0012a5b8  assets/occasion-art/via-than-tai-600.webp
001336be  assets/occasions/gia-dinh-viet-nam.json
0013385c  assets/occasions/giai-phong-mien-nam.json
00133a53  assets/occasions/giao-thua.jsoneR
00133c8a  assets/occasions/gio-to-hung-vuong.json}R
00133eb0  assets/occasions/le-tinh-nhan.json}P
00134052  assets/occasions/mung-1-thang-10.json
00134241  assets/occasions/mung-1-thang-11.json
00134437  assets/occasions/mung-1-thang-12.json
0013462d  assets/occasions/mung-1-thang-2.json
0013481e  assets/occasions/mung-1-thang-3.json
00134a0f  assets/occasions/mung-1-thang-4.json
00134c01  assets/occasions/mung-1-thang-5.json
00134df1  assets/occasions/mung-1-thang-6.json
00134fe4  assets/occasions/mung-1-thang-7.json
001351d8  assets/occasions/mung-1-thang-8.json
001353ca  assets/occasions/mung-1-thang-9.json
001355bc  assets/occasions/ngay-van-hoa-viet-nam.json
001357aa  assets/occasions/nha-giao-viet-nam.json
0013593e  assets/occasions/ong-cong-ong-tao.json
00135b66  assets/occasions/phu-nu-viet-nam.json
00135d04  assets/occasions/quan-doi-nhan-dan.json
00135ebf  assets/occasions/quoc-khanh.json
001360cf  assets/occasions/quoc-te-lao-dong.json
````

## Packing and Obfuscation

Inferred: the target is not packed (score 2; threshold 3).

- Whole-file entropy is 7.74 bits/byte, typical of packed data (weight 2)

## Methodology and Limitations

- Identified the format from file signatures and fingerprinted the file with MD5, SHA-1, and SHA-256.
- Parsed PE, ELF, and Mach-O headers, sections, imports, exports, symbols, and build metadata.
- Measured Shannon entropy for the file and every section.
- Extracted ASCII and UTF-16LE strings and classified network, path, registry, and command indicators.
- Classified imported APIs into capability categories and evaluated security mitigations from header fields.
- Disassembled x86/x86-64 with iced-x86 and every other supported architecture through an embedded SLEIGH engine, by recursive descent.
- Ranked functions by the capabilities and indicators they touch and listed the most relevant ones.

### Limitations

- Analysis is static: the target is never executed, so run-time behavior is inferred, not observed.
- Imports show what a program can call, not what it does; dynamically resolved APIs are invisible to import analysis.
- Disassembly covers code reachable from the entry point, exports, and symbols; indirect-only code may be missed.
- A few architectures are parsed but not yet disassembled; for those, code analysis is omitted.

## Detected Capabilities

Derived: capabilities matched from the embedded rule pack against imports, strings, and recovered instructions.

| Capability | Namespace | Scope | ATT&CK / MBC |
| --- | --- | --- | --- |
| base64 alphabet | data-manipulation/encoding/base64 | file | Data::Encode Data::Base64 [C0026.001] |
| MZ header | executable/pe | file | none |
| OpenSSL library | lib/openssl | file | none |
| zlib library | lib/zlib | file | none |

## Cryptographic Constants

Derived: cryptographic constants identified by table signature in the target.

| Address | Constant | Kind |
| --- | --- | --- |
| 0x539e5e | TEA/XTEA delta | block-cipher |
| 0x3a82cc | ChaCha sigma | stream-cipher |
| 0x79d766 | base64 alphabet | encoding |

## Go Binary

Go binary (): package , 0 dependencies.

## Coverage & Limitations

Coverage: base.apk parsed as ZIP container; 0 functions and 0 instructions recovered across 50034 strings. Limitations: 18743296 bytes of high-entropy (encrypted or compressed) regions were not decrypted; no executable header was parsed, so imports and mitigations are unavailable. An audit-grade evidence bundle (`re-<name>.evidence.json`) lists every finding with a stable content-addressed ID and can be checked offline with `rea verify`.

