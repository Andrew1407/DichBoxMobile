package com.diches.dichboxmobile.api.user

import com.diches.dichboxmobile.api.ApiParser
import com.diches.dichboxmobile.datatypes.UserContainer
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit

class UserAPI : ApiParser<UserContainer>() {
    private val service: UserService = retrofit.create(UserService::class.java)

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

    suspend fun removeUser(rmContainer: UserContainer.RemovedUser): Pair<Int, UserContainer> {
        val reqBody = makeRequest(rmContainer)
        val response = service.removeUser(reqBody)
        return getResponseData(
                response, UserContainer.RemovedRes::class.java
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

    suspend fun getSubscriptions(nameContainer: UserContainer.NameContainer): Pair<Int, UserContainer> {
        val reqBody = makeRequest(nameContainer)
        val response = service.getSubscriptions(reqBody)
        return getResponseData(
                response, UserContainer.Subscriptions::class.java
        )
    }

    suspend fun subscribeAction(subContainer: UserContainer.SubsAction): Pair<Int, UserContainer> {
        val reqBody = makeRequest(subContainer)
        val response = service.subscribeAction(reqBody)
        return getResponseData(
                response, UserContainer.SubsActionRes::class.java
        )
    }

    suspend fun getNotifications(nameContainer: UserContainer.NameContainer): Pair<Int, UserContainer> {
        val reqBody = makeRequest(nameContainer)
        val response = service.getNotifications(reqBody)
        return getResponseData(
                response, UserContainer.Notifications::class.java
        )
    }

    suspend fun removeNotifications(rmContainer: UserContainer.NotificationsRemoved): Pair<Int, UserContainer> {
        val reqBody = makeRequest(rmContainer)
        val response = service.removeNotifications(reqBody)
        return getResponseData(
                response, UserContainer.RemovedRes::class.java
        )
    }

    override fun parseJSON(jsonStr: String, container: Class<*>): UserContainer {
        return UserContainer.parseJSON(jsonStr, container)
    }

    override fun stringifyJSON(entries: UserContainer): String {
        return UserContainer.stringifyJSON(entries)
    }
}