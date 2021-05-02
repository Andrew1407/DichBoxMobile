package com.diches.dichboxmobile.api.boxes

import com.diches.dichboxmobile.api.ApiParser
import com.diches.dichboxmobile.datatypes.BoxesContainer

class BoxesAPI : ApiParser<BoxesContainer>() {
    private val service = retrofit.create(BoxesService::class.java)

    suspend fun getUserBoxes(getContainer: BoxesContainer.UserBoxesReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(getContainer)
        val response = service.getUserBoxes(reqBody)
        return getResponseData(
            response, BoxesContainer.UserBoxes::class.java
        )
    }

    suspend fun verify(verContainer: BoxesContainer.VerifyBody): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(verContainer)
        val response = service.verify(reqBody)
        return getResponseData(
                response, BoxesContainer.VerifyRes::class.java
        )
    }

    suspend fun createBox(addContainer: BoxesContainer.EditedBoxData): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(addContainer)
        val response = service.createBox(reqBody)
        return getResponseData(
                response, BoxesContainer.NameContainer::class.java
        )
    }

    suspend fun getBoxDetails(infoContainer: BoxesContainer.BoxDetailsReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(infoContainer)
        val response = service.getBoxDetails(reqBody)
        return getResponseData(
                response, BoxesContainer.BoxData::class.java
        )
    }

    suspend fun removeBox(rmContainer: BoxesContainer.RemoveBoxReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(rmContainer)
        val response = service.removeBox(reqBody)
        return getResponseData(
                response, BoxesContainer.Removed::class.java
        )
    }

    suspend fun editBox(editedContainer: BoxesContainer.EditedBoxData): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(editedContainer)
        val response = service.editBox(reqBody)
        return getResponseData(
                response, BoxesContainer.EditedFields::class.java
        )
    }

    suspend fun getPathFiles(pathContainer: BoxesContainer.PathEntriesReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(pathContainer)
        val response = service.getPathFiles(reqBody)
        return getResponseData(
                response, BoxesContainer.PathEntries::class.java
        )
    }

    suspend fun removeFile(rmContainer: BoxesContainer.FilePropertiesReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(rmContainer)
        val response = service.removeFile(reqBody)
        return getResponseData(
                response, BoxesContainer.RemoveFileRes::class.java
        )
    }

    suspend fun renameFile(renameContainer: BoxesContainer.RenameFileReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(renameContainer)
        val response = service.renameFile(reqBody)
        return getResponseData(
                response, BoxesContainer.RenameFileRes::class.java
        )
    }

    suspend fun createFile(createContainer: BoxesContainer.FilePropertiesReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(createContainer)
        val response = service.createFile(reqBody)
        return getResponseData(
                response, BoxesContainer.CreateFileRes::class.java
        )
    }

    suspend fun getFileEntries(getContainer: BoxesContainer.FilePropertiesReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(getContainer)
        val response = service.getFileEntries(reqBody)
        return getResponseData(
                response, BoxesContainer.FoundFile::class.java
        )
    }

    suspend fun saveFiles(saveContainer: BoxesContainer.SaveFilesReq): Pair<Int, BoxesContainer> {
        val reqBody = makeRequest(saveContainer)
        val response = service.saveFiles(reqBody)
        return getResponseData(
                response, BoxesContainer.SaveFilesRes::class.java
        )
    }

    override fun parseJSON(jsonStr: String, container: Class<*>): BoxesContainer {
        return BoxesContainer.parseJSON(jsonStr, container)
    }

    override fun stringifyJSON(entries: BoxesContainer): String {
        return BoxesContainer.stringifyJSON(entries)
    }
}