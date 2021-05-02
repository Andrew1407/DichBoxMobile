package com.diches.dichboxmobile.api.boxes

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BoxesService {
    @POST("/boxes/create")
    suspend fun createBox(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/user_boxes")
    suspend fun getUserBoxes(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/verify")
    suspend fun verify(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/details")
    suspend fun getBoxDetails(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/remove")
    suspend fun removeBox(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/edit")
    suspend fun editBox(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/files/list")
    suspend fun getPathFiles(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/files/remove")
    suspend fun removeFile(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/files/rename")
    suspend fun renameFile(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/files/create")
    suspend fun createFile(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/files/get")
    suspend fun getFileEntries(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("/boxes/files/save")
    suspend fun saveFiles(@Body requestBody: RequestBody): Response<ResponseBody>
}
