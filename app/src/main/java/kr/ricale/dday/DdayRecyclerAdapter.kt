package kr.ricale.dday

import android.graphics.Bitmap
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.contains
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import kr.ricale.dday.model.Dday
import kr.ricale.dday.utils.DateUtil
import kr.ricale.dday.utils.LoadImageTask

class DdayRecyclerAdapter(private val ddays: ArrayList<Dday>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<DdayRecyclerAdapter.ViewHolder>() {

    private var checkable = false
    private var checkedIndices = HashSet<Int>()
    private var images = SparseArray<Bitmap>()
    private var loadingImages = SparseBooleanArray()

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val ivBackground: ImageView = view.findViewById(R.id.ddayListItemImage)
        private val tvDiff: TextView = view.findViewById(R.id.ddayListItemDiff)
        private val tvName: TextView = view.findViewById(R.id.ddayListItemName)
        private val tvYear: TextView = view.findViewById(R.id.ddayListItemYear)
        private val tvMonth: TextView = view.findViewById(R.id.ddayListItemMonth)
        private val tvDay: TextView = view.findViewById(R.id.ddayListItemDay)
        private val checkbox: MaterialCheckBox = view.findViewById(R.id.ddayListItemCheck)

        fun bind(dday: Dday, checkable: Boolean, listener: OnItemClickListener) {
            setDdayInfo(dday)
            setCheckbox(checkable)

            view.setOnClickListener {
                listener.onItemClick(view, dday)
            }
            view.setOnLongClickListener {
                listener.onItemLongClick(view, dday)
                true
            }
        }

        fun setImage(bitmap: Bitmap?) {
            ivBackground.setImageBitmap(bitmap)
        }

        fun setOnCheckedChangeListener(l: (v: CompoundButton, c: Boolean) -> Unit) {
            checkbox.setOnCheckedChangeListener(l)
        }

        private fun setDdayInfo(dday: Dday) {
            val context = view.context

            tvDiff.text = DateUtil.getDiffString(dday.diffToday)
            tvName.text = dday.name
            tvYear.text = dday.year.toString()
            tvMonth.text = context.getString(R.string.date_string_month, dday.month)
            tvDay.text = context.getString(R.string.date_string_day, dday.day)
        }

        private fun setCheckbox(checkable: Boolean) {
            checkbox.visibility = if(checkable) View.VISIBLE else View.GONE
            if(!checkable) {
                checkbox.isChecked = false
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(itemView: View, dday: Dday)
        fun onItemLongClick(itemView: View, dday: Dday)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_dday, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = ddays.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ddays[position], checkable, listener)

        val dday = ddays[position]
        val ddayIdx = dday.index

        if(images.contains(ddayIdx)) {
            holder.setImage(images.get(ddayIdx))

        } else {
            if(!loadingImages.contains(ddayIdx)) {
                loadingImages.put(ddayIdx, true)

                val loadImageTask = LoadImageTask(object :
                    LoadImageTask.Listener {
                    override fun onSuccess(bitmap: Bitmap?) {
                        images.put(ddayIdx, bitmap)
                        holder.setImage(bitmap)
                        loadingImages.delete(ddayIdx)
                    }
                })

                loadImageTask.execute(dday)
            }
        }

        holder.setOnCheckedChangeListener { _, isChecked ->
            val contains = checkedIndices.contains(position)
            if(isChecked) {
                if(!contains) {
                    checkedIndices.add(position)
                }
            } else {
                if(contains) {
                    checkedIndices.remove(position)
                }
            }
        }
    }

    fun addItem(dday: Dday) {
        ddays.add(dday)
        notifyDataSetChanged()
    }

    fun setCheckableMode() {
        checkable = true
        checkedIndices.clear()
        notifyDataSetChanged()
    }

    fun finishCheckableMode() {
        checkable = false
        notifyDataSetChanged()
    }

    fun removeSelectedItems() {
        checkedIndices.sortedWith(Comparator { a, b ->
            when {
                a < b -> 1
                a > b -> -1
                else -> 0
            }
        }).forEach {
            val dday = ddays[it]
            dday.remove()
            ddays.removeAt(it)
        }
        notifyDataSetChanged()
    }
}