package com.diches.dichboxmobile.datatypes

sealed class UserContainer {
    companion object {
        fun parseJSON(
            jsonStr: String,
            container: Class<*>
        ): UserContainer = com.diches.dichboxmobile.datatypes.parseJSON(jsonStr, container) as UserContainer

        fun stringifyJSON(jsonObj: UserContainer): String = com.diches.dichboxmobile.datatypes.stringifyJSON(jsonObj)
    }

    data class NameContainer(val name: String) : UserContainer()

    data class VerifyField(
            val inputField: String,
            val inputValue: String
    ) : UserContainer()

    data class VerifyFieldRes(val foundValue: String?) : UserContainer()

    data class VerifyPasswd(
            val username: String,
            val passwd: String
    ) : UserContainer()

    data class VerifyPasswdRes(val checked: Boolean) : UserContainer()

    data class SignUp(
            val name: String,
            val email: String,
            val passwd: String
    ) : UserContainer()

    data class SignIn(
            val email: String,
            val passwd: String
    ) : UserContainer()

    data class FoundUsers(val searched: List<FoundUser>) : UserContainer() {
        data class FoundUser(
            val name: String,
            val name_color: String,
            val logo: String? = null
        )
    }

    data class SearchedChunk(val searchStr: String) : UserContainer()

}
