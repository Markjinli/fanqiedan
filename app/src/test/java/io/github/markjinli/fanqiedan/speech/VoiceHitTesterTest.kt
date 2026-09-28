/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class VoiceHitTesterTest :
    StringSpec({
        val w = 1000f
        val h = 1000f

        "bottom band is online" {
            VoiceHitTester.hit(500f, 900f, w, h) shouldBe VoiceEngine.ONLINE
            VoiceHitTester.hit(0f, h - (h * 0.15f), w, h) shouldBe VoiceEngine.ONLINE
        }

        "upper-left band is offline" {
            VoiceHitTester.hit(200f, 800f, w, h) shouldBe VoiceEngine.OFFLINE
        }

        "upper-right band is openless" {
            VoiceHitTester.hit(800f, 800f, w, h) shouldBe VoiceEngine.OPENLESS
        }

        "above 30 percent from the bottom is cancel" {
            VoiceHitTester.hit(500f, 500f, w, h) shouldBe VoiceEngine.CANCEL
            VoiceHitTester.hit(500f, h - (h * 0.30f) - 1f, w, h) shouldBe VoiceEngine.CANCEL
        }

        "outside the window is cancel" {
            VoiceHitTester.hit(-1f, 900f, w, h) shouldBe VoiceEngine.CANCEL
            VoiceHitTester.hit(500f, 1001f, w, h) shouldBe VoiceEngine.CANCEL
        }
    })
