package com.diches.dichboxmobile.tools

import android.content.Intent
import android.provider.MediaStore
import androidx.fragment.app.Fragment

fun pickFromGallery(fragment: Fragment, resultCode: Int) {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    fragment.startActivityForResult(intent, resultCode)
}
