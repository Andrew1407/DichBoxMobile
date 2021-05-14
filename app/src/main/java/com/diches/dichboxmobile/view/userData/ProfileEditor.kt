package com.diches.dichboxmobile.view.userData

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.mv.inputPickers.ColorPicker
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.EditedViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserDataViewModel
import com.diches.dichboxmobile.mv.userDataManager.viewModelStates.UserStateViewModel
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors.UserLogoEditor
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.user.SavedEditState
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.user.UserEditorVerifier
import com.diches.dichboxmobile.tools.fromBitmapToBase64

class ProfileEditor: Fragment() {
    private lateinit var changeNameColorBtn: Button
    private lateinit var changeDescColorBtn: Button
    private lateinit var submitter: Button
    private lateinit var changeLogoBtn: Button
    private lateinit var imagePicker: ImageCropper
    private lateinit var userLogoEditor: UserLogoEditor
    private lateinit var editHandler: UserEditorVerifier
    private lateinit var nameColorBtn: Button
    private lateinit var descriptionColorBtn: Button
    private lateinit var userDataViewModel: UserDataViewModel
    private lateinit var userStateViewModel: UserStateViewModel
    private lateinit var openedFilesViewModel: OpenedFilesViewModel
    private lateinit var boxDataViewModel: BoxDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_editor, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDataViewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        userStateViewModel = ViewModelProvider(requireActivity()).get(UserStateViewModel::class.java)
        openedFilesViewModel = ViewModelProvider(requireActivity()).get(OpenedFilesViewModel::class.java)
        boxDataViewModel = ViewModelProvider(requireActivity()).get(BoxDataViewModel::class.java)

        submitter = view.findViewById(R.id.editProfileBtn)

        var userData = userDataViewModel.liveData.value!!

        val savedState = if (savedInstanceState == null) null
            else SavedEditState(
                logo = savedInstanceState.getString("logo"),
                nameColor = savedInstanceState.getInt("nameColor"),
                descriptionColor = savedInstanceState.getInt("descriptionColor"),
                passwdAreaVisible = savedInstanceState.getBoolean("passwdArea")
            )

        setImageEditor(savedState, userData)
        val editColorFields = setUserVerifier(savedState)
        setColorPickers(editColorFields, savedState, userData)

        userDataViewModel.liveData.observe(viewLifecycleOwner) {
            if (it == null || it == userData) return@observe
            parentFragmentManager
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit()
            userData = it
        }
        savedInstanceState?.clear()
    }

    private fun setImageEditor(savedState: SavedEditState?, userData: UserContainer.UserData) {
        imagePicker = ImageCropper(this)
        val imageView = view?.findViewById<ImageView>(R.id.editUserLogo)!!
        val logoArgs = Pair(imageView, if (savedState == null) userData.logo else savedState.logo)
        val logoBtns = listOf(
                R.id.changeUserLogoBtn,
                R.id.setDefaultUserLogoBtn,
                R.id.cancelUserLogoBtn
        ).map { view?.findViewById<Button>(it)!! }

        userLogoEditor = UserLogoEditor(userData.logo, logoArgs, logoBtns)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setUserVerifier(savedState: SavedEditState?): Pair<EditText, EditText> {
        val name = requireView().findViewById<EditText>(R.id.editUsername)!!
        val description = requireView().findViewById<EditText>(R.id.editDescription)!!

        editHandler = UserEditorVerifier(submitter, userDataViewModel.liveData.value!!)
        handleEditor(editHandler, savedState)

        return Pair(name, description)
    }

    private fun handleEditor(editor: UserEditorVerifier, savedState: SavedEditState?) {
        val name = requireView().findViewById<EditText>(R.id.editUsername)!!
        val nameWarning = view?.findViewById<TextView>(R.id.editUserNameWarning)!!
        nameColorBtn = requireView().findViewById(R.id.editUsernameColor)!!
        val description = requireView().findViewById<EditText>(R.id.editDescription)!!
        descriptionColorBtn = requireView().findViewById(R.id.editDescriptionColor)!!
        val email = view?.findViewById<EditText>(R.id.editEmail)!!
        val emailWarning = view?.findViewById<TextView>(R.id.editUserEmailWarning)!!

        val showPasswdBtn = view?.findViewById<Button>(R.id.changePasswdBtn)!!
        val passwdArea = view?.findViewById<LinearLayout>(R.id.editUserPasswdArea)!!
        val passwdCancelBtn = view?.findViewById<Button>(R.id.cancelChangePasswdBtn)!!
        val currentPasswd = view?.findViewById<EditText>(R.id.editUserPasswd)!!
        val currentPasswdWarning = view?.findViewById<TextView>(R.id.editUserPasswdWarning)!!
        val newPasswd = view?.findViewById<EditText>(R.id.editUserNewPasswd)!!
        val newPasswdWarning = view?.findViewById<TextView>(R.id.editUserNewPasswdWarning)!!

        val editedViewModel = ViewModelProvider(requireActivity()).get(EditedViewModel::class.java)
        editor.addNameCheck(name, nameWarning, nameColorBtn)
            .addDescriptionCheck(description, descriptionColorBtn)
            .handleSavedState(savedState)
            .addEmailCheck(email, emailWarning)
            .addPasswdCheck(
                passwdArea,
                Pair(showPasswdBtn, passwdCancelBtn),
                Pair(currentPasswd, currentPasswdWarning),
                Pair(newPasswd, newPasswdWarning)
            )
            .addLogoEditor(userLogoEditor, imagePicker)
            .setSubmitClb { editData ->
                val editedFields = editData.edited
                setEditedUserData(editedFields, editData.logo)
                val newName = editedFields.name
                if (newName != null) setNewUsername(newName)
                editedViewModel.setEdited(true)
                parentFragmentManager
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit()
            }
    }

    private fun setEditedUserData(editedFields: UserContainer.EditedFields, logo: String?) {
        val userData = userDataViewModel.liveData.value!!
        val editedData = userData.copy(
            name = editedFields.name ?: userData.name,
            description = editedFields.description ?: userData.description,
            name_color = editedFields.name_color ?: userData.name_color,
            description_color = editedFields.description_color ?: userData.description_color,
            email = editedFields.email ?: userData.email,
            logo = if (logo == "removed") null else logo
        )
        Toast.makeText(requireActivity().application, "Edited", Toast.LENGTH_LONG).show()
        userDataViewModel.setUserData(editedData)
    }

    private fun setNewUsername(newName: String) {
        val oldName = userDataViewModel.liveData.value!!.name
        userStateViewModel.setState(Pair(newName, newName))
        val boxData = boxDataViewModel.liveData.value
        if (boxData != null)
            boxDataViewModel.setBoxData(boxData.copy(owner_name = newName))
        val openedFiles = openedFilesViewModel.liveData.value
        if (openedFiles != null)
            openedFilesViewModel.renamePaths("/${oldName}", "/$newName")
    }

    private fun setColorPickers(
            pickableFields: Pair<EditText, EditText>,
            savedState: SavedEditState?,
            userData: UserContainer.UserData
    ) {
        val (name, description) = pickableFields
        changeNameColorBtn = view?.findViewById(R.id.editUsernameColor)!!
        changeDescColorBtn = view?.findViewById(R.id.editDescriptionColor)!!
        changeLogoBtn = view?.findViewById(R.id.changeUserLogoBtn)!!

        ColorPicker.getDialogBuilder()
                .setBtn(changeNameColorBtn)
                .setTitle("Username color")
                .setDefaultColor(savedState?.nameColor ?: Color.parseColor(userData.name_color))
                .setOkCLb { envelope, _ ->
                    editHandler.changeNameColor(name, envelope.color, changeNameColorBtn)
                }
                .createDialog(requireContext())

        ColorPicker.getDialogBuilder()
                .setBtn(changeDescColorBtn)
                .setTitle("Description color")
                .setDefaultColor(savedState?.descriptionColor ?: Color.parseColor(userData.description_color))
                .setOkCLb { envelope, _ ->
                    editHandler.changeDescriptionColor(description, envelope.color, changeDescColorBtn)
                }
                .createDialog(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        imagePicker.handleActivityResult(requestCode, resultCode, data)  {
            view?.findViewById<ImageView>(R.id.editUserLogo)?.setImageBitmap(it)
            val imageEncoded = fromBitmapToBase64(it)
            userLogoEditor.setLogo(imageEncoded)
            editHandler.checkAllAdded()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val passwdAreaState = editHandler.getPasswdAreaState()
        outState.putBoolean("passwdArea", passwdAreaState)
        outState.putString("logo", userLogoEditor.getLogo().first)
        outState.putInt("nameColor", nameColorBtn.currentTextColor)
        outState.putInt("descriptionColor", descriptionColorBtn.currentTextColor)
    }
}