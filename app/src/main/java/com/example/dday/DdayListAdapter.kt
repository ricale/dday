package com.example.dday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.dday.model.Dday

class DdayListAdapter(context: Context, ddays: List<Dday>):
    ArrayAdapter<Dday>(context, 0, ddays) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?:
            LayoutInflater
                .from(context)
                .inflate(R.layout.listitem_dday, parent, false)

        val tvIndex = itemView.findViewById<TextView>(R.id.ddayListItemIndex)
        val tvName = itemView.findViewById<TextView>(R.id.ddayListItemName)
        val tvDate = itemView.findViewById<TextView>(R.id.ddayListItemDate)

        val dday = getItem(position)

        if(dday != null) {
            tvIndex.text = dday.index.toString()
            tvName.text = dday.name
            tvDate.text = dday.date
        }

        return itemView
    }
}