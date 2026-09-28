/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.osfans.trime.R
import com.osfans.trime.ui.common.PaddingPreferenceFragment
import com.osfans.trime.util.addPreference
import java.io.File

class SpeechSettingsFragment : PaddingPreferenceFragment() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        val granted =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        val model = File(requireContext().filesDir, "sherpa-offline.onnx")
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext()).apply {
            addPreference(
                getString(R.string.fanqiedan_speech),
                if (granted) "麦克风权限已授予" else getString(R.string.fanqiedan_voice_need_mic),
            )
            addPreference(
                getString(R.string.fanqiedan_voice_offline),
                if (model.exists()) "离线模型已就绪" else getString(R.string.fanqiedan_voice_model_missing),
            ) {
                Toast.makeText(requireContext(), R.string.fanqiedan_voice_model_missing, Toast.LENGTH_SHORT).show()
            }
            addPreference(getString(R.string.fanqiedan_voice_openless), getString(R.string.fanqiedan_voice_coming_soon))
        }
    }
}
