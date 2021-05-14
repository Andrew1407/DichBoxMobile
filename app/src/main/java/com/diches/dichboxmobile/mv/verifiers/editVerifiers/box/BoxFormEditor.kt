package com.diches.dichboxmobile.mv.verifiers.editVerifiers.box

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.addTextChangedListener
import com.diches.dichboxmobile.api.Statuses
import com.diches.dichboxmobile.api.user.UserAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.datatypes.UserContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.AccessListViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.TempAccessListsViewModel
import com.diches.dichboxmobile.mv.verifiers.editVerifiers.box.accessList.AccessList
import com.diches.dichboxmobile.tools.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BoxFormEditor(
        username: String,
        private val bundle: Bundle?,
        submitter: Button,
        private val boxDataVM: BoxDataViewModel
) : BoxFormGenerator(username, bundle, submitter) {
    private val boxApi = api
    private val userApi = UserAPI()
    private lateinit var editorsCopy: TempAccessListsViewModel
    private lateinit var viewersCopy: TempAccessListsViewModel
    private lateinit var editorsList: AccessListViewModel
    private lateinit var viewersList: AccessListViewModel
    private val modifiedFields = mutableMapOf(
            "name" to false,
            "editors" to false,
            "privacy" to false,
            "desc" to false,
            "nameColor" to false,
            "descColor" to false
    )

    init {
        val initialData = boxDataVM.liveData.value!!
        nameColor = bundle?.getString("nameColor") ?: initialData.name_color
        descriptionColor = bundle?.getString("descriptionColor") ?: initialData.description_color
        nameCorrect = true
    }

    fun setEditors(
            editedViewModel: TempAccessListsViewModel,
            listViewModel: AccessListViewModel
    ): BoxFormEditor {
        editorsCopy = editedViewModel
        editorsList = listViewModel
        return this
    }

    fun setViewers(
            editedViewModel: TempAccessListsViewModel,
            listViewModel: AccessListViewModel
    ): BoxFormEditor {
        viewersCopy = editedViewModel
        viewersList = listViewModel
        return this
    }

    fun fetchAccessLists(): BoxFormEditor {
        val box = boxDataVM.liveData.value!!
        val listsBody = UserContainer.AccessListsReq(box.owner_name, box.name)
        val (st, res) = runBlocking { userApi.getAccessLists(listsBody) }
        if (Statuses.OK.eqNot(st)) return this
        val (limitedUsers, editors) = res as UserContainer.AccessLists
        editorsCopy.setEditedAddedUsers(editors)
        viewersCopy.setEditedAddedUsers(limitedUsers)
        editorsList.setAddedUsers(editors.toList())
        viewersList.setAddedUsers(limitedUsers.toList())
        return this
    }

    override fun checkAll() {
        val initialData = boxDataVM.liveData.value!!
        modifiedFields["name"] = nameInput.text.toString() != initialData.name
        if (!nameCorrect && modifiedFields["name"]!!) return decorateSubmitter(false)
        val editorsNames = editorsContainer.getAddedUsers().map { it.name }
        val editorsCopyNames = editorsCopy.added.value!!.map { it.name }
        modifiedFields["editors"] = editorsNames != editorsCopyNames
        val logoEdited = logoEditor.getLogo().second
        val (currentPrivacy, limitedUsers) = privacyOptions.getPrivacy()
        var privacyChanged = currentPrivacy != initialData.access_level
        if (!privacyChanged && currentPrivacy == "limited") {
            val limitedNames = limitedUsers.map { it.name }
            val limitedCopyNames = viewersCopy.added.value!!.map { it.name }
            privacyChanged = limitedNames != limitedCopyNames
        }
        modifiedFields["privacy"] = privacyChanged
        modifiedFields["desc"] = descriptionInput.text.toString() != initialData.description
        modifiedFields["nameColor"] = nameColor != initialData.name_color
        modifiedFields["descColor"] = descriptionColor != initialData.description_color

        val editedAll = modifiedFields
                .map { it.value }
                .reduce { acc, b -> acc || b }
        decorateSubmitter(editedAll || logoEdited)
    }

    override fun verifyNameInput(name: EditText, warning: TextView): BoxFormEditor {
        super.verifyNameInput(name, warning)
        if (bundle == null)
            nameInput.setText(boxDataVM.liveData.value!!.name)
        return this
    }

    override fun addDescriptionInput(description: EditText): BoxFormEditor {
        super.addDescriptionInput(description)
        descriptionInput.addTextChangedListener { checkAll() }
        if (bundle == null)
            descriptionInput.setText(boxDataVM.liveData.value!!.description)
        return this
    }

    override suspend fun handleSubmit() {
        val boxFieldsEdited = modifiedFields
                .filter { it.key != "editors" }
                .map { it.value }
                .reduce { acc, b -> acc || b }

        val boxData = boxDataVM.liveData.value!!
        val (logo, logoModified) = logoEditor.getLogo()
        val (currentPrivacy, limitedUsers) = privacyOptions.getPrivacy()
        val editedValues = if (!boxFieldsEdited) null else BoxesContainer.EditedFields(
                name = if (!modifiedFields["name"]!!) null else nameInput.text.toString(),
                name_color = if (!modifiedFields["nameColor"]!!) null else nameColor,
                description = if (!modifiedFields["desc"]!!) null else descriptionInput.text.toString(),
                description_color = if (!modifiedFields["descColor"]!!) null else descriptionColor,
                access_level = currentPrivacy
        )

        val editedBody = BoxesContainer.EditedBoxData(
                username = boxData.owner_name,
                boxName = boxData.name,
                logo = if (logoModified) logo else null,
                boxData = editedValues,
                editors = if (!modifiedFields["editors"]!!) null
                    else editorsContainer.getAddedUsers().map { it.name },
                limitedUsers = if (currentPrivacy != "limited") null
                    else limitedUsers.map { it.name }
        )

        val (st, res) = withContext(Dispatchers.IO) { boxApi.editBox(editedBody) }
        if (Statuses.OK.eq(st)) submitClb(res)

    }

    override fun handleWarning(input: String, warning: String) {
        val nameChecked = if (input != boxDataVM.liveData.value!!.name) input else ""
        super.handleWarning(nameChecked, warning)
    }

    override fun addBoxPrivacyOptions(handler: BoxPrivacyHandler): BoxFormEditor {
        super.addBoxPrivacyOptions(handler)
        privacyOptions.setViewersChangeClb {
            modifiedFields["privacy"] = true
            checkAll()
        }
        return this
    }

    override fun addEditorList(handler: AccessList): BoxFormEditor {
        super.addEditorList(handler)
        editorsContainer.onListModified {
            modifiedFields["editors"] = true
            checkAll()
        }
        return this
    }
}