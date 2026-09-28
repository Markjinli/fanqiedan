/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

sealed class SpeechResult {
    data class Text(
        val value: String,
    ) : SpeechResult()

    data class Error(
        val message: String,
    ) : SpeechResult()

    data object Cancelled : SpeechResult()

    data object ComingSoon : SpeechResult()

    data object ModelMissing : SpeechResult()
}
