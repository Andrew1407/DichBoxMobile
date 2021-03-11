package com.diches.dichboxmobile.mv.inputPickers

import android.content.Context
import android.graphics.Color
import android.widget.Button
import com.diches.dichboxmobile.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ColorPicker {
    private lateinit var openBtn: Button
    private var title: String = "Default title"
    private var defaultColor: Int = Color.WHITE
    private lateinit var okClb: (envelope: ColorEnvelope, fromUser: Boolean) -> Unit

    companion object {
        fun getDialogBuilder(): ColorPicker = ColorPicker()
    }

    fun setBtn(btn: Button): ColorPicker {
        openBtn = btn
        return this
    }

    fun setTitle(inputTitle: String): ColorPicker {
        title = inputTitle
        return this
    }

    fun setDefaultColor(inputColor: Int): ColorPicker {
        defaultColor = inputColor
        return this
    }

    fun setOkCLb(clb: (envelope: ColorEnvelope, fromUser: Boolean) -> Unit): ColorPicker {
        okClb = clb
        return this
    }

    fun createDialog(ctx: Context) {
        openBtn.setOnClickListener {
            val builder = ColorPickerDialog.Builder(ctx, R.style.colorPickerTheme)
            val bubbleFlag = BubbleFlag(ctx)
            bubbleFlag.flagMode = FlagMode.ALWAYS
            builder.colorPickerView.flagView = bubbleFlag
            builder.colorPickerView.setInitialColor(defaultColor)
            builder
                .setTitle(title)
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .setPositiveButton("ok", ColorEnvelopeListener(okClb))
                .setNegativeButton("cancel") { di, _ ->
                    di.dismiss()
                }
                .show()
        }
    }
}