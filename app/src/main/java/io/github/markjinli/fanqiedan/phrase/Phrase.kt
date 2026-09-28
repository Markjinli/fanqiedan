/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.phrase

data class Phrase(
    val text: String,
    val code: String,
    val weight: Int = 100,
)
