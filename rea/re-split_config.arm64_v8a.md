# Reverse Engineering Report: split_config.arm64_v8a.apk

_ZIP container | 53,576 bytes (52.3 KiB) | SHA-256 d35e429e1ab1a4e295bbbf8627dae38e80e99ec791d9857913f8864b8d4ce10b_

## Executive Summary

Observed: split_config.arm64_v8a.apk is ZIP container (53,576 bytes (52.3 KiB)); native code analysis does not apply.

- Observed: 1 indicator extracted, including 1 URL.

Evidence labels: Observed facts are read directly from the file; Derived facts are computed deterministically from observed data; Inferred statements are interpretations that an analyst should confirm.

## Identity and Fingerprints

| Property | Value |
| --- | --- |
| File name | split_config.arm64_v8a.apk |
| Size | 53,576 bytes (52.3 KiB) |
| Format | ZIP container |
| Entropy (bits/byte) | 3.49 |
| MD5 | 111920ba5dc4f447e79a5b0fa0f75172 |
| SHA-1 | 8d280c599c7b029b88e2c050ce6760fbcba02b10 |
| SHA-256 | d35e429e1ab1a4e295bbbf8627dae38e80e99ec791d9857913f8864b8d4ce10b |

## Strings and Indicators

Observed: 172 printable strings (172 ASCII, 0 UTF-16LE).

### Indicators

| Type | Value | File offset |
| --- | --- | --- |
| URL | https://android.googlesource.com/toolchain/llvm-project | 0x5fe3 |

### String Sample

````text
0000001e  AndroidManifest.xmlu
000044b1  __cxa_finalize
000044c0  __cxa_atexit
000044cd  __stack_chk_fail
000044de  JNI_OnLoad
00004510  libandroidx.graphics.path.so
0000452d  LIBANDROIDX.GRAPHICS.PATH
0000455c  libdl.so
00004ab1  androidx/graphics/path/ConicConverter
00004adc  android/graphics/Path
00004afb  mNativePath
00004b07  internalPathIteratorHasNext
00004b2e  ro.build.version.sdk
00004b43  androidx/graphics/path/PathIteratorPreApi34Impl
00004b73  internalPathIteratorRawSize
00004b9d  createInternalPathIterator
00004bb8  destroyInternalPathIterator
00004bd4  internalConicToQuadratics
00004bf0  internalPathIteratorNext
00004c09  internalPathIteratorPeek
00004c22  internalPathIteratorSize
00006083  .fini_array
00006095  .comment
0000609e  .note.android.ident
000060bb  .rela.plt
000060d2  .eh_frame_hdr
000060e0  .gnu.version_r
000060ef  .data.rel.ro
000060fc  .rela.dyn
00006106  .gnu.version
0000611b  .gnu.hash
00006125  .eh_frame
0000612f  .note.gnu.build-id
00006142  .gnu.version_d
00006151  .dynamic
0000860d  __cxa_finalize
0000861c  __cxa_atexit
00008629  __register_atfork
00008698  Java_androidx_datastore_core_NativeSharedCounter_nativeTruncateFile
00008703  Java_androidx_datastore_core_NativeSharedCounter_nativeCreateSharedCounter
00008776  __stack_chk_fail
00008787  Java_androidx_datastore_core_NativeSharedCounter_nativeGetCounterValue
00008804  Java_androidx_datastore_core_NativeSharedCounter_nativeIncrementAndGetCounterValue
00008899  ftruncate
000088b0  getauxval
000088ed  libdl.so
00008b28  exynos9810
00008b33  java/io/IOException
0000a18c  .note.android.ident
0000a1a0  .note.gnu.build-id
0000a1bb  .gnu.version
0000a1c8  .gnu.version_r
0000a1d7  .gnu.hash
0000a1e9  .rela.dyn
0000a1f3  .rela.plt
0000a205  .eh_frame_hdr
0000a213  .eh_frame
0000a228  .data.rel.ro
0000a235  .fini_array
0000a241  .init_array
0000a24d  .dynamic
0000a25f  .relro_padding
0000a273  .comment
0000a8a6  stamp-cert-sha2563
0000b0aa  Ho Chi Minh city1
0000b0c5  EZStudio681
0000b0da  ezstudio1
0000b0ed  EZ Studio 680
0000b149  Ho Chi Minh city1
0000b164  EZStudio681
0000b179  ezstudio1
0000b18c  EZ Studio 680
0000b6e5  Ho Chi Minh city1
0000b700  EZStudio681
0000b715  ezstudio1
0000b728  EZ Studio 680
0000b784  Ho Chi Minh city1
0000b79f  EZStudio681
0000b7b4  ezstudio1
0000b7c7  EZ Studio 680
0000bcdf  California1
0000bcf4  Mountain View1
0000bd0c  Google Inc.1
0000bd22  Android1
0000bd34  Android0 
0000bd77  California1
0000bd8c  Mountain View1
0000bda4  Google Inc.1
0000bdba  Android1
0000bdcc  Android0
````

## Packing and Obfuscation

Inferred: the target is not packed (score 0; threshold 3).

No packing signals fired.

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
| ELF executable | executable/elf | file | none |

## Coverage & Limitations

Coverage: split_config.arm64_v8a.apk parsed as ZIP container; 0 functions and 0 instructions recovered across 172 strings. Limitations: 4096 bytes of high-entropy (encrypted or compressed) regions were not decrypted; no executable header was parsed, so imports and mitigations are unavailable. An audit-grade evidence bundle (`re-<name>.evidence.json`) lists every finding with a stable content-addressed ID and can be checked offline with `rea verify`.

## Provenance

Recovered build provenance: toolchain Android (9352603, based on r450784d1) clang version 14.0.7 (http.

