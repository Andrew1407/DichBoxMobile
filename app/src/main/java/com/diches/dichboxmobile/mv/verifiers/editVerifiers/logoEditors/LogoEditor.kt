package com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors

import com.diches.dichboxmobile.mv.inputPickers.ImageCropper

interface LogoEditor {
    fun getLogo(): Pair<String?, Boolean>
    fun setLogo(src: String?)
    fun handleChangeBtn(picker: ImageCropper): LogoEditor
    fun handleCancelBtn(): LogoEditor
    fun handleDefaultBtn(): LogoEditor
}
