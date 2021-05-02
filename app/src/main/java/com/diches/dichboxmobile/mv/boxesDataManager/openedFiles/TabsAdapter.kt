package com.diches.dichboxmobile.mv.boxesDataManager.openedFiles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diches.dichboxmobile.R
import com.diches.dichboxmobile.mv.boxesDataManager.viewStates.OpenedFilesViewModel
import com.diches.dichboxmobile.tools.AppColors

class TabsAdapter(private val openedFilesVM: OpenedFilesViewModel) :
    RecyclerView.Adapter<TabsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.nameField)
        val closeIcon = view.findViewById<ImageView>(R.id.closeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.opened_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val openedFiles = openedFilesVM.liveData.value!!
        val item = openedFiles[position]
        val textView = holder.textView
        textView.text = item.name
        val background = if (item.opened) R.drawable.file_tab_opened else R.drawable.file_tab_border
        val textColor = if (item.opened) AppColors.BLACK else AppColors.BLUE
        holder.view.setBackgroundResource(background)
        textView.setTextColor(textColor.raw)
        holder.textView.setOnClickListener { openedFilesVM.openFile(position) }
        holder.closeIcon.setOnClickListener { openedFilesVM.closeFile(position) }
    }

    override fun getItemCount(): Int {
        val openedFiles = openedFilesVM.liveData.value
        return openedFiles?.size ?: 0
    }
}
