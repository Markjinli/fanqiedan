/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.phrase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PhraseStoreTest :
    StringSpec({
        "parse skips comments and blank lines" {
            val parsed =
                PhraseStore.parse(
                    """
                    # header
                    你好	addr	100

                    会议	A	80
                    """.trimIndent(),
                )
            parsed shouldBe
                listOf(
                    Phrase("你好", "addr", 100),
                    Phrase("会议", "A", 80),
                )
        }

        "serialize round-trips a simple list" {
            val phrases = listOf(Phrase("你好", "addr", 100), Phrase("会议", "A", 80))
            PhraseStore.parse(PhraseStore.serialize(phrases)) shouldBe phrases
        }

        "newlines in text are escaped as \\n" {
            val phrases = listOf(Phrase("第一行\n第二行", "addr", 100))
            val serialized = PhraseStore.serialize(phrases)
            serialized shouldBe "第一行\\n第二行\taddr\t100\n"
            PhraseStore.parse(serialized) shouldBe phrases
        }

        "validate rejects empty text" {
            PhraseStore.validate("addr", "", emptyList()) shouldBe
                PhraseValidation.Error("短语不能为空")
        }

        "validate rejects mixed-case and illegal codes" {
            PhraseStore.validate("Addr", "x", emptyList()) shouldBe
                PhraseValidation.Error("关键字须为 1–32 个小写字母，或单个大写字母")
            PhraseStore.validate("addr1", "x", emptyList()) shouldBe
                PhraseValidation.Error("关键字须为 1–32 个小写字母，或单个大写字母")
            PhraseStore.validate("AA", "x", emptyList()) shouldBe
                PhraseValidation.Error("关键字须为 1–32 个小写字母，或单个大写字母")
        }

        "validate accepts lowercase strings and a single uppercase letter" {
            PhraseStore.validate("addr", "家", emptyList()) shouldBe PhraseValidation.Ok
            PhraseStore.validate("A", "家", emptyList()) shouldBe PhraseValidation.Ok
        }

        "validate rejects duplicate codes except at ignoreIndex" {
            val existing = listOf(Phrase("旧", "addr", 100))
            PhraseStore.validate("addr", "新", existing) shouldBe
                PhraseValidation.Error("关键字不能重复")
            PhraseStore.validate("addr", "新", existing, ignoreIndex = 0) shouldBe PhraseValidation.Ok
        }

        "lookup is exact-code only" {
            val phrases = listOf(Phrase("家", "addr", 100), Phrase("啊", "A", 100))
            PhraseStore.lookup(phrases, "add") shouldBe emptyList()
            PhraseStore.lookup(phrases, "addr") shouldBe listOf(Phrase("家", "addr", 100))
            PhraseStore.lookup(phrases, "A") shouldBe listOf(Phrase("啊", "A", 100))
        }
    })
