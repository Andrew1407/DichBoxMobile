package com.diches.dichboxmobile.mv.verifiers.editVerifiers.box

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList.AccessList
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.logoEditors.BoxLogoEditor
import com.diches.dichboxmobile.tools.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

open class BoxFormGenerator(
        private val username: String,
        bundle: Bundle?,
        private val submitter: Button
) {
    protected val api = BoxesAPI()
    protected lateinit var nameInput: EditText
    private lateinit var nameWarning: TextView
    protected lateinit var descriptionInput: EditText
    protected lateinit var privacyOptions: BoxPrivacyHandler
    protected lateinit var editorsContainer: AccessList
    protected lateinit var logoEditor: BoxLogoEditor
    private val defaultColor = "#00d9ff"
    protected var nameColor: String = bundle?.getString("nameColor") ?: defaultColor
    protected var descriptionColor: String = bundle?.getString("descriptionColor") ?: defaultColor
    protected var nameCorrect: Boolean = false
    protected lateinit var submitClb: (container: BoxesContainer) -> Unit

    init {
        submitter.isEnabled = false
        submitter.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                handleSubmit()
            }
        }
    }

    fun afterSubmit(clb: (container: BoxesContainer) -> Unit): BoxFormGenerator {
        submitClb = clb
        return this
    }

    open fun verifyNameInput(name: EditText, warning: TextView): BoxFormGenerator {
        nameWarning = warning
        nameInput = name
        nameInput.setTextColor(Color.parseColor(nameColor))
        val validName = Regex("""^[^\s/]{1,40}${'$'}""")
        val nameInvalid =
                "Box name length should be 1-40 symbols (unique, no spaces and no path definitions like: \"../path1/path2/...\")"
        val nameTaken = "You already have a box with the same name"

        nameInput.addTextChangedListener {
            CoroutineScope(Dispatchers.Main).launch {
                val input = nameInput.text.toString()
                nameCorrect = when {
                    input.isEmpty() -> {
                        handleWarning(input, "")
                        false
                    }
                    !validName.matches(input) -> {
                        handleWarning(input, nameInvalid)
                        false
                    }
                    else -> {
                        val nameIsTaken = withContext(Dispatchers.IO) { checkBoxNameTaken(input) }
                        handleWarning(input, if (nameIsTaken) nameTaken else "")
                        !nameIsTaken
                    }
                }
                checkAll()
            }
        }

        return this
    }

    open fun addDescriptionInput(description: EditText): BoxFormGenerator {
        descriptionInput = description
        descriptionInput.setTextColor(Color.parseColor(descriptionColor))
        return this
    }

    fun setDescriptionColor(color: Int) {
        descriptionColor = colorToHex(color)
    }

    fun setNameColor(color: Int) {
        nameColor = colorToHex(color)
    }

    open fun addBoxPrivacyOptions(handler: BoxPrivacyHandler): BoxFormGenerator {
        privacyOptions = handler
        return this
    }

    open fun addEditorList(handler: AccessList): BoxFormGenerator {
        editorsContainer = handler
        return this
    }

    fun addLogoEditor(editor: BoxLogoEditor): BoxFormGenerator {
        logoEditor = editor
        return this
    }

    fun saveState(bundle: Bundle) {
        bundle.putString("logo", logoEditor.getLogo().first)
        bundle.putString("nameColor", nameColor)
        bundle.putString("descriptionColor", descriptionColor)
        privacyOptions.saveState(bundle)
        editorsContainer.saveState(bundle)
    }

    protected open fun handleWarning(input: String, warning: String) {
        if (input.isEmpty()) {
            DrawableCompat.setTint(nameInput.background, AppColors.BLUE.raw)
            nameWarning.text = ""
            return
        }
        nameWarning.text = warning
        val nameInputColor = if (warning.isEmpty()) AppColors.GREEN else AppColors.CRIMSON
        DrawableCompat.setTint(nameInput.background, nameInputColor.raw)
    }

    private suspend fun checkBoxNameTaken(input: String): Boolean {
        val verifyContainer = BoxesContainer.VerifyBody(username, input)
        val (st, res) = api.verify(verifyContainer)
        val (foundValue) = res as BoxesContainer.VerifyRes
        return foundValue != null
    }

    open fun checkAll() {
        decorateSubmitter(nameCorrect)
    }

    protected fun decorateSubmitter(validState: Boolean) {
        submitter.isEnabled = validState
        val btnColor = if (validState) AppColors.GREEN.raw else AppColors.BLUE.raw
        val shapeDrawable = ShapeDrawable()
        shapeDrawable.shape = RectShape()
        shapeDrawable.paint.color = btnColor
        shapeDrawable.paint.strokeWidth = 20f
        shapeDrawable.paint.style = Paint.Style.STROKE

        submitter.background = shapeDrawable
        submitter.setTextColor(btnColor)
    }

    private fun colorToHex(color: Int): String = String.format("#%06X", color and 0xFFFFFF).toLowerCase(Locale.ROOT)

    protected open suspend fun handleSubmit() {
        val editorsList = editorsContainer.getAddedUsers()
        val (privacy, limitedList) = privacyOptions.getPrivacy()
        val createdBody = BoxesContainer.EditedBoxData(
                username = username,
                logo = logoEditor.getLogo().first,
                editors = if (editorsList.isEmpty()) null else editorsList.map { it.name },
                limitedUsers = if(limitedList.isEmpty()) null else limitedList.map { it.name },
                boxData = BoxesContainer.EditedFields(
                        name = nameInput.text.toString(),
                        name_color = if (nameColor == defaultColor) null else nameColor,
                        description = descriptionInput.text.toString(),
                        description_color = if (descriptionColor == defaultColor) null else descriptionColor,
                        access_level = privacy
                )
        )

        val (st, res) = withContext(Dispatchers.IO) { api.createBox(createdBody) }
        if (Statuses.CREATED.eq(st)) submitClb(res)
    }
}