package com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.diches.dichboxmobile.mv.boxesDataManager.filesManipulator.dialogs.InputDialog
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.FilesListViewModel
import com.diches.dichboxmobile.tools.fromBitmapToBase64
import com.diches.dichboxmobile.tools.fromUriToBitmap
import com.diches.dichboxmobile.tools.pickFromGallery

class EditorTools(
        private val fragment: Fragment,
        private val filesListVM: FilesListViewModel
) {
    private val resultCode = 100

    fun addImage(trigger: View): EditorTools {
        trigger.setOnClickListener { pickFromGallery(fragment, resultCode) }
        return this
    }

    fun addFile(trigger: View, title: String, clb: (name: String) -> Unit): EditorTools {
        trigger.setOnClickListener {
            InputDialog(filesListVM).buildDialog(fragment, title, clb)
        }
        return this
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun handleActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?,
            addClb: (type: String, name: String, src: String?) -> Unit
    ) {
        if (!(requestCode == this.resultCode && resultCode == Activity.RESULT_OK)) return

        data?.data?.let { uri ->
            val filename = DocumentFile
                    .fromSingleUri(fragment.requireContext(), uri)!!.name!!
                    .replace(" ", "")
            val entries = filesListVM.liveData.value!!.entries.dir!!.src.map { it.name }
            if (entries.indexOf(filename) != -1) return
            val bitmap = fromUriToBitmap(uri, fragment.requireActivity())
            val src = fromBitmapToBase64(bitmap)
            addClb("image", filename, src)
        }
    }
}