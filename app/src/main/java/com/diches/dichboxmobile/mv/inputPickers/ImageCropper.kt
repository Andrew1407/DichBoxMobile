package com.diches.dichboxmobile.mv.inputPickers

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.tools.fromUriToBitmap
import com.diches.dichboxmobile.tools.pickFromGallery
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream

class ImageCropper(private val fragment: Fragment) {
    private val resultCode = 100

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(fragment.requireContext(), fragment)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        imageHandler: (src: Bitmap) -> Unit
    ) {
        if (requestCode == this.resultCode && resultCode == Activity.RESULT_OK)
            data?.data?.let { launchImageCrop(it) }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val src = fromUriToBitmap(result.uri, fragment.requireActivity())
            val compressed = Bitmap.createScaledBitmap(src, 350, 350, true)
            imageHandler(compressed)
        }
    }

    fun handlePickOnClick(btn: Button) {
        btn.setOnClickListener { pickFromGallery(fragment, resultCode) }
    }
}