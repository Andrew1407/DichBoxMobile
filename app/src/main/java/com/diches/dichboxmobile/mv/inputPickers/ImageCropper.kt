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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream

class ImageCropper(private val fragment: Fragment) {
    private val resultCode = 100

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        fragment.startActivityForResult(intent, resultCode)
    }

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
        if (requestCode == this.resultCode && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                launchImageCrop(uri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            val src = convertUriBoBase64(result.uri)
            imageHandler(src)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun convertUriBoBase64(uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(fragment.requireActivity().contentResolver, uri)
        val bitmap = ImageDecoder.decodeBitmap(source)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun handlePickOnClick(btn: Button) {
        btn.setOnClickListener { pickFromGallery() }
    }
}