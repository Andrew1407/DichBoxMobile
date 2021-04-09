package com.diches.dichboxmobile.datatypes

sealed class BoxesContainer {
    companion object {
        fun parseJSON(
            jsonStr: String,
            container: Class<*>
        ): BoxesContainer = com.diches.dichboxmobile.datatypes.parseJSON(jsonStr, container) as BoxesContainer

        fun stringifyJSON(jsonObj: BoxesContainer): String = com.diches.dichboxmobile.datatypes.stringifyJSON(jsonObj)
    }

    data class NameContainer(val name: String) : BoxesContainer()

    data class BoxDataListItem(
        val name: String,
        val name_color: String,
        val owner_name: String,
        val access_level: String
    ) : BoxesContainer()

    data class BoxData(
        val name: String,
        val name_color: String,
        val description: String,
        val description_color: String,
        val owner_name: String,
        val access_level: String,
        val owner_nc: String,
        val reg_date: String,
        val last_edited: String,
        val editor: Boolean,
        val logo: String?
    ) : BoxesContainer()

    data class UserBoxesReq(
        val viewerName: String?,
        val boxOwnerName: String
    ) : BoxesContainer()

    data class UserBoxes(val boxesList: List<BoxDataListItem>): BoxesContainer()

    data class EditedFields(
            val name: String?,
            val name_color: String?,
            val description: String?,
            val description_color: String?,
            val access_level: String?,
            val last_edited: String? = null,
            var logo: String? = null
    ) : BoxesContainer()

    data class EditedBoxData(
            val username: String,
            val logo: String?,
            val boxData: EditedFields?,
            val editors: List<String>?,
            val limitedUsers: List<String>?,
            val boxName: String? = null
            ) : BoxesContainer()

    data class VerifyBody(
            val username: String,
            val boxName: String
    ) : BoxesContainer()

    data class VerifyRes(val foundValue: String?) : BoxesContainer()

    data class BoxDetailsReq(
            val ownerName: String,
            val viewerName: String?,
            val boxName: String
    ) : BoxesContainer()

    data class RemoveBoxReq(
            val username: String,
            val boxName: String,
            val confirmation: String,
            val ownPage: Boolean
    ) : BoxesContainer()

    data class Removed(val removed: Boolean) : BoxesContainer()
}
