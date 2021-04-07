package com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors

import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.tools.fromBase64ToBitmap

class UserLogoEditor (
        private val initialLogo: String?,
        logoParams: Pair<ImageView, String?>,
        buttons: List<Button>
): LogoEditor {
    private val changeBtn: Button = buttons[0]
    private val setDefaultBtn: Button = buttons[1]
    private val cancelBtn: Button = buttons[2]
    private val logoContainer: ImageView = logoParams.first
    private var logo: String? = logoParams.second
    private lateinit var checkAllClb: () -> Unit

    init {
        if (initialLogo == logo) {
            cancelBtn.visibility = View.GONE
            if (initialLogo == null) {
                setDefaultBtn.visibility = View.GONE
                logoContainer.setImageResource(R.drawable.default_user_logo)
            } else {
                logoContainer.setImageBitmap(fromBase64ToBitmap(initialLogo))
            }
        } else {
            if (logo == "removed" || initialLogo == null)
                setDefaultBtn.visibility = View.GONE
            if (logo == null || logo == "removed")
                logoContainer.setImageResource(R.drawable.default_user_logo)
            else
                logoContainer.setImageBitmap(fromBase64ToBitmap(logo!!))
        }
    }

    private fun setCancelVisibility() {
        cancelBtn.visibility = if (logo == initialLogo) View.GONE else View.VISIBLE
    }

    fun setCheckClb(clb: () -> Unit): UserLogoEditor {
        checkAllClb = clb
        return this
    }

    override fun getLogo(): Pair<String?, Boolean> {
        val modified = logo != initialLogo
        return Pair(logo, modified)
    }

    private fun setLogoView() {
        if (logo == null || logo == "removed")
            logoContainer.setImageResource(R.drawable.default_user_logo)
        else
            logoContainer.setImageBitmap(fromBase64ToBitmap(logo!!))
    }

    override fun setLogo(src: String?) {
        logo = src
        setDefaultBtn.visibility = if (initialLogo == null) View.GONE else View.VISIBLE
        setCancelVisibility()
        setLogoView()
    }

    override fun handleChangeBtn(picker: ImageCropper): UserLogoEditor {
        picker.handlePickOnClick(changeBtn)
        return this
    }

    override fun handleCancelBtn(): UserLogoEditor {
        cancelBtn.setOnClickListener {
            logo = initialLogo
            setDefaultBtn.visibility = if (initialLogo == null) View.GONE else View.VISIBLE
            setCancelVisibility()
            setLogoView()
            checkAllClb()
        }
        return this
    }

    override fun handleDefaultBtn(): UserLogoEditor {
        setDefaultBtn.setOnClickListener {
            logo = "removed"
            setDefaultBtn.visibility = View.GONE
            setCancelVisibility()
            setLogoView()
            checkAllClb()
        }
        return this
    }
}