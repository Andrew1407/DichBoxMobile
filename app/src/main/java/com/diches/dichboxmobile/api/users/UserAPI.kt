package com.diches.dichboxmobile.api.users

import com.diches.dichboxmobile.datatypes.UserContainer
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit

class UserAPI {
    private val service: UserService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.3:7041")
            .build()

        service = retrofit.create(UserService::class.java)
    }

    suspend fun search(searchContainer: UserContainer.SearchedChunk): Pair<Int, UserContainer> {
        val reqBody = makeRequest(searchContainer)
        val response = service.search(reqBody)
        return getResponseData(
                response, UserContainer.FoundUsers::class.java
        )
    }

    suspend fun createUser(signContainer: UserContainer.SignUp): Pair<Int, UserContainer> {
        val reqBody = makeRequest(signContainer)
        val response = service.signUp(reqBody)
        return getResponseData(
                response, UserContainer.NameContainer::class.java
        )
    }

    suspend fun enterUser(signContainer: UserContainer.SignIn): Pair<Int, UserContainer> {
        val reqBody = makeRequest(signContainer)
        val response = service.signIn(reqBody)
        return getResponseData(
                response, UserContainer.NameContainer::class.java
        )
    }

    suspend fun findUser(signContainer: UserContainer.FindContainer): Pair<Int, UserContainer> {
        val reqBody = makeRequest(signContainer)
        val response = service.findUser(reqBody)
        return getResponseData(
                response, UserContainer.UserData::class.java
        )
    }

    suspend fun verifyField(verContainer: UserContainer.VerifyField): Pair<Int, UserContainer> {
        val reqBody = makeRequest(verContainer)
        val response = service.verifyField(reqBody)
        return getResponseData(
                response, UserContainer.VerifyFieldRes::class.java
        )
    }

    suspend fun verifyPassword(verContainer: UserContainer.VerifyPasswd): Pair<Int, UserContainer> {
        val reqBody = makeRequest(verContainer)
        val response = service.verifyPasswd(reqBody)
        return getResponseData(
                response, UserContainer.VerifyPasswdRes::class.java
        )
    }

    suspend fun editUser(editContainer: UserContainer.EditData): Pair<Int, UserContainer> {
        val reqBody = makeRequest(editContainer)
        val response = service.edit(reqBody)
        return getResponseData(
                response, UserContainer.EditedFields::class.java
        )
    }

    private fun getResponseData(
            response: Response<ResponseBody>,
            respClass: Class<*>
    ): Pair<Int, UserContainer> {
        val resStr = if(response.isSuccessful) response.body() else response.errorBody()
        val resParsed = JsonParser.parseString(resStr?.string())
        val resStrJSON = Gson().toJson(resParsed)
        val finalRes = UserContainer.parseJSON(resStrJSON, respClass)
        return Pair(response.code(), finalRes)
    }

    private fun makeRequest(entries: UserContainer): RequestBody {
        val entriesStr = UserContainer.stringifyJSON(entries)
        val mediaType = "application/json".toMediaTypeOrNull()
        return entriesStr.toRequestBody(mediaType)
    }
}