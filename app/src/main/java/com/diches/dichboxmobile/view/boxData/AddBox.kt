package com.diches.dichboxmobile.view.boxData

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.FragmentsRedirector
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.CurrentBoxViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.EditorsViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.ViewersViewModel
import com.diches.dichboxmobile.mv.inputPickers.ColorPicker
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.BoxFormGenerator
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.BoxPrivacyHandler
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList.AccessList
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors.BoxLogoEditor
import com.diches.dichboxmobile.tools.fromBitmapToBase64
import com.diches.dichboxmobile.view.boxesList.BoxesInfo

class AddBox: Fragment() {
    private lateinit var editorsHandler: AccessList
    private lateinit var boxPrivacyHandler: BoxPrivacyHandler
    private lateinit var imagePicker: ImageCropper
    private lateinit var boxLogoEditor: BoxLogoEditor
    private lateinit var formHandler: BoxFormGenerator

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box_add, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        val addBoxBtn = view.findViewById<Button>(R.id.createBtn)
        val editContainer = view.findViewById<LinearLayout>(R.id.editorsContainer)
        val boxPrivacyView = view.findViewById<LinearLayout>(R.id.boxPrivacy)
        val boxLogoView = view.findViewById<ImageView>(R.id.editBoxLogo)

        val namesViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        val editorsViewModel = ViewModelProvider(requireActivity()).get(EditorsViewModel::class.java)
        val viewersViewModel = ViewModelProvider(requireActivity()).get(ViewersViewModel::class.java)
        val currentBoxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)

        val username = namesViewModel.namesState.value!!.first!!

        val boxNameInput = view.findViewById<EditText>(R.id.boxNameInput)
        val boxNameWarning = view.findViewById<TextView>(R.id.boxNameWarning)
        val descriptionInput = view.findViewById<EditText>(R.id.editDescription)
        val descriptionColorBtn = view.findViewById<Button>(R.id.editDescriptionColor)
        val nameColorBtn = view.findViewById<Button>(R.id.setBoxNameColor)

        handleListView(editContainer)
        handleListView(boxPrivacyView)

        val setLogoBtn = view.findViewById<Button>(R.id.changeBoxLogoBtn)
        val rmLogoBtn = view.findViewById<Button>(R.id.removeBoxLogoBtn)
        val cancelLogoBtn = view.findViewById<Button>(R.id.cancelBoxLogoBtn)

        val logoButtons = listOf(setLogoBtn, rmLogoBtn, cancelLogoBtn)

        imagePicker = ImageCropper(this)
        imagePicker.handlePickOnClick(setLogoBtn)
        val visibleArgs = Pair(boxLogoView, savedInstanceState?.getString("logo"))
        boxLogoEditor = BoxLogoEditor(null, visibleArgs, logoButtons) {  }
                .handleChangeBtn(imagePicker)
                .handleCancelBtn()
                .handleDefaultBtn()

        editorsHandler = AccessList(editContainer, editorsViewModel, "editors")
                .setSavedInput(savedInstanceState)
                .addListAdapters(requireContext())
                .handleSearch(username)

        boxPrivacyHandler = BoxPrivacyHandler(boxPrivacyView, savedInstanceState, viewersViewModel,"public")
                .handleLimitedView(requireContext(), username)
                .handleRadioChoice()

        formHandler = BoxFormGenerator(username, savedInstanceState, addBoxBtn)
                .addLogoEditor(boxLogoEditor)
                .addDescriptionInput(descriptionInput)
                .addBoxPrivacyOptions(boxPrivacyHandler)
                .addEditorList(editorsHandler)
                .verifyNameInput(boxNameInput, boxNameWarning)
                .afterSubmit {
                    val (boxName) = it as BoxesContainer.NameContainer
                    currentBoxViewModel.setCurrentBox(boxName)
                    parentFragmentManager
                            .beginTransaction()
                            .replace(R.id.boxesContainer, BoxEntries(), "BOXES_ENTRIES_TAG")
                            .commit()
                }

        setColorPickerDialog(nameColorBtn, boxNameInput, "Box name color") {
            formHandler.setNameColor(it)
        }

        setColorPickerDialog(descriptionColorBtn, descriptionInput, "Description color") {
            formHandler.setDescriptionColor(it)
        }

        setUpRedirector(cancelBtn)
        savedInstanceState?.clear()
    }

    private fun setUpRedirector(cancelBtn: Button) {
        val redirector = requireActivity() as FragmentsRedirector
        cancelBtn.setOnClickListener {
            parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.boxesContainer, BoxesInfo(), "BOXES_LIST_TAG")
                    .commit()
            redirector.redirectToBoxInfo()
        }
    }

    private fun setColorPickerDialog(
            btn: Button,
            input: EditText,
            title: String,
            okClb: (color: Int) -> Unit
    ) {
        ColorPicker.getDialogBuilder()
                .setBtn(btn)
                .setTitle(title)
                .setDefaultColor(Color.parseColor("#00d9ff"))
                .setOkCLb { envelope, _ ->
                    val shapeDrawable = ShapeDrawable()
                    shapeDrawable.shape = RectShape()
                    shapeDrawable.paint.color = envelope.color
                    shapeDrawable.paint.strokeWidth = 20f
                    shapeDrawable.paint.style = Paint.Style.STROKE

                    btn.background = shapeDrawable
                    btn.setTextColor(envelope.color)
                    input.setTextColor(envelope.color)
                    okClb(envelope.color)
                }
                .createDialog(requireContext())
    }

    private fun handleListView(view: View) {
        val foundUsers = view.findViewById<ListView>(R.id.foundUsers)
        val addedUsers = view.findViewById<ListView>(R.id.addedUsers)
        foundUsers.isNestedScrollingEnabled = true
        addedUsers.isNestedScrollingEnabled = true
        addedUsers.emptyView = view.findViewById(R.id.addedUsersEmpty)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imagePicker.handleActivityResult(requestCode, resultCode, data)  {
            view?.findViewById<ImageView>(R.id.editUserLogo)?.setImageBitmap(it)
            val imageEncoded = fromBitmapToBase64(it)
            boxLogoEditor.setLogo(imageEncoded)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        formHandler.saveState(outState)
    }
}