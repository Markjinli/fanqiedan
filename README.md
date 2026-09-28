<!--
SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
SPDX-FileCopyrightText: 2015 - 2024 Rime community
SPDX-License-Identifier: GPL-3.0-or-later
-->

# 番茄蛋输入法

开源 Android 中文输入法。基于 [同文输入法 (Trime)](https://github.com/osfans/trime) 与 [Rime](https://rime.im/)，包名 `io.github.markjinli.fanqiedan`。许可 **GPL-3.0-or-later**。

作者：[Markjinli](https://github.com/Markjinli)

## 下载

[下载最新 APK（arm64）](https://github.com/Markjinli/fanqiedan/releases/latest/download/fanqiedan-arm64.apk)

这个链接固定指向最新 Release 里的 `fanqiedan-arm64.apk`。以后只要新版本仍用这个文件名上传，链接就会自动换成最新包，不用改 README。

当前包是 arm64 调试版，包名 `io.github.markjinli.fanqiedan.debug`。x86 模拟器装不上。

全部版本见 [Releases](https://github.com/Markjinli/fanqiedan/releases)。

## 安装

1. 用上面的链接下载 APK，允许安装未知来源应用后安装。
2. 系统设置 → 系统 → 语言和输入法 → 启用「番茄蛋输入法」，并设为当前输入法。
3. 首次打开 App 完成同文的存储/部署引导。

## 功能

### 个性化短语

设置 → **个性化短语**：关键字全词命中后上屏短语。

| 关键字 | 例子 |
|---|---|
| 1–32 个小写字母 | `addr` |
| 单个大写字母 | `A`（中文模式下 Shift+A，不切英文） |

编码未打完（如 `add`）不会出 `addr` 的短语。

### 语音

长按空格，屏幕最下面 30% 出现分区（手指仍按着，松手时看落点）：

- 最下面 15%：在线系统识别
- 下面 15%–30% 左侧：离线（sherpa 模型需自行放到应用 files 目录 `sherpa-offline.onnx`）
- 下面 15%–30% 右侧：Openless（第一版占位「即将支持」）
- 滑出这 30%：取消

### 模糊音 / 外观 / emoji

- 设置 → 模糊音：默认全关，按项打开。
- 外观：沿用同文主题选择。
- emoji：雾凇/同文符号键盘与词表。

## 编译

需要 Android SDK 36、NDK（与同文相同）、JDK 17。同文 native 子模块：

```bash
git submodule update --init --recursive
./gradlew :app:assembleDebug
```

Windows 请在 Git Bash 或已配置的 Android Studio 中编译。

单测（不依赖 NDK）：

```bash
./gradlew :app:testDebugUnitTest --tests io.github.markjinli.fanqiedan.*
```

## 致谢

- [同文输入法 Trime](https://github.com/osfans/trime)
- [Rime 中州韵](https://rime.im/)
- [雾凇拼音 rime-ice](https://github.com/iDvel/rime-ice)
- [Openless](https://github.com/Open-Less/openless)（语音后续接入，第一版不链接其库）

同文原 README 见仓库历史与上游项目。
