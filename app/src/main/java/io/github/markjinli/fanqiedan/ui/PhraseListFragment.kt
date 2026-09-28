/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.osfans.trime.R
import com.osfans.trime.ui.common.PaddingPreferenceFragment
import com.osfans.trime.ui.main.NavigationRoute
import com.osfans.trime.util.addPreference
import com.osfans.trime.util.navigateWithAnim
import io.github.markjinli.fanqiedan.phrase.PhraseRepository

class PhraseListFragment : PaddingPreferenceFragment() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        rebuild()
    }

    override fun onResume() {
        super.onResume()
        rebuild()
    }

    private fun rebuild() {
        val phrases = runCatching { PhraseRepository.load() }.getOrDefault(emptyList())
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            addPreference(getString(R.string.fanqiedan_phrase_add)) {
                PhraseEditArgs.index = -1
                findNavController().navigateWithAnim(NavigationRoute.PhraseEdit)
            }
            phrases.forEachIndexed { index, phrase ->
                addPreference(phrase.code, phrase.text.replace("\n", " ")) {
                    PhraseEditArgs.index = index
                    findNavController().navigateWithAnim(NavigationRoute.PhraseEdit)
                }
            }
        }
    }
}
