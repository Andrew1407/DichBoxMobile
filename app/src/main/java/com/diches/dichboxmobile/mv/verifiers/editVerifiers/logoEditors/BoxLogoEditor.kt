package com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.tools.fromBase64ToBitmap

class BoxLogoEditor(
        private val initialLogo: String?,
        logoParams: Pair<ImageView, String?>,
        buttons: List<Button>,
        private val checkAllClb: () -> Unit
) : LogoEditor {
    private val changeBtn: Button = buttons[0]
    private val removeBtn: Button = buttons[1]
    private val cancelBtn: Button = buttons[2]
    private val logoContainer: ImageView = logoParams.first
    private var logo: String? = logoParams.second

    init {
        if (initialLogo == logo) {
            cancelBtn.visibility = View.GONE
            if (initialLogo == null) {
                removeBtn.visibility = View.GONE
                logoContainer.visibility = View.GONE
            } else {
                if (!logoContainer.isVisible) logoContainer.visibility = View.VISIBLE
                logoContainer.setImageBitmap(fromBase64ToBitmap(initialLogo))
            }
        } else {
            if (logo == "removed" || initialLogo == null) {
                if (logoContainer.isVisible) logoContainer.visibility = View.GONE
                removeBtn.visibility = View.GONE
            }

            if (logo == null || logo == "removed") {
                if (logoContainer.isVisible) logoContainer.visibility = View.GONE
            } else {
                logoContainer.setImageBitmap(fromBase64ToBitmap(logo!!))
                if (!logoContainer.isVisible) logoContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun setButtonsState() {
        val changed = logo != initialLogo
        cancelBtn.isVisible = changed
        changeBtn.text = if (changed) "change logo" else "*set logo"
    }

    private fun setLogoView() {
        val logoIsNotEmpty = logo == null || logo == "removed"
        logoContainer.visibility = if (logoIsNotEmpty) View.GONE else View.VISIBLE
        if (!logoIsNotEmpty)
            logoContainer.setImageBitmap(fromBase64ToBitmap(logo!!))
    }

    override fun getLogo(): Pair<String?, Boolean> {
        val modified = logo != initialLogo
        return Pair(logo, modified)
    }

    override fun setLogo(src: String?) {
        logo = src
        removeBtn.isVisible = initialLogo != null
        setButtonsState()
        setLogoView()
    }

    override fun handleChangeBtn(picker: ImageCropper): BoxLogoEditor {
        picker.handlePickOnClick(changeBtn)
        return this
    }

    override fun handleCancelBtn(): BoxLogoEditor {
        cancelBtn.setOnClickListener {
            logo = initialLogo
            removeBtn.isVisible = initialLogo != null
            setButtonsState()
            setLogoView()
            checkAllClb()
        }
        return this
    }

    override fun handleDefaultBtn(): BoxLogoEditor {
        removeBtn.setOnClickListener {
            logo = "removed"
            removeBtn.visibility = View.GONE
            setButtonsState()
            setLogoView()
            checkAllClb()
        }
        return this
    }
}