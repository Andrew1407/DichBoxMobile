package com.diches.dichboxmobile.api

import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit

abstract class ApiParser <C> {
    protected val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.6:7041")
        .build()

    protected fun getResponseData(
        response: Response<ResponseBody>,
        respClass: Class<*>
    ): Pair<Int, C> {
        val resStr = if (response.isSuccessful) response.body() else response.errorBody()
        val resParsed = JsonParser.parseString(resStr?.string())
        val resStrJSON = Gson().toJson(resParsed)
        val finalRes = parseJSON(resStrJSON, respClass)
        return Pair(response.code(), finalRes)
    }

    protected fun makeRequest(entries: C): RequestBody {
        val entriesStr = stringifyJSON(entries)
        val mediaType = "application/json".toMediaTypeOrNull()
        return entriesStr.toRequestBody(mediaType)
    }

    protected abstract fun stringifyJSON(entries: C): String
    protected abstract fun parseJSON(jsonStr: String, container: Class<*>): C
}
