package com.diches.dichboxmobile.mv.boxesDataManager

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.diches.dichboxmobile.api.boxes.BoxesAPI
import com.diches.dichboxmobile.datatypes.BoxesContainer
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.BoxDataViewModel
import com.diches.dichboxmobile.tools.AppColors
import com.diches.dichboxmobile.tools.decorateView
import com.diches.dichboxmobile.tools.fillView
import com.diches.dichboxmobile.tools.fromBase64ToBitmap
import kotlinx.coroutines.runBlocking

class BoxProfiler(private val boxState: BoxDataViewModel) {
    private val api = BoxesAPI()
    private lateinit var boxDetails: BoxesContainer.BoxData

    fun fillViewModel(usernames: Pair<String?, String>, boxName: String): BoxProfiler {
        val stateData = boxState.liveData.value
        boxDetails = stateData ?: fetchBoxData(usernames, boxName)
        return this
    }

    fun setLogo(logoView: ImageView): BoxProfiler {
        val logoSrc = boxDetails.logo
        val hasLogo = logoSrc != null
        logoView.isVisible = hasLogo
        if (hasLogo) {
            val decoded = fromBase64ToBitmap(logoSrc!!)
            logoView.setImageBitmap(decoded)
        }
        return this
    }

    fun fillBoxName(boxNameView: TextView): BoxProfiler {
        fillView(boxNameView, Pair(boxDetails.name, boxDetails.name_color))
        return this
    }

    fun fillDescription(descriptionView: TextView): BoxProfiler {
        if (boxDetails.description.isEmpty()) {
            descriptionView.visibility = View.GONE
        } else {
            descriptionView.visibility = View.VISIBLE
            fillView(descriptionView, Pair(boxDetails.description, boxDetails.description_color))
        }
        return this
    }

    fun fillCreator(creatorView: TextView): BoxProfiler {
        val prefix = "Creator: "
        val color = Color.parseColor(boxDetails.owner_nc)
        decorateView(creatorView, prefix, Pair(boxDetails.owner_name, color))
        return this
    }

    fun fillType(typeView: TextView): BoxProfiler {
        val prefix = "Type: "
        val color = AppColors.ORANGE.raw
        decorateView(typeView, prefix, Pair(boxDetails.access_level, color))
        return this
    }

    fun fillDates(createdView: TextView, editedView: TextView): BoxProfiler {
        val color = AppColors.PURPLE.raw
        decorateView(createdView, "Created: ", Pair(boxDetails.reg_date, color))
        decorateView(editedView, "Last edited: ", Pair(boxDetails.last_edited, color))
        return this
    }

    fun refreshData() {
        val stateData = boxState.liveData.value
        if (stateData != null) boxDetails = stateData
    }

    private fun fetchBoxData(
            usernames: Pair<String?, String>,
            boxName: String
    ): BoxesContainer.BoxData {
        val (viewerName, ownerName) = usernames
        val detailsBody = BoxesContainer.BoxDetailsReq(ownerName, viewerName, boxName)
        val (st, res) = runBlocking { api.getBoxDetails(detailsBody) }
        val data = res as BoxesContainer.BoxData
        if (st == 200) boxState.setBoxData(data)
        return data
    }
}