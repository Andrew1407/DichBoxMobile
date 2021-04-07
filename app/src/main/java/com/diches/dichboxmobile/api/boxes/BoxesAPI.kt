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

    override fun parseJSON(jsonStr: String, container: Class<*>): BoxesContainer {
        return BoxesContainer.parseJSON(jsonStr, container)
    }

    override fun stringifyJSON(entries: BoxesContainer): String {
        return BoxesContainer.stringifyJSON(entries)
    }
}