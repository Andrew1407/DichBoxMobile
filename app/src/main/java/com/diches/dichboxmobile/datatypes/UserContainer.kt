package com.diches.dichboxmobile.datatypes

sealed class UserContainer {
    companion object {
        fun parseJSON(
            jsonStr: String,
            container: Class<*>
        ): UserContainer = com.diches.dichboxmobile.datatypes.parseJSON(jsonStr, container) as UserContainer

        fun stringifyJSON(jsonObj: UserContainer): String = com.diches.dichboxmobile.datatypes.stringifyJSON(jsonObj)
    }

    data class NameContainer(val name: String?) : UserContainer()

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

    data class Subscriptions(val subs: List<FoundUser>) : UserContainer()

    data class FoundUsers(val searched: List<FoundUser>) : UserContainer()
    data class FoundNamesList(val foundUsers: List<FoundUser>) : UserContainer()

    data class FoundUser(
        val name: String,
        val name_color: String,
        val logo: String? = null
    ) : UserContainer()

    data class SearchedChunk(val searchStr: String) : UserContainer()

    data class FindContainer(
            val pathName: String,
            val username: String?
    ) : UserContainer()

    data class UserData(
            val name: String,
            val name_color: String,
            val description: String,
            val description_color: String,
            val followers: Int,
            val reg_date: String,
            val logo: String?,
            val ownPage: Boolean,
            val follower: Boolean,
            val email: String? = null,
            val notifications: Int? = null
    ) : UserContainer()

    data class EditData(
            val username: String,
            val logo: String?,
            val edited: EditedFields
    ) : UserContainer()

    data class EditedFields(
            var name: String?,
            var name_color: String?,
            var description: String?,
            var description_color: String?,
            var email: String?,
            var passwd: String? = null
    ) : UserContainer()

    data class SubsAction(
            val action: String,
            val personName: String,
            val subscriptionName: String,
            val responseValues: Boolean = false
    ) : UserContainer()

    data class SubsActionRes(
            val unsubscribed: Boolean = false,
            val followers: Int = 0,
            val follower: Boolean = false
    ) : UserContainer()

    data class Notifications(val notifications: List<NotificationData>) : UserContainer()

    data class NotificationData(
            val id: Int,
            val icon: String?,
            val type: String,
            val msgEntries: List<String>,
            val note_date: String,
            val user_name: String? = null,
            val user_color: String? = null,
            val box_name: String? = null,
            val box_color: String? = null
    ) : UserContainer()

    data class NotificationsRemoved(
            val username: String,
            val ntsIds: List<Int>
    ) : UserContainer()

    data class RemovedRes(val removed: Boolean) : UserContainer()

    data class RemovedUser(
            val username: String,
            val confirmation: String
    ) : UserContainer()

    data class NameListSearch(
            val username: String,
            val nameTemplate: String
    ) : UserContainer()

    data class AccessListsReq(
            val username: String,
            val boxName: String
    ) : UserContainer()

    data class AccessLists(
            val limitedUsers: List<FoundUser>,
            val editors: List<FoundUser>
    ) : UserContainer()
}
