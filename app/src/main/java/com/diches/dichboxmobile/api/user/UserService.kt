package com.diches.dichboxmobile.api.user

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("/users/verify")
    suspend fun verifyField(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/passwd_verify")
    suspend fun verifyPasswd(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/create")
    suspend fun signUp(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/enter")
    suspend fun signIn(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/remove")
    suspend fun removeUser(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/find")
    suspend fun findUser(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/search")
    suspend fun search(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/edit")
    suspend fun edit(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/subs_list")
    suspend fun getSubscriptions(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/subscription")
    suspend fun subscribeAction(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/notifications_list")
    suspend fun getNotifications(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/notifications_remove")
    suspend fun removeNotifications(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/names_list")
    suspend fun findUsernames(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/access_lists")
    suspend fun getAccessLists(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/users/identify")
    suspend fun getUsernameByUuid(@Body requestBody: RequestBody): Response<ResponseBody>
}
