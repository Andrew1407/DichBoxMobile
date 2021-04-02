package com.diches.dichboxmobile.datatypes

sealed class BoxesContainer {
    companion object {
        fun parseJSON(
            jsonStr: String,
            container: Class<*>
        ): BoxesContainer = com.diches.dichboxmobile.datatypes.parseJSON(jsonStr, container) as BoxesContainer

        fun stringifyJSON(jsonObj: BoxesContainer): String = com.diches.dichboxmobile.datatypes.stringifyJSON(jsonObj)
    }

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
        val editor: Boolean
    ) : BoxesContainer()

    data class UserBoxesReq(
        val viewerName: String?,
        val boxOwnerName: String
    ) : BoxesContainer()

    data class UserBoxes(val boxesList: List<BoxDataListItem>): BoxesContainer()
}
