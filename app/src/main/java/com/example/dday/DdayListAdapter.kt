package com.example.dday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.dday.model.Dday
import com.example.dday.utils.DateUtil

class DdayListAdapter(context: Context, ddays: List<Dday>):
    ArrayAdapter<Dday>(context, 0, ddays) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?:
            LayoutInflater
                .from(context)
                .inflate(R.layout.listitem_dday, parent, false)

        val tvDiff = itemView.findViewById<TextView>(R.id.ddayListItemDiff)
        val tvName = itemView.findViewById<TextView>(R.id.ddayListItemName)
        val tvYear = itemView.findViewById<TextView>(R.id.ddayListItemYear)
        val tvMonth = itemView.findViewById<TextView>(R.id.ddayListItemMonth)
        val tvDay = itemView.findViewById<TextView>(R.id.ddayListItemDay)

        val dday = getItem(position)

        if(dday != null) {
            tvDiff.text = DateUtil.getDiffSTring(dday.diffToday)
            tvName.text = dday.name
            tvYear.text = dday.year.toString()
            tvMonth.text = context.getString(R.string.date_string_month, dday.month)
            tvDay.text = context.getString(R.string.date_string_day, dday.day)
        }

        return itemView
    }
}