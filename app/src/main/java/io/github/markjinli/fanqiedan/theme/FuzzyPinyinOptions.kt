/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.theme

import com.osfans.trime.data.base.DataManager
import java.io.File

data class FuzzyOption(
    val id: String,
    val label: String,
    val example: String,
    val algebra: List<String>,
)

object FuzzyPinyinOptions {
    const val STATE_FILE = "fanqiedan_fuzzy.txt"
    const val SCHEMA_PATCH = "luna_pinyin_simp.custom.yaml"

    val options =
        listOf(
            FuzzyOption("zh_z", "z / zh", "资/知", listOf("derive/^z(?!h)/zh/", "derive/^zh/z/")),
            FuzzyOption("ch_c", "c / ch", "磁/吃", listOf("derive/^c(?!h)/ch/", "derive/^ch/c/")),
            FuzzyOption("sh_s", "s / sh", "丝/师", listOf("derive/^s(?!h)/sh/", "derive/^sh/s/")),
            FuzzyOption("n_l", "n / l", "泥/离", listOf("derive/^n/l/", "derive/^l/n/")),
            FuzzyOption("an_ang", "an / ang", "安/昂", listOf("derive/an$/ang/", "derive/ang$/an/")),
            FuzzyOption("en_eng", "en / eng", "恩/鞥", listOf("derive/en$/eng/", "derive/eng$/en/")),
            FuzzyOption("in_ing", "in / ing", "音/英", listOf("derive/in$/ing/", "derive/ing$/in/")),
        )

    fun stateFile(): File = File(DataManager.userDataDir, STATE_FILE)

    fun schemaPatchFile(): File = File(DataManager.userDataDir, SCHEMA_PATCH)

    fun loadEnabled(): Set<String> {
        val f = stateFile()
        if (!f.exists()) return emptySet()
        return f.readLines().map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    fun saveEnabled(enabled: Set<String>) {
        stateFile().writeText(enabled.sorted().joinToString("\n", postfix = "\n"))
        writeSchemaPatch(enabled)
    }

    fun writeSchemaPatch(enabled: Set<String> = loadEnabled()) {
        val algebra =
            options
                .filter { it.id in enabled }
                .flatMap { it.algebra }
                .joinToString("\n") { "        - $it" }
        val algebraBlock =
            if (algebra.isEmpty()) {
                ""
            } else {
                """
  speller/algebra/+:
$algebra
                """.trimEnd()
            }
        schemaPatchFile().writeText(
            """
# 番茄蛋：自定义短语 + 模糊音补丁。由 App 生成，请用设置页修改。
patch:
  engine/translators/+:
    - table_translator@custom_phrase
  custom_phrase:
    dictionary: ""
    user_dict: custom_phrase
    db_class: stabledb
    enable_completion: false
    enable_sentence: false
    initial_quality: 1
$algebraBlock
            """.trimIndent() + "\n",
        )
    }
}
