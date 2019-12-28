package com.example.dday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.dday.model.Remaining
import com.example.dday.utils.DateUtil

class RemainingListAdapter(context: Context, dates: List<Remaining>):
    ArrayAdapter<Remaining>(context, 0, dates) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?:
        LayoutInflater
            .from(context)
            .inflate(R.layout.listitem_remainings, parent, false)

        val tvName = itemView.findViewById<TextView>(R.id.remainingName)
        val tvDate = itemView.findViewById<TextView>(R.id.remainingDate)
        val tvCount = itemView.findViewById<TextView>(R.id.remainingCount)

        val remaining = getItem(position)
        val name = remaining?.name
        val date = remaining?.date
        val period =
            if(date != null) {
                DateUtil.getRemaingingDays(date)
            } else {
                0
            }

        tvName.text = name
        tvDate.text =
            if(date != null) {
                context.getString(
                    R.string.date_string,
                    date.year,
                    date.monthValue,
                    date.dayOfMonth
                )

            } else {
                ""
            }
        tvCount.text = DateUtil.getDiffString(period.toInt())

        val color = ContextCompat.getColor(
            context,
            if(period <= 0) {
                R.color.colorOnSurface
            } else {
                R.color.colorOnSurfaceInactive
            }
        )

        tvName.setTextColor(color)
        tvDate.setTextColor(color)
        tvCount.setTextColor(color)

        return itemView
    }
}