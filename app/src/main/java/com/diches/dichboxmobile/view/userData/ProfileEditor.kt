package com.diches.dichboxmobile.view.userData

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.inputPickers.ColorPicker
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.mv.userDataManager.UserDataViewModel
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.LogoEditor
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.user.SavedEditState
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.user.UserEditorVerifier
import java.io.ByteArrayOutputStream

class ProfileEditor: Fragment() {
    private lateinit var changeNameColorBtn: Button
    private lateinit var changeDescColorBtn: Button
    private lateinit var changeLogoBtn: Button
    private lateinit var imagePicker: ImageCropper
    private lateinit var logoEditor: LogoEditor
    private lateinit var editHandler: UserEditorVerifier
    private lateinit var nameColorBtn: Button
    private lateinit var descriptionColorBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_user_editor, container, false)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(UserDataViewModel::class.java)
        val userData = viewModel.liveData.value!!

        val savedState = if (savedInstanceState == null) null
            else SavedEditState(
                logo = savedInstanceState.getString("logo"),
                nameColor = savedInstanceState.getInt("nameColor"),
                descriptionColor = savedInstanceState.getInt("descriptionColor"),
                passwdAreaVisible = savedInstanceState.getBoolean("passwdArea")
        )

        setImageEditor(savedState, userData)
        val editColorFields = setUserVerifier(savedState, userData, viewModel)
        setColorPickers(editColorFields, savedState, userData)
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

        logoEditor = LogoEditor(userData.logo, logoArgs, logoBtns)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setUserVerifier(
            savedState: SavedEditState?,
            userData: UserContainer.UserData,
            viewModel: UserDataViewModel
    ): Pair<EditText, EditText> {
        val submitter = view?.findViewById<Button>(R.id.editProfileBtn)!!
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


        editHandler = UserEditorVerifier(submitter, userData)
                .addNameCheck(name, nameWarning, nameColorBtn)
                .addDescriptionCheck(description, descriptionColorBtn)
                .handleSavedState(savedState)
                .addEmailCheck(email, emailWarning)
                .addPasswdCheck(
                        passwdArea,
                        Pair(showPasswdBtn, passwdCancelBtn),
                        Pair(currentPasswd, currentPasswdWarning),
                        Pair(newPasswd, newPasswdWarning)
                )
                .addLogoEditor(logoEditor, imagePicker)
                .setSubmitClb { editData ->
                    val editedFields = editData.edited
                    if (editedFields.name != null)
                        context?.openFileOutput("signed.txt", Context.MODE_PRIVATE).use {
                            it?.write(editedFields.name?.toByteArray())
                        }

                    val editedData = userData.copy(
                            name = editedFields.name ?: userData.name,
                            description = editedFields.description ?: userData.description,
                            name_color = editedFields.name_color ?: userData.name_color,
                            description_color = editedFields.description_color ?: userData.description_color,
                            email = editedFields.email ?: userData.email,
                            logo = if (editData.logo == "removed") null else editData.logo
                    )
                    Toast.makeText(requireActivity().application, "Edited", Toast.LENGTH_LONG).show()
                    viewModel.setUserData(editedData)
                }

        return Pair(name, description)
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
            val baos = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            val imageEncoded = Base64.encodeToString(b, Base64.DEFAULT)
            logoEditor.setLogo(imageEncoded)
            editHandler.checkAllAdded()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val passwdAreaState = editHandler.getPasswdAreaState()
        outState.putBoolean("passwdArea", passwdAreaState)
        outState.putString("logo", logoEditor.getLogo().first)
        outState.putInt("nameColor", nameColorBtn.currentTextColor)
        outState.putInt("descriptionColor", descriptionColorBtn.currentTextColor)
    }
}