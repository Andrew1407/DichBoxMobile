package com.diches.dichboxmobile.datatypes

import com.google.gson.Gson

internal fun <C> parseJSON(
    jsonStr: String,
    container: Class<C>
): C = Gson().fromJson(jsonStr, container) as C

internal fun <C> stringifyJSON(jsonObj: C): String = Gson().toJson(jsonObj)
