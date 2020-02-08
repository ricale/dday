package kr.ricale.dday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.ricale.dday.model.Remaining
import kr.ricale.dday.utils.DateUtil

class RemainingRecyclerAdapter(private val myDataset: Array<Remaining>):
        RecyclerView.Adapter<RemainingRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_remainings, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = myDataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemView = holder.view

        val tvName = itemView.findViewById<TextView>(R.id.remainingName)
        val tvDate = itemView.findViewById<TextView>(R.id.remainingDate)
        val tvCount = itemView.findViewById<TextView>(R.id.remainingCount)

        val remaining = myDataset[position]
        val name = remaining.name
        val date = remaining.date
        val period = DateUtil.getRemaingingDays(date)

        tvName.text = name
        tvDate.text =
            itemView.context.getString(
                R.string.date_string,
                date.year,
                date.monthValue,
                date.dayOfMonth
            )
        tvCount.text = DateUtil.getDiffString(period.toInt())

        val color = ContextCompat.getColor(
            itemView.context,
            if(period <= 0) {
                R.color.colorOnSurface
            } else {
                R.color.colorOnSurfaceInactive
            }
        )

        tvName.setTextColor(color)
        tvDate.setTextColor(color)
        tvCount.setTextColor(color)
    }
}