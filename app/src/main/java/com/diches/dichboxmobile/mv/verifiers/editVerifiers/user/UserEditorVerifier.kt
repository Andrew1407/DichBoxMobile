package com.diches.dichboxmobile.mv.verifiers.editVerifiers.user

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.inputPickers.ImageCropper
import com.diches.dichboxmobile.mv.verifiers.FieldsTemplates
import com.diches.dichboxmobile.mv.verifiers.FieldsWarnings
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors.UserLogoEditor
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.MutableInputVerifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class UserEditorVerifier (
        private val submitter: Button,
        private val defaultData: UserContainer.UserData
) {
    private val inputVerifier = MutableInputVerifier<UserEditFields>()
    private val api = UserAPI()
    private lateinit var onSubmitClb: (name: UserContainer.EditData) -> Unit
    private lateinit var passwdVerifier: PasswordsVerifier
    private lateinit var userLogoEditor: UserLogoEditor
    private lateinit var nameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var nameColorBtn: Button
    private lateinit var descriptionColorBtn: Button
    private var passwdAreaVisible = false
    private val edited = UserContainer.EditedFields(
            name = defaultData.name,
            name_color = defaultData.name_color,
            description = defaultData.description,
            description_color = defaultData.description_color,
            email = defaultData.email!!
    )

    init {
        submitter.isEnabled = false
        addSubmitAction()

        inputVerifier
                .addVerifier(
                        key = UserEditFields.NAME,
                        templateWarning = FieldsWarnings.NAME_INVALID.text,
                        templateTest = { FieldsTemplates.NAME.test(it) },
                        fetchWarning = FieldsWarnings.EMAIL_TAKEN.text,
                        fetchHandler = {
                            val ownName = it == defaultData.name
                            if (ownName) true
                            else !verifyField(UserEditFields.NAME.getVal(), it)
                        }
                ).addOmitted(UserEditFields.NAME)
                .addVerifier(
                    key = UserEditFields.EMAIL,
                    templateWarning = FieldsWarnings.EMAIL_INVALID.text,
                    templateTest = { FieldsTemplates.EMAIL.test(it) },
                    fetchWarning = FieldsWarnings.EMAIL_TAKEN.text,
                    fetchHandler = {
                        val ownEmail =  it == defaultData.email
                        if (ownEmail) true
                        else !verifyField(UserEditFields.EMAIL.getVal(), it)
                    }
                ).addOmitted(UserEditFields.EMAIL)
                .addVerifier(
                        key = UserEditFields.DESCRIPTION,
                        templateWarning = "",
                        templateTest = { true }
                ).addOmitted(UserEditFields.DESCRIPTION)

    }

    fun setSubmitClb(clb: (name: UserContainer.EditData) -> Unit): UserEditorVerifier {
        onSubmitClb = clb
        return this
    }

    fun handleSavedState(state: SavedEditState?): UserEditorVerifier {
        if (state != null) {
            val nameColor = state.nameColor
            val descriptionColor = state.descriptionColor
            nameInput.setTextColor(nameColor)
            changeBtnColor(nameColorBtn, nameColor)
            descriptionInput.setTextColor(descriptionColor)
            changeBtnColor(descriptionColorBtn, descriptionColor)
            passwdAreaVisible = state.passwdAreaVisible
            edited.name_color = colorToHex(nameColor)
            edited.description_color = colorToHex(descriptionColor)
        }
        return this
    }

    fun addNameCheck(
            nameContainer: EditText,
            warning: TextView,
            colorBtn: Button
    ): UserEditorVerifier {
        nameInput = nameContainer
        nameColorBtn = colorBtn
        changeBtnColor(nameColorBtn, Color.parseColor(edited.name_color))
        nameInput.setText(edited.name)
        nameInput.setTextColor(Color.parseColor(edited.name_color))
        onTextChange(nameInput) {
            val inputText = nameInput.text.toString()
            val rotated = edited.name == inputText
            if (rotated) return@onTextChange
            inputVerifier.removeOmitted(UserEditFields.NAME)
            val warningText = withContext(Dispatchers.IO) {
                inputVerifier.verify(UserEditFields.NAME, inputText)
            }
            edited.name = inputText
            decorateFields(warningText, nameInput, warning)
            checkAllAdded()
        }
        return this
    }

    fun addDescriptionCheck(description: EditText, colorBtn: Button): UserEditorVerifier {
        descriptionInput = description
        descriptionColorBtn = colorBtn
        changeBtnColor(descriptionColorBtn, Color.parseColor(edited.description_color))
        descriptionInput.setText(edited.description)
        descriptionInput.setTextColor(Color.parseColor(edited.description_color))
        onTextChange(descriptionInput) {
            val inputText = descriptionInput.text.toString()
            val rotated = edited.description == inputText
            if (rotated) return@onTextChange
            inputVerifier.removeOmitted(UserEditFields.DESCRIPTION)
            inputVerifier.verify(UserEditFields.DESCRIPTION, inputText)
            edited.description = inputText
            checkAllAdded()
        }
        return this
    }

    fun addEmailCheck(email: EditText, warning: TextView): UserEditorVerifier {
        email.setText(edited.email)
        onTextChange(email) {
            val inputText = email.text.toString()
            val rotated = edited.email == inputText
            if (rotated) return@onTextChange
            inputVerifier.removeOmitted(UserEditFields.EMAIL)
            val warningText = withContext(Dispatchers.IO) {
                inputVerifier.verify(UserEditFields.EMAIL, inputText)
            }
            edited.email = inputText
            decorateFields(warningText, email, warning)
            checkAllAdded()
        }
        return this
    }

    fun changeNameColor(name: EditText, color: Int, btn: Button) {
        name.setTextColor(color)
        edited.name_color = colorToHex(color)
        changeBtnColor(btn, color)
        checkAllAdded()
    }

    fun changeDescriptionColor(description: EditText, color: Int, btn: Button) {
        description.setTextColor(color)
        edited.description_color = colorToHex(color)
        changeBtnColor(btn, color)
        checkAllAdded()
    }

    fun addPasswdCheck(
            passwdArea: LinearLayout,
            switchers: Pair<Button, Button>,
            passwd: Pair<EditText, TextView>,
            newPasswd: Pair<EditText, TextView>
    ): UserEditorVerifier {
        passwdVerifier = PasswordsVerifier(passwdArea, switchers, passwdAreaVisible)
                .setVerifyParams(api, defaultData.name)
                .addPasswdCheck(passwd.first, passwd.second)
                .addNewPasswdCheck(newPasswd.first, newPasswd.second)
                .handleSwitchAreaState()
                .setCheckClb { checkAllAdded() }

        return this
    }

    fun getPasswdAreaState(): Boolean = passwdVerifier.getAreaState()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun addLogoEditor(le: UserLogoEditor, picker: ImageCropper): UserEditorVerifier {
        userLogoEditor = le
                .setCheckClb { checkAllAdded() }
                .handleChangeBtn(picker)
                .handleDefaultBtn()
                .handleCancelBtn()

        checkAllAdded()
        return this
    }

    private fun changeBtnColor(btn: Button, color: Int) {
        val shapeDrawable = ShapeDrawable()

        shapeDrawable.shape = RectShape()
        shapeDrawable.paint.color = color
        shapeDrawable.paint.strokeWidth = 20f
        shapeDrawable.paint.style = Paint.Style.STROKE

        btn.background = shapeDrawable
        btn.setTextColor(color)
    }

    private fun decorateFields(
            warnMessage: String,
            input: EditText,
            warning: TextView
    ) {
        val hasWarning = warnMessage.isNotEmpty()
        val warnColor = if (hasWarning) AppColors.CRIMSON else AppColors.GREEN
        warning.text = warnMessage
        DrawableCompat.setTint(input.background, warnColor.raw)
    }


    private fun onTextChange(field: EditText, clb: suspend () -> Unit) {
        field.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                clb()
            }
        }
    }

    private suspend fun verifyField(
            field: String,
            value: String
    ): Boolean {
        val container = UserContainer.VerifyField(field, value)
        val (_, res) = api.verifyField(container)
        return (res as UserContainer.VerifyFieldRes).foundValue != null
    }

    fun checkAllAdded() {
        val textChanged = defaultData.name != edited.name ||
                defaultData.description != edited.description ||
                defaultData.email != edited.email

        val colorsChanged = defaultData.name_color != edited.name_color ||
                defaultData.description_color != edited.description_color

        val isValid = inputVerifier.checkAll() && textChanged || colorsChanged ||
                passwdVerifier.getChecked().first || userLogoEditor.getLogo().second

        submitter.isEnabled = isValid
        val btnColor = if (isValid) AppColors.GREEN else AppColors.BLUE
        val btnBackground = if (isValid) R.drawable.sign_submit_btn_active
        else R.drawable.sign_submit_btn_inactive
        submitter.setTextColor(btnColor.raw)
        submitter.setBackgroundResource(btnBackground)
    }

    private fun colorToHex(color: Int): String = String.format("#%06X", color and 0xFFFFFF).toLowerCase(Locale.ROOT)

    private fun addSubmitAction() {
        val checkIfEdited = { default: String, edited: String ->
            if (default == edited) null else edited
        }
        submitter.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val (passwdModified, passwdStr) = passwdVerifier.getChecked()
                val passwd = if (passwdModified) passwdStr else null
                val (logoSrc, logoModified) = userLogoEditor.getLogo()
                val editedLogo = if (logoModified) logoSrc else null
                edited.name_color = edited.name_color
                edited.description_color = edited.description_color
                val container = UserContainer.EditData(
                        username = defaultData.name,
                        logo = editedLogo,
                        edited = UserContainer.EditedFields(
                                name = checkIfEdited(defaultData.name, edited.name!!),
                                description = checkIfEdited(defaultData.description, edited.description!!),
                                email = checkIfEdited(defaultData.email!!, edited.email!!),
                                name_color = checkIfEdited(defaultData.name_color, edited.name_color!!),
                                description_color = checkIfEdited(defaultData.description_color, edited.description_color!!),
                                passwd = passwd
                        )
                )

                val (st, _) = api.editUser(container)
                if (st == 200) onSubmitClb(container.copy(logo = logoSrc))
            }
        }
    }
}