package com.diches.dichboxmobile.view.userData

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.R
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class ProfileEditor: Fragment() {
    private lateinit var changeNameColorBtn: Button
    private lateinit var changeDescColorBtn: Button
    private lateinit var changeLogoBtn: Button

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_editor, container, false)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeNameColorBtn = view.findViewById(R.id.editUsernameColor)
        changeDescColorBtn = view.findViewById(R.id.editDescriptionColor)
        changeLogoBtn = view.findViewById(R.id.changeLogoBtn)

        setColorPikerDialog(changeNameColorBtn, "Username color")
        setColorPikerDialog(changeDescColorBtn, "Description color")

        changeLogoBtn.setOnClickListener {
            pickFromGallery()
        }
    }

    private fun setColorPikerDialog(btn: Button, title: String) {
        btn.setOnClickListener {
            val builder = ColorPickerDialog.Builder(context, R.style.colorPickerTheme)

            val bubbleFlag = BubbleFlag(requireContext())
            bubbleFlag.flagMode = FlagMode.ALWAYS

            builder.colorPickerView.flagView = bubbleFlag
            builder.setTitle(title)
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .setPositiveButton("ok", ColorEnvelopeListener { envelope, _ ->
                    val color = envelope.color
                    val shapeDrawable = ShapeDrawable()

                    shapeDrawable.shape = RectShape()
                    shapeDrawable.paint.color = color
                    shapeDrawable.paint.strokeWidth = 20f
                    shapeDrawable.paint.style = Paint.Style.STROKE

                    btn.background = shapeDrawable
                    btn.setTextColor(color)
                })
                .setNegativeButton("cancel") { di, _ ->
                    di.dismiss()
                }
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, 100)
    }

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                launchImageCrop(uri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK) {
                val result = CropImage.getActivityResult(data)
                println(result.uri)
        }
    }
}