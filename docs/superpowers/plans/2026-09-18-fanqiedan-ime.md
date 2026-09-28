# 番茄蛋输入法 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fork 同文 (Trime) into 番茄蛋输入法 with custom phrases, zoned voice input, theme presets, fuzzy pinyin, and emoji — shippable as a GitHub APK.

**Architecture:** Keep Trime’s Java/Kotlin package `com.osfans.trime` and JNI. Change only `applicationId` to `io.github.markjinli.fanqiedan`. Add new Kotlin under `io.github.markjinli.fanqiedan.{phrase,speech,theme}`. Phrases write `custom_phrase.txt` then `RimeApi.deploy()`. Voice is an IME-window overlay (bottom 30%). Uppercase letter codes are intercepted in the IME and committed from PhraseStore; lowercase codes go through Rime.

**Tech Stack:** Kotlin, Trime/Rime, Kotest (existing `StringSpec`/`BehaviorSpec`), Android `SpeechRecognizer`, sherpa-onnx later (v1: model-missing path).

**Spec:** `docs/superpowers/specs/2026-09-18-fanqiedan-ime-design.md`

## Global Constraints

- License GPL-3.0-or-later (same as Trime)
- `applicationId` = `io.github.markjinli.fanqiedan` (debug suffix `.debug` stays)
- Display name: 番茄蛋输入法 / 番茄蛋输入法 (Debug)
- minSdk 21 (do not raise)
- Do not rename `com.osfans.trime` packages
- Do not link Openless (AGPL); stub only
- Offline ASR model is not bundled in the APK
- Zone engines are fixed: bottom 15% online, 15–30% left offline, 15–30% right Openless
- Phrase file: UTF-8, one entry per line, `text<TAB>code<TAB>weight`; newlines in text stored as `\n`

## Files

- Keep entire Trime tree after merge into this repo
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/phrase/Phrase.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/phrase/PhraseStore.kt`
- Create: `app/src/test/java/io/github/markjinli/fanqiedan/phrase/PhraseStoreTest.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/VoiceEngine.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/VoiceHitTester.kt`
- Create: `app/src/test/java/io/github/markjinli/fanqiedan/speech/VoiceHitTesterTest.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/SpeechResult.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/SpeechEngine.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/SpeechRouter.kt`
- Create: `app/src/test/java/io/github/markjinli/fanqiedan/speech/SpeechRouterTest.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/SystemSpeechEngine.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/SherpaSpeechEngine.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/OpenlessSpeechEngine.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/speech/VoiceOverlayView.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/ui/PhraseListFragment.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/ui/PhraseEditFragment.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/ui/SpeechSettingsFragment.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/ui/FuzzyPinyinFragment.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/theme/FuzzyPinyinOptions.kt`
- Create: `app/src/main/assets/rime/custom_phrase.txt` (empty header)
- Create: `app/src/main/assets/rime/luna_pinyin_simp.custom.yaml` (custom_phrase translator + fuzzy placeholders)
- Modify: `app/build.gradle.kts` (`applicationId`)
- Modify: `app/src/main/res/values/strings.xml` and `values-zh-rCN`, `values-zh-rTW`
- Modify: `app/src/main/AndroidManifest.xml` (`RECORD_AUDIO`)
- Modify: `app/src/main/java/com/osfans/trime/ui/main/NavigationRoute.kt`
- Modify: `app/src/main/java/com/osfans/trime/ui/main/MainFragment.kt`
- Modify: `app/src/main/java/com/osfans/trime/ime/core/TrimeInputMethodService.kt` (overlay + uppercase intercept)
- Modify: `app/src/main/java/com/osfans/trime/data/base/DataManager.kt` (seed custom_phrase.txt)
- Modify: `README.md`

---

### Task 1: Merge Trime and rebrand

**Files:**
- Merge `C:\Users\J\trime-upstream` into this repo (unrelated histories)
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Modify: `app/src/main/res/values-zh-rTW/strings.xml`

**Interfaces:** none

- [ ] **Step 1: Merge Trime develop into fanqiedan**

```bash
git remote add trime "C:/Users/J/trime-upstream"
git fetch trime
git merge trime/develop --allow-unrelated-histories -m "Merge Trime develop as 番茄蛋 base."
```

If merge conflicts on README, keep both: Trime README under `docs/trime-upstream.md`, ours as `README.md`.

- [ ] **Step 2: Change applicationId**

In `app/build.gradle.kts` `defaultConfig`:
`applicationId = "io.github.markjinli.fanqiedan"`
Leave `namespace = "com.osfans.trime"`.

- [ ] **Step 3: Rename display strings**

`app_name_release` → `番茄蛋输入法`
`app_name_debug` → `番茄蛋输入法 (Debug)`
Same in zh-rCN and zh-rTW.

- [ ] **Step 4: Commit**

```bash
git add app/build.gradle.kts app/src/main/res/values/strings.xml app/src/main/res/values-zh-rCN/strings.xml app/src/main/res/values-zh-rTW/strings.xml
git commit -m "rebrand: 番茄蛋输入法 applicationId io.github.markjinli.fanqiedan"
```

---

### Task 2: PhraseStore (TDD)

**Files:**
- Test: `app/src/test/java/io/github/markjinli/fanqiedan/phrase/PhraseStoreTest.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/phrase/Phrase.kt`
- Create: `app/src/main/java/io/github/markjinli/fanqiedan/phrase/PhraseStore.kt`

**Interfaces:**

```kotlin
data class Phrase(val text: String, val code: String, val weight: Int = 100)

sealed class PhraseValidation {
    data object Ok : PhraseValidation()
    data class Error(val message: String) : PhraseValidation()
}

object PhraseStore {
    fun parse(content: String): List<Phrase>
    fun serialize(phrases: List<Phrase>): String
    fun validate(code: String, text: String, existing: List<Phrase>, ignoreIndex: Int? = null): PhraseValidation
    fun lookup(phrases: List<Phrase>, code: String): List<Phrase>
}
```

- [ ] **Step 1: Write failing Kotest**

Use `io.kotest.core.spec.style.StringSpec` like `SyncRelativePathTest`.

Cases:
- parse `#` comments and blank lines skipped
- parse `你好\taddr\t100`
- serialize round-trip
- text with newline becomes `\n` in file and back
- validate rejects empty text, empty code, `Addr`, `addr1`, `AA`, duplicate code
- validate accepts `addr` and `A`
- lookup exact code only (`add` does not match `addr`)

- [ ] **Step 2: Run test, expect compile/fail**

`./gradlew :app:testDebugUnitTest --tests io.github.markjinli.fanqiedan.phrase.PhraseStoreTest`

- [ ] **Step 3: Implement PhraseStore**

Code rules:
- `CODE_LOWER = Regex("^[a-z]{1,32}$")`
- `CODE_UPPER = Regex("^[A-Z]$")`
- serialize: `text.replace("\n", "\\n")` then `joinToString("\n") { "${it.text}\t${it.code}\t${it.weight}" }` + trailing newline
- parse: skip blank and lines starting with `#`; split on tab, 2 or 3 columns (weight default 100); unescape `\\n`

- [ ] **Step 4: Re-run tests, expect PASS**

- [ ] **Step 5: Commit** `feat: PhraseStore parse/validate/serialize`

---

### Task 3: Phrase settings + Rime file + deploy

**Files:**
- Create fragments listed above
- Modify NavigationRoute, MainFragment
- Modify DataManager.sync() to copy default `custom_phrase.txt` if missing
- Create `app/src/main/assets/rime/custom_phrase.txt` header comments
- Create schema patch enabling `custom_phrase` table translator on `luna_pinyin_simp`

**Interfaces:**
- Consumes: `PhraseStore`, `DataManager.userDataDir`, `RimeDaemon` / `RimeApi.deploy()`
- File path: `File(DataManager.userDataDir, "custom_phrase.txt")`
- On save: write file, then `rime.launchOnReady { deploy() }`. On deploy failure toast `短语已保存，部署失败，可重试` — do not roll back file.

- [ ] **Step 1: Add NavigationRoute.Phrases and PhrasesEdit(index: Int)**
- [ ] **Step 2: PhraseListFragment / PhraseEditFragment** (Preference or simple RecyclerView + edit fields)
- [ ] **Step 3: MainFragment: put 个性化短语 at top of screen**
- [ ] **Step 4: Seed custom_phrase.txt and luna_pinyin_simp.custom.yaml with custom_phrase translator** (standard Rime custom_phrase block)
- [ ] **Step 5: Commit** `feat: phrase settings write custom_phrase.txt and deploy`

---

### Task 4: Uppercase letter intercept

**Files:**
- Modify: `TrimeInputMethodService.kt` (or `CommonKeyboardActionListener.kt` if that is where Shift+letter is handled)

**Interfaces:**
- Consumes: `PhraseStore.parse` + `PhraseStore.lookup(code)`
- When Rime `statusCached.isAsciiMode == false` and the incoming key is Shift+A–Z: if lookup(`"A"`) nonempty, `commitText` the first phrase and consume the key. Otherwise fall through.

- [ ] **Step 1: Add intercept**
- [ ] **Step 2: Commit** `feat: Shift+letter commits uppercase phrase codes`

---

### Task 5: VoiceHitTester + SpeechRouter (TDD)

**Files:**
- Create VoiceEngine, VoiceHitTester, SpeechResult, SpeechEngine, SpeechRouter + tests

**Interfaces:**

```kotlin
enum class VoiceEngine { ONLINE, OFFLINE, OPENLESS, CANCEL }

object VoiceHitTester {
    fun hit(x: Float, y: Float, width: Float, height: Float): VoiceEngine {
        // y origin top of IME window. Bottom of window is y=height.
        // bottom 15% of height → ONLINE (full width)
        // 15%–30% from bottom, x < width/2 → OFFLINE
        // 15%–30% from bottom, x >= width/2 → OPENLESS
        // else CANCEL
    }
}

sealed class SpeechResult {
    data class Text(val value: String) : SpeechResult()
    data class Error(val message: String) : SpeechResult()
    data object Cancelled : SpeechResult()
    data object ComingSoon : SpeechResult()
    data object ModelMissing : SpeechResult()
}

interface SpeechEngine {
    suspend fun transcribe(pcm: ByteArray, sampleRateHz: Int): SpeechResult
}

class SpeechRouter(
    private val online: SpeechEngine,
    private val offline: SpeechEngine,
    private val openless: SpeechEngine,
) {
    suspend fun transcribe(pcm: ByteArray, sampleRateHz: Int, engine: VoiceEngine): SpeechResult
}
```

Router: `CANCEL` → `Cancelled`; `OPENLESS` → always `ComingSoon` without calling engines; others dispatch.

- [ ] **Step 1: Failing tests for hit geometry** (width=1000, height=1000: (500, 900)=ONLINE, (200, 800)=OFFLINE, (800, 800)=OPENLESS, (500, 500)=CANCEL; y=height*0.85 boundary online; y=height*0.70 boundary cancel)
- [ ] **Step 2: Failing tests for router with fake engines**
- [ ] **Step 3: Implement**
- [ ] **Step 4: Tests pass**
- [ ] **Step 5: Commit** `feat: voice hit testing and speech router`

---

### Task 6: Voice overlay + engines + RECORD_AUDIO

**Files:**
- VoiceOverlayView, SystemSpeechEngine, SherpaSpeechEngine, OpenlessSpeechEngine
- Manifest RECORD_AUDIO
- TrimeInputMethodService: long-press space (~400ms) shows overlay covering bottom 30% of IME window; record until up; hit-test; router; commit text or toast

**Interfaces:**
- Overlay labels: 在线 · 系统识别 / 离线 / Openless
- High-contrast colors, not theme-dependent
- Openless greyed
- SherpaSpeechEngine: if model file missing return `ModelMissing` (do not bundle model)
- SystemSpeechEngine uses `android.speech.SpeechRecognizer`
- First use: request `RECORD_AUDIO`; if denied toast with settings link
- Slide out of 30% → cancel, no toast

- [ ] **Step 1: Overlay view + IME wiring**
- [ ] **Step 2: Engines**
- [ ] **Step 3: SpeechSettingsFragment (model download placeholder + permission status)**
- [ ] **Step 4: Commit** `feat: long-press space zoned voice overlay`

---

### Task 7: Fuzzy pinyin, emoji, about

**Files:**
- FuzzyPinyinOptions + FuzzyPinyinFragment
- luna_pinyin_simp.custom.yaml algebra patches toggled by writing a small `fanqiedan_fuzzy.yaml` or patching custom.yaml
- MainFragment: 模糊音, 语音识别; keep 外观 as existing Theme route; 高级 already exists
- AboutFragment: add 番茄蛋 / GPL / 同文 / 雾凇 / Markjinli thanks (or extra strings on About)

Fuzzy defaults **all off**. Keys: zh/z, ch/c, sh/s, n/l, an/ang (and matching fog-song set if present). Each switch shows an example string. Save → write patch → `deploy()`. No “enable all”.

Emoji: keep Trime symbol keyboard; no new engine.

- [ ] **Step 1: Fuzzy options + UI**
- [ ] **Step 2: About strings**
- [ ] **Step 3: Commit** `feat: fuzzy pinyin toggles and 番茄蛋 about`

---

### Task 8: README + GitHub Actions debug assemble (best effort)

**Files:**
- `README.md` — enable IME steps, phrase usage (`addr` / `A`), voice zones, GPL acknowledgements
- `.github/workflows/android.yml` — `./gradlew :app:assembleDebug` (will need NDK/submodules; if native missing, document)

- [ ] **Step 1: README**
- [ ] **Step 2: Commit** `docs: 番茄蛋 README`

---

## Spec coverage

| Spec item | Task |
|---|---|
| Fork + applicationId + name | 1 |
| Phrase file / validate / settings / deploy no rollback | 2–3 |
| Uppercase A–Z | 4 |
| Voice zones 15% / 15–30% L/R | 5–6 |
| System + sherpa + Openless stub | 6 |
| RECORD_AUDIO, model not in APK | 6 |
| Theme presets (existing Theme settings + name) | 7 (reuse ThemeSettingsFragment) |
| Fuzzy default off | 7 |
| Emoji via existing | 7 |
| Errors explicit | 3, 6 |
| Unit tests PhraseStore + hit + router | 2, 5 |
| GitHub README | 8 |
