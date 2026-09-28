/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

class SpeechRouter(
    private val online: SpeechEngine,
    private val offline: SpeechEngine,
    private val openless: SpeechEngine,
) {
    suspend fun transcribe(
        pcm: ByteArray,
        sampleRateHz: Int,
        engine: VoiceEngine,
    ): SpeechResult =
        when (engine) {
            VoiceEngine.CANCEL -> SpeechResult.Cancelled
            VoiceEngine.OPENLESS -> SpeechResult.ComingSoon
            VoiceEngine.ONLINE -> online.transcribe(pcm, sampleRateHz)
            VoiceEngine.OFFLINE -> offline.transcribe(pcm, sampleRateHz)
        }
}
