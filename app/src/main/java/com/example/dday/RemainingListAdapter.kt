package com.example.dday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.threeten.bp.LocalDate
import org.threeten.bp.Period

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
        val period = Period.between(LocalDate.now(), date)

        tvDate.text = date?.toString()
        tvCount.text = period.days.toString()

        return itemView
    }
}