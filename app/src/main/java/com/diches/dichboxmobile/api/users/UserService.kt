package com.diches.dichboxmobile.api.users

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
}