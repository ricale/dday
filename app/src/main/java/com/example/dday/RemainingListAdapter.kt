package com.example.dday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.dday.utils.DateUtil
import org.threeten.bp.LocalDate

class RemainingListAdapter(context: Context, dates: List<LocalDate>):
    ArrayAdapter<LocalDate>(context, 0, dates) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?:
            LayoutInflater
                .from(context)
                .inflate(R.layout.listitem_remainings, parent, false)

        val tvDate = itemView.findViewById<TextView>(R.id.remainingDate)
        val tvCount = itemView.findViewById<TextView>(R.id.remainingCount)

        val date = getItem(position)
        val period = if(date != null) {
            DateUtil.getRemaingingDays(date)
        } else {
            0
        }

        tvDate.text = if(date != null) {
            context.getString(
                R.string.date_string_formatted,
                date.year,
                date.monthValue,
                date.dayOfMonth
            )

        } else {
            ""
        }
        tvCount.text = DateUtil.getDiffSTring(period.toInt())

        return itemView
    }
}