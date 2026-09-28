/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

interface SpeechEngine {
    suspend fun transcribe(
        pcm: ByteArray,
        sampleRateHz: Int,
    ): SpeechResult
}
