package com.example.dday

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dday.model.Dday
import com.example.dday.utils.DateUtil

class DdayDetailActivity : AppCompatActivity() {

    private lateinit var tvDiff: TextView
    private lateinit var tvName: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvMonth: TextView
    private lateinit var tvDay: TextView

    private lateinit var lvRemainings: ListView

    private lateinit var dday: Dday

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_detail)

        tvDiff = findViewById(R.id.ddayDetailDiff)
        tvName = findViewById(R.id.ddayDetailName)
        tvYear = findViewById(R.id.ddayDetailYear)
        tvMonth = findViewById(R.id.ddayDetailMonth)
        tvDay = findViewById(R.id.ddayDetailDay)
        lvRemainings = findViewById(R.id.ddayRemainings)

        dday = Dday.get(
            intent.getIntExtra("index", 0)
        ) ?: throw Exception()

        setDdayInfo()
        setRemainings()
    }

    private fun setDdayInfo() {
        tvDiff.text = DateUtil.getDiffSTring(dday.diffToday)
        tvName.text = dday.name
        tvYear.text = dday.year.toString()
        tvMonth.text = dday.month.toString()
        tvDay.text = dday.day.toString()
    }
    private fun setRemainings() {
        lvRemainings.adapter = RemainingListAdapter(this, dday.getRemainings())
    }
}
