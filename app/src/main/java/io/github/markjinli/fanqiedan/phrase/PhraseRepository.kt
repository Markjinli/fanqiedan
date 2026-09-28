/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.phrase

import com.osfans.trime.data.base.DataManager
import java.io.File

object PhraseRepository {
    fun file(): File = File(DataManager.userDataDir, PhraseStore.FILE_NAME)

    fun load(): List<Phrase> {
        val f = file()
        if (!f.exists()) return emptyList()
        return PhraseStore.parse(f.readText())
    }

    fun save(phrases: List<Phrase>) {
        val f = file()
        f.parentFile?.mkdirs()
        f.writeText(PhraseStore.serialize(phrases))
    }
}
