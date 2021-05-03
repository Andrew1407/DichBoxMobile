package com.diches.dichboxmobile.mv.boxesDataManager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.diches.dichboxmobile.R

class TypesSpinnerAdapter(
    private val context: Context,
    var items: List<String>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val row = convertView ?: LayoutInflater
            .from(context)
            .inflate(R.layout.boxes_types_spinner_item, parent, false)

        val item = row.findViewById<TextView>(R.id.boxTypesItem)
        item.text = items[position]
        return row
    }

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()
}