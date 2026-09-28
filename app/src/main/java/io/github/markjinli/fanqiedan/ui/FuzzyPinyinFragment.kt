/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import com.osfans.trime.R
import com.osfans.trime.daemon.RimeDaemon
import com.osfans.trime.daemon.launchOnReady
import com.osfans.trime.ui.common.PaddingPreferenceFragment
import com.osfans.trime.util.toast
import io.github.markjinli.fanqiedan.theme.FuzzyPinyinOptions
import kotlinx.coroutines.launch

class FuzzyPinyinFragment : PaddingPreferenceFragment() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        val enabled = FuzzyPinyinOptions.loadEnabled().toMutableSet()
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            FuzzyPinyinOptions.options.forEach { option ->
                addPreference(
                    CheckBoxPreference(requireContext()).apply {
                        key = option.id
                        title = option.label
                        summary = option.example
                        isChecked = option.id in enabled
                        isIconSpaceReserved = false
                        setOnPreferenceChangeListener { _, newValue ->
                            if (newValue as Boolean) enabled.add(option.id) else enabled.remove(option.id)
                            FuzzyPinyinOptions.saveEnabled(enabled)
                            RimeDaemon.getFirstSessionOrNull()?.launchOnReady { api ->
                                lifecycleScope.launch {
                                    runCatching { api.deploy() }
                                        .onFailure { requireContext().toast(R.string.fanqiedan_phrase_deploy_failed) }
                                }
                            }
                            true
                        }
                    },
                )
            }
        }
    }
}
