/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.osfans.trime.R
import com.osfans.trime.daemon.RimeDaemon
import com.osfans.trime.daemon.launchOnReady
import com.osfans.trime.util.toast
import io.github.markjinli.fanqiedan.phrase.Phrase
import io.github.markjinli.fanqiedan.phrase.PhraseRepository
import io.github.markjinli.fanqiedan.phrase.PhraseStore
import io.github.markjinli.fanqiedan.phrase.PhraseValidation
import kotlinx.coroutines.launch
import splitties.dimensions.dp

class PhraseEditFragment : Fragment() {
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): android.view.View {
        val ctx = requireContext()
        val phrases = runCatching { PhraseRepository.load() }.getOrDefault(emptyList()).toMutableList()
        val index = PhraseEditArgs.index
        val existing = phrases.getOrNull(index)

        val codeInput =
            EditText(ctx).apply {
                hint = getString(R.string.fanqiedan_phrase_code)
                setText(existing?.code.orEmpty())
            }
        val textInput =
            EditText(ctx).apply {
                hint = getString(R.string.fanqiedan_phrase_text)
                minLines = 4
                gravity = Gravity.TOP
                setText(existing?.text.orEmpty())
            }

        fun persist(updated: List<Phrase>) {
            runCatching { PhraseRepository.save(updated) }
                .onFailure {
                    Toast.makeText(ctx, it.message, Toast.LENGTH_SHORT).show()
                    return
                }
            RimeDaemon.getFirstSessionOrNull()?.launchOnReady { api ->
                lifecycleScope.launch {
                    runCatching { api.deploy() }
                        .onFailure { ctx.toast(R.string.fanqiedan_phrase_deploy_failed) }
                }
            }
            findNavController().popBackStack()
        }

        val save =
            Button(ctx).apply {
                setText(R.string.fanqiedan_phrase_save)
                setOnClickListener {
                    val code = codeInput.text.toString().trim()
                    val text = textInput.text.toString()
                    when (val result = PhraseStore.validate(code, text, phrases, ignoreIndex = index.takeIf { it >= 0 })) {
                        is PhraseValidation.Error -> Toast.makeText(ctx, result.message, Toast.LENGTH_SHORT).show()
                        PhraseValidation.Ok -> {
                            val phrase = Phrase(text = text, code = code)
                            if (index in phrases.indices) {
                                phrases[index] = phrase
                            } else {
                                phrases.add(phrase)
                            }
                            persist(phrases)
                        }
                    }
                }
            }
        val delete =
            Button(ctx).apply {
                setText(R.string.fanqiedan_phrase_delete)
                isEnabled = index in phrases.indices
                setOnClickListener {
                    if (index in phrases.indices) {
                        phrases.removeAt(index)
                        persist(phrases)
                    }
                }
            }

        return LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            addView(codeInput, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            addView(textInput, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f).apply { topMargin = dp(12) })
            addView(save, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })
            addView(delete, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })
        }
    }
}
