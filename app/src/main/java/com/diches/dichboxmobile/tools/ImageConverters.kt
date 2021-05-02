package com.diches.dichboxmobile.tools

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream

fun fromBase64ToBitmap(logo: String): Bitmap {
    val basePrefix = Regex("""^data:image\/.+;base64,""")
    val logoSrc = logo.replace(basePrefix, "")
    val imageBytes = Base64.decode(logoSrc, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

@RequiresApi(Build.VERSION_CODES.P)
fun fromUriToBitmap(uri: Uri, activity: Activity): Bitmap {
    val source = ImageDecoder.createSource(activity.contentResolver, uri)
    val bitmap = ImageDecoder.decodeBitmap(source)
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    return bitmap
}

fun fromBitmapToBase64(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}
