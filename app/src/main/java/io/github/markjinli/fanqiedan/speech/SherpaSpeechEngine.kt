/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

import java.io.File

class SherpaSpeechEngine(
    private val modelFile: File,
) : SpeechEngine {
    override suspend fun transcribe(
        pcm: ByteArray,
        sampleRateHz: Int,
    ): SpeechResult {
        if (!modelFile.exists()) return SpeechResult.ModelMissing
        return SpeechResult.Error("离线识别引擎尚未加载模型运行时")
    }
}
