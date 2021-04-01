package com.diches.dichboxmobile.mv.verifiers.editVerifiers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.tools.fromBase64ToBitmap

class LogoEditor(
        private val initialLogo: String?,
        logoParams: Pair<ImageView, String?>,
        buttons: List<Button>
) {
    private val changeBtn: Button = buttons[0]
    private val setDefaultBtn: Button = buttons[1]
    private val cancelBtn: Button = buttons[2]
    private val logoContainer: ImageView = logoParams.first
    private var logo: String? = logoParams.second
    private lateinit var checkAllClb: () -> Unit

    init {
        if (initialLogo == logo) {
            cancelBtn.visibility = View.GONE
            if (initialLogo === null) {
                setDefaultBtn.visibility = View.GONE
                logoContainer.setImageResource(R.drawable.default_user_logo)
            } else {
                logoContainer.setImageBitmap(fromBase64ToBitmap(initialLogo))
            }
        } else {
            if (logo === "removed" || initialLogo === null)
                setDefaultBtn.visibility = View.GONE
            if (logo === null || logo === "removed")
                logoContainer.setImageResource(R.drawable.default_user_logo)
            else
                logoContainer.setImageBitmap(fromBase64ToBitmap(logo!!))
        }
    }

    private fun setCancelVisibility() {
        cancelBtn.visibility = if (logo === initialLogo) View.GONE else View.VISIBLE
    }

    fun setCheckClb(clb: () -> Unit): LogoEditor {
        checkAllClb = clb
        return this
    }

    fun getLogo(): Pair<String?, Boolean> {
        val modified = logo != initialLogo
        return Pair(logo, modified)
    }

    private fun setLogoView() {
        if (logo == null || logo == "removed")
            logoContainer.setImageResource(R.drawable.default_user_logo)
        else
            logoContainer.setImageBitmap(fromBase64ToBitmap(logo!!))
    }

    fun setLogo(src: String) {
        logo = src
        setDefaultBtn.visibility = if (initialLogo === null) View.GONE else View.VISIBLE
        setCancelVisibility()
        setLogoView()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun handleChangeBtn(picker: ImageCropper): LogoEditor {
        picker.handlePickOnClick(changeBtn)
        return this
    }

    fun handleCancelBtn(): LogoEditor {
        cancelBtn.setOnClickListener {
            logo = initialLogo
            setDefaultBtn.visibility = if (initialLogo === null) View.GONE else View.VISIBLE
            setCancelVisibility()
            setLogoView()
            checkAllClb()
        }
        return this
    }

    fun handleDefaultBtn(): LogoEditor {
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