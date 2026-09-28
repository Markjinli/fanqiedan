/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

private class FakeEngine(
    private val result: SpeechResult,
    var called: Boolean = false,
) : SpeechEngine {
    override suspend fun transcribe(
        pcm: ByteArray,
        sampleRateHz: Int,
    ): SpeechResult {
        called = true
        return result
    }
}

class SpeechRouterTest :
    StringSpec({
        "cancel does not call engines" {
            val online = FakeEngine(SpeechResult.Text("on"))
            val offline = FakeEngine(SpeechResult.Text("off"))
            val openless = FakeEngine(SpeechResult.Text("ol"))
            val router = SpeechRouter(online, offline, openless)
            runBlocking {
                router.transcribe(ByteArray(0), 16000, VoiceEngine.CANCEL) shouldBe SpeechResult.Cancelled
            }
            online.called shouldBe false
            offline.called shouldBe false
            openless.called shouldBe false
        }

        "openless is stubbed as coming soon" {
            val online = FakeEngine(SpeechResult.Text("on"))
            val offline = FakeEngine(SpeechResult.Text("off"))
            val openless = FakeEngine(SpeechResult.Text("should not run"))
            val router = SpeechRouter(online, offline, openless)
            runBlocking {
                router.transcribe(ByteArray(0), 16000, VoiceEngine.OPENLESS) shouldBe SpeechResult.ComingSoon
            }
            openless.called shouldBe false
        }

        "online and offline dispatch to the matching engine" {
            val online = FakeEngine(SpeechResult.Text("on"))
            val offline = FakeEngine(SpeechResult.Text("off"))
            val openless = FakeEngine(SpeechResult.ComingSoon)
            val router = SpeechRouter(online, offline, openless)
            runBlocking {
                router.transcribe(ByteArray(0), 16000, VoiceEngine.ONLINE) shouldBe SpeechResult.Text("on")
                router.transcribe(ByteArray(0), 16000, VoiceEngine.OFFLINE) shouldBe SpeechResult.Text("off")
            }
            online.called shouldBe true
            offline.called shouldBe true
        }
    })
