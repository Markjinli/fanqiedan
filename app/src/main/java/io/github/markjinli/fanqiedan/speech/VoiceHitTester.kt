/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

object VoiceHitTester {
    /**
     * [x]/[y] are in the IME window, origin at the top-left.
     * Bottom 15% of [height] is ONLINE (full width).
     * 15%–30% from the bottom is OFFLINE (left) / OPENLESS (right).
     * Anything else is CANCEL.
     */
    fun hit(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
    ): VoiceEngine {
        if (width <= 0f || height <= 0f) return VoiceEngine.CANCEL
        val fromBottom = height - y
        val bandBottom = height * 0.15f
        val bandTop = height * 0.30f
        return when {
            fromBottom < 0f || fromBottom > bandTop || x < 0f || x > width -> VoiceEngine.CANCEL
            fromBottom <= bandBottom -> VoiceEngine.ONLINE
            x < width / 2f -> VoiceEngine.OFFLINE
            else -> VoiceEngine.OPENLESS
        }
    }
}
