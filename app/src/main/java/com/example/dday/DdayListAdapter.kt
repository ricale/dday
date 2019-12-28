package com.example.dday

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.dday.model.Dday
import com.example.dday.utils.DateUtil
import com.google.android.material.checkbox.MaterialCheckBox

class DdayListAdapter(context: Context, ddays: List<Dday>):
    ArrayAdapter<Dday>(context, 0, ddays) {

    private var checkedIndices = HashSet<Int>()
    private var checkable = false

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
        val checkbox = itemView.findViewById<MaterialCheckBox>(R.id.ddayListItemCheck)

        val dday = getItem(position)

        initCheckbox(checkbox, position)

        if(dday != null) {
            tvDiff.text = DateUtil.getDiffString(dday.diffToday)
            tvName.text = dday.name
            tvYear.text = dday.year.toString()
            tvMonth.text = context.getString(R.string.date_string_month, dday.month)
            tvDay.text = context.getString(R.string.date_string_day, dday.day)
        }

        return itemView
    }

    private fun initCheckbox(checkbox: MaterialCheckBox, position: Int) {
        checkbox.visibility = if(checkable) View.VISIBLE else View.GONE
        if(!checkable) {
            checkbox.isChecked = false
        }
        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
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
            val dday = getItem(it)
            dday?.remove()
            remove(dday)
        }
    }
}