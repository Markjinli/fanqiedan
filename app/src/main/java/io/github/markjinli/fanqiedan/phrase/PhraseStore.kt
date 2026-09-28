/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.phrase

sealed class PhraseValidation {
    data object Ok : PhraseValidation()

    data class Error(
        val message: String,
    ) : PhraseValidation()
}

object PhraseStore {
    const val FILE_NAME = "custom_phrase.txt"

    private val CODE_LOWER = Regex("^[a-z]{1,32}$")
    private val CODE_UPPER = Regex("^[A-Z]$")

    fun parse(content: String): List<Phrase> =
        content.lineSequence().mapNotNull { raw ->
            val line = raw.trimEnd('\r')
            if (line.isBlank() || line.startsWith("#")) return@mapNotNull null
            val parts = line.split('\t')
            if (parts.size < 2) return@mapNotNull null
            val text = unescapeText(parts[0])
            val code = parts[1]
            val weight = parts.getOrNull(2)?.toIntOrNull() ?: 100
            Phrase(text = text, code = code, weight = weight)
        }.toList()

    fun serialize(phrases: List<Phrase>): String {
        if (phrases.isEmpty()) return ""
        return phrases.joinToString("\n", postfix = "\n") { phrase ->
            "${escapeText(phrase.text)}\t${phrase.code}\t${phrase.weight}"
        }
    }

    fun validate(
        code: String,
        text: String,
        existing: List<Phrase>,
        ignoreIndex: Int? = null,
    ): PhraseValidation {
        if (text.isEmpty()) return PhraseValidation.Error("短语不能为空")
        if (!isValidCode(code)) {
            return PhraseValidation.Error("关键字须为 1–32 个小写字母，或单个大写字母")
        }
        val duplicate =
            existing.withIndex().any { (index, phrase) ->
                phrase.code == code && index != ignoreIndex
            }
        if (duplicate) return PhraseValidation.Error("关键字不能重复")
        return PhraseValidation.Ok
    }

    fun lookup(
        phrases: List<Phrase>,
        code: String,
    ): List<Phrase> = phrases.filter { it.code == code }

    fun isValidCode(code: String): Boolean = CODE_LOWER.matches(code) || CODE_UPPER.matches(code)

    private fun escapeText(text: String): String = text.replace("\\", "\\\\").replace("\n", "\\n")

    private fun unescapeText(text: String): String {
        val out = StringBuilder(text.length)
        var i = 0
        while (i < text.length) {
            val c = text[i]
            if (c == '\\' && i + 1 < text.length) {
                when (text[i + 1]) {
                    'n' -> {
                        out.append('\n')
                        i += 2
                        continue
                    }
                    '\\' -> {
                        out.append('\\')
                        i += 2
                        continue
                    }
                }
            }
            out.append(c)
            i++
        }
        return out.toString()
    }
}
