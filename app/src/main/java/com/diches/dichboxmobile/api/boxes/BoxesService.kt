package com.diches.dichboxmobile.api.boxes

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BoxesService {
    @POST("/boxes/user_boxes")
    suspend fun getUserBoxes(@Body requestBody: RequestBody): Response<ResponseBody>
}