/*
 * SPDX-FileCopyrightText: 2026 番茄蛋输入法 contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package io.github.markjinli.fanqiedan.speech

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.osfans.trime.R

class VoiceOverlayView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
    ) : View(context, attrs) {
        var highlighted: VoiceEngine = VoiceEngine.ONLINE
            set(value) {
                if (field != value) {
                    field = value
                    invalidate()
                }
            }

        var onFinished: ((VoiceEngine) -> Unit)? = null

        private val dimPaint = Paint().apply { color = Color.argb(160, 0, 0, 0) }
        private val onlinePaint = Paint().apply { color = Color.parseColor("#1565C0") }
        private val offlinePaint = Paint().apply { color = Color.parseColor("#2E7D32") }
        private val openlessPaint = Paint().apply { color = Color.parseColor("#616161") }
        private val selectedPaint = Paint().apply { color = Color.parseColor("#FFD54F") }
        private val textPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = 42f
            }
        private val rect = RectF()

        init {
            visibility = GONE
        }

        fun showOverlay() {
            highlighted = VoiceEngine.ONLINE
            visibility = VISIBLE
            invalidate()
        }

        fun hideOverlay() {
            visibility = GONE
        }

        fun pointer(
            x: Float,
            y: Float,
        ) {
            highlighted = VoiceHitTester.hit(x, y, width.toFloat(), height.toFloat())
        }

        fun finishWithCurrent() {
            hideOverlay()
            onFinished?.invoke(highlighted)
        }

        fun finish(
            x: Float,
            y: Float,
        ) {
            val engine = VoiceHitTester.hit(x, y, width.toFloat(), height.toFloat())
            hideOverlay()
            onFinished?.invoke(engine)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> pointer(event.x, event.y)
                MotionEvent.ACTION_UP -> finish(event.x, event.y)
                MotionEvent.ACTION_CANCEL -> {
                    hideOverlay()
                    onFinished?.invoke(VoiceEngine.CANCEL)
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            canvas.drawRect(0f, 0f, w, h, dimPaint)

            val midBandTop = h * 0.70f
            val midBandBottom = h * 0.85f

            // 15%–30% from bottom
            drawBand(canvas, 0f, midBandTop, w / 2f, midBandBottom, offlinePaint, VoiceEngine.OFFLINE, context.getString(R.string.fanqiedan_voice_offline))
            drawBand(canvas, w / 2f, midBandTop, w, midBandBottom, openlessPaint, VoiceEngine.OPENLESS, context.getString(R.string.fanqiedan_voice_openless))
            // bottom 15%
            drawBand(canvas, 0f, midBandBottom, w, h, onlinePaint, VoiceEngine.ONLINE, context.getString(R.string.fanqiedan_voice_online))
        }

        private fun drawBand(
            canvas: Canvas,
            left: Float,
            top: Float,
            right: Float,
            bottom: Float,
            fill: Paint,
            engine: VoiceEngine,
            label: String,
        ) {
            rect.set(left, top, right, bottom)
            canvas.drawRect(rect, fill)
            if (highlighted == engine) {
                selectedPaint.style = Paint.Style.STROKE
                selectedPaint.strokeWidth = 12f
                canvas.drawRect(rect, selectedPaint)
            }
            canvas.drawText(label, rect.centerX(), rect.centerY() + 14f, textPaint)
        }
    }
