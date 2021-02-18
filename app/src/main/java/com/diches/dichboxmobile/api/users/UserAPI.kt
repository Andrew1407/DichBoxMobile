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
            .baseUrl("http://192.168.1.2:7041")
            .build()

        service = retrofit.create(UserService::class.java)
    }

    suspend fun search(searchContainer: UserContainer.SearchedChunk): UserContainer.FoundUsers {
        val reqBody = makeRequest(searchContainer)
        val response = service.search(reqBody)
        return getResponseData(
                response, UserContainer.FoundUsers::class.java
        ) as UserContainer.FoundUsers
    }

    suspend fun createUser(signContainer: UserContainer.SignUp): UserContainer.NameContainer {
        val reqBody = makeRequest(signContainer)
        val response = service.signUp(reqBody)
        return getResponseData(
                response, UserContainer.NameContainer::class.java
        ) as UserContainer.NameContainer
    }

    suspend fun enterUser(signContainer: UserContainer.SignUp): UserContainer.NameContainer {
        val reqBody = makeRequest(signContainer)
        val response = service.signIn(reqBody)
        return getResponseData(
                response, UserContainer.NameContainer::class.java
        ) as UserContainer.NameContainer
    }

    suspend fun verifyField(verContainer: UserContainer.VerifyField): UserContainer.VerifyFieldRes {
        val reqBody = makeRequest(verContainer)
        val response = service.verifyField(reqBody)
        return getResponseData(
                response, UserContainer.VerifyFieldRes::class.java
        ) as UserContainer.VerifyFieldRes
    }

    suspend fun verifyPassword(verContainer: UserContainer.VerifyPasswd): UserContainer.VerifyPasswdRes {
        val reqBody = makeRequest(verContainer)
        val response = service.verifyPasswd(reqBody)
        return getResponseData(
                response, UserContainer.VerifyPasswdRes::class.java
        ) as UserContainer.VerifyPasswdRes
    }

    private fun getResponseData(
            response: Response<ResponseBody>,
            respClass: Class<*>
    ): UserContainer {
        val resParsed = JsonParser.parseString(response.body()?.string())
        val resStrJSON = Gson().toJson(resParsed)
        return UserContainer.parseJSON(resStrJSON, respClass)
    }

    private fun makeRequest(entries: UserContainer): RequestBody {
        val entriesStr = UserContainer.stringifyJSON(entries)
        val mediaType = "application/json".toMediaTypeOrNull()
        return  entriesStr.toRequestBody(mediaType)
    }
}