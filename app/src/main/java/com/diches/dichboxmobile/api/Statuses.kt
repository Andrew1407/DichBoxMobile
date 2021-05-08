package com.diches.dichboxmobile.api

enum class Statuses(val code: Int) {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    SERVER_INTERNAL(500);

    fun eq(status: Int): Boolean = status == code
    fun eqNot(status: Int): Boolean = !eq(status)
}