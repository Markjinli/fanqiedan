/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class SystemSpeechEngine(
    private val context: Context,
) : SpeechEngine {
    override suspend fun transcribe(
        pcm: ByteArray,
        sampleRateHz: Int,
    ): SpeechResult {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return SpeechResult.Error("设备没有系统语音识别器")
        }
        return suspendCancellableCoroutine { cont ->
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            recognizer.setRecognitionListener(
                object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) = Unit

                    override fun onBeginningOfSpeech() = Unit

                    override fun onRmsChanged(rmsdB: Float) = Unit

                    override fun onBufferReceived(buffer: ByteArray?) = Unit

                    override fun onEndOfSpeech() = Unit

                    override fun onError(error: Int) {
                        if (cont.isActive) {
                            cont.resume(SpeechResult.Error("在线识别失败"))
                        }
                        recognizer.destroy()
                    }

                    override fun onResults(results: Bundle?) {
                        val text =
                            results
                                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                ?.firstOrNull()
                                .orEmpty()
                        if (cont.isActive) {
                            cont.resume(
                                if (text.isBlank()) {
                                    SpeechResult.Error("没有识别到语音")
                                } else {
                                    SpeechResult.Text(text)
                                },
                            )
                        }
                        recognizer.destroy()
                    }

                    override fun onPartialResults(partialResults: Bundle?) = Unit

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?,
                    ) = Unit
                },
            )
            val intent =
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
            cont.invokeOnCancellation { recognizer.destroy() }
            recognizer.startListening(intent)
        }
    }
}
