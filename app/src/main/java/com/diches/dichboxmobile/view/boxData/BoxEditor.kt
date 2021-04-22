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
import com.diches.dichboxmobile.mv.boxesDataManager.RemoveBoxDialog
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.*
import com.diches.dichboxmobile.mv.inputPickers.ColorPicker
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.EditedViewModel
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.BoxFormEditor
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.BoxPrivacyHandler
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList.AccessList
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors.BoxLogoEditor
import com.diches.dichboxmobile.tools.fromBitmapToBase64

class BoxEditor : Fragment() {
    private lateinit var imagePicker: ImageCropper
    private lateinit var boxLogoEditor: BoxLogoEditor
    private lateinit var editorsHandler: AccessList
    private lateinit var boxPrivacyHandler: BoxPrivacyHandler
    private lateinit var boxFormEditor: BoxFormEditor
    private lateinit var editorsCopyVM: EditorsCopyViewModel
    private lateinit var viewersCopyVM: ViewersCopyViewModel
    private lateinit var editorsVM: EditorsViewModel
    private lateinit var viewersVM: ViewersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_box_editor, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boxDataViewModel = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)
        val boxViewModel = ViewModelProvider(requireActivity()).get(CurrentBoxViewModel::class.java)
        val filesListVM = ViewModelProvider(requireActivity()).get(FilesListViewModel::class.java)
        editorsCopyVM = ViewModelProvider(requireActivity()).get(EditorsCopyViewModel::class.java)
        viewersCopyVM = ViewModelProvider(requireActivity()).get(ViewersCopyViewModel::class.java)
        editorsVM = ViewModelProvider(requireActivity()).get(EditorsViewModel::class.java)
        viewersVM = ViewModelProvider(requireActivity()).get(ViewersViewModel::class.java)

        val redirector = requireActivity() as FragmentsRedirector

        val removeBoxBtn = view.findViewById<Button>(R.id.removeBoxBtn)
        val submitBtn = view.findViewById<Button>(R.id.editBoxBtn)
        val boxLogoView = view.findViewById<ImageView>(R.id.editBoxLogo)
        val setLogoBtn = view.findViewById<Button>(R.id.changeBoxLogoBtn)
        val rmLogoBtn = view.findViewById<Button>(R.id.removeBoxLogoBtn)
        val cancelLogoBtn = view.findViewById<Button>(R.id.cancelBoxLogoBtn)
        val editContainer = view.findViewById<LinearLayout>(R.id.editorsContainer)
        val boxPrivacyView = view.findViewById<LinearLayout>(R.id.boxPrivacy)
        val descriptionColorBtn = view.findViewById<Button>(R.id.editDescriptionColor)
        val nameColorBtn = view.findViewById<Button>(R.id.setBoxNameColor)
        val boxNameInput = view.findViewById<EditText>(R.id.boxNameInput)
        val descriptionInput = view.findViewById<EditText>(R.id.editDescription)
        val boxNameWarning = view.findViewById<TextView>(R.id.boxNameWarning)

        val logoButtons = listOf(setLogoBtn, rmLogoBtn, cancelLogoBtn)
        val boxData = boxDataViewModel.liveData.value!!

        val nameColor = savedInstanceState?.getString("nameColor") ?: boxData.name_color
        val descColor = savedInstanceState?.getString("descriptionColor") ?: boxData.description_color
        colorizeButton(nameColorBtn, Color.parseColor(nameColor))
        colorizeButton(descriptionColorBtn, Color.parseColor(descColor))

        val initialLogo = boxData.logo
        val savedLogo = savedInstanceState?.getString("logo") ?: initialLogo
        imagePicker = ImageCropper(this)
        imagePicker.handlePickOnClick(setLogoBtn)
        val visibleArgs = Pair(boxLogoView, savedLogo)
        boxLogoEditor = BoxLogoEditor(initialLogo, visibleArgs, logoButtons) { boxFormEditor.checkAll() }
                .handleChangeBtn(imagePicker)
                .handleCancelBtn()
                .handleDefaultBtn()

        handleListView(editContainer)
        handleListView(boxPrivacyView)

        val username = boxData.owner_name
        boxFormEditor = BoxFormEditor(username, savedInstanceState, submitBtn, boxDataViewModel)
                .setEditors(editorsCopyVM, editorsVM)
                .setViewers(viewersCopyVM, viewersVM)

        if (savedInstanceState == null) boxFormEditor.fetchAccessLists()

        editorsHandler = AccessList(editContainer, editorsVM, "editors")
                .setSavedInput(savedInstanceState)
                .addListAdapters(requireContext())
                .handleSearch(username)

        val privacy = boxData.access_level
        boxPrivacyHandler = BoxPrivacyHandler(boxPrivacyView, savedInstanceState, viewersVM, privacy)
                .handleLimitedView(requireContext(), username)
                .handleRadioChoice { boxFormEditor.checkAll() }

        boxFormEditor
                .addEditorList(editorsHandler)
                .addLogoEditor(boxLogoEditor)
                .addBoxPrivacyOptions(boxPrivacyHandler)
                .verifyNameInput(boxNameInput, boxNameWarning)
                .addDescriptionInput(descriptionInput)
                .afterSubmit {
                    clearListState()
                    val updated = it as BoxesContainer.EditedFields
                    val oldData = boxDataViewModel.liveData.value!!
                    val newData = oldData.copy(
                            last_edited = updated.last_edited!!,
                            name = updated.name ?: oldData.name,
                            name_color = updated.name_color ?: oldData.name_color,
                            description = updated.description ?: oldData.description,
                            description_color = updated.description_color ?: oldData.description_color,
                            logo = updated.logo ?: oldData.logo,
                            access_level = updated.access_level ?: oldData.access_level
                    )
                    boxDataViewModel.setBoxData(newData)

                    Toast.makeText(requireActivity().application, "Edited", Toast.LENGTH_LONG).show()
                    val editedViewModel = ViewModelProvider(requireActivity()).get(BoxEditedViewModel::class.java)
                    editedViewModel.setEdited(true)
                    parentFragmentManager
                            .beginTransaction()
                            .detach(this)
                            .attach(this)
                            .commit()
                }

        val statesList = listOf(boxViewModel, boxDataViewModel, filesListVM)
        val removeBoxDialog = RemoveBoxDialog(requireContext(), statesList, redirector)
        removeBoxDialog.handleOptionAction(removeBoxBtn)

        setColorPickerDialog(
                nameColorBtn,
                nameColorBtn.currentTextColor,
                boxNameInput,
                "Box name color"
        ) {
            boxFormEditor.setNameColor(it)
            boxFormEditor.checkAll()
        }

        setColorPickerDialog(
                descriptionColorBtn,
                descriptionColorBtn.currentTextColor,
                descriptionInput,
                "Description color"
        ) {
            boxFormEditor.setDescriptionColor(it)
            boxFormEditor.checkAll()
        }

        savedInstanceState?.clear()
    }

    private fun clearListState() {
        editorsCopyVM.setEditedAddedUsers(emptyList())
        viewersCopyVM.setEditedAddedUsers(emptyList())
        editorsVM.setFoundUsers(emptyList())
        editorsVM.setAddedUsers(emptyList())
        viewersVM.setFoundUsers(emptyList())
        viewersVM.setAddedUsers(emptyList())
    }

    private fun setColorPickerDialog(
            btn: Button,
            defaultColor: Int,
            input: EditText,
            title: String,
            okClb: (color: Int) -> Unit
    ) {
        ColorPicker.getDialogBuilder()
                .setBtn(btn)
                .setTitle(title)
                .setDefaultColor(defaultColor)
                .setOkCLb { envelope, _ ->
                    colorizeButton(btn, envelope.color)
                    input.setTextColor(envelope.color)
                    okClb(envelope.color)
                }
                .createDialog(requireContext())
    }

    private fun colorizeButton(btn: Button, color: Int) {
        val shapeDrawable = ShapeDrawable()
        shapeDrawable.shape = RectShape()
        shapeDrawable.paint.color = color
        shapeDrawable.paint.strokeWidth = 20f
        shapeDrawable.paint.style = Paint.Style.STROKE

        btn.background = shapeDrawable
        btn.setTextColor(color)
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
            boxFormEditor.checkAll()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        boxFormEditor.saveState(outState)
    }
}