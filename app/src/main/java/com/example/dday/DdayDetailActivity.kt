package com.example.dday

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.dday.model.Dday
import com.example.dday.utils.DateUtil
import com.google.android.material.card.MaterialCardView


class DdayDetailActivity : AppCompatActivity() {
    companion object {
        const val VIEW_NAME_CONTAINER = "detail:container"
        const val VIEW_NAME_DIFF = "detail:diff"
        const val VIEW_NAME_NAME = "detail:name"
        const val VIEW_NAME_YEAR = "detail:year"
        const val VIEW_NAME_MONTH = "detail:month"
        const val VIEW_NAME_DAY = "detail:day"
    }

    private lateinit var ctLayout: MaterialCardView
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

        ctLayout = findViewById(R.id.ddayDetailContainer)
        tvDiff = findViewById(R.id.ddayDetailDiff)
        tvName = findViewById(R.id.ddayDetailName)
        tvYear = findViewById(R.id.ddayDetailYear)
        tvMonth = findViewById(R.id.ddayDetailMonth)
        tvDay = findViewById(R.id.ddayDetailDay)
        lvRemainings = findViewById(R.id.ddayRemainings)

        dday = Dday.get(
            intent.getIntExtra("index", 0)
        ) ?: throw Exception()

        ViewCompat.setTransitionName(ctLayout, VIEW_NAME_CONTAINER)
        ViewCompat.setTransitionName(tvDiff, VIEW_NAME_DIFF)
        ViewCompat.setTransitionName(tvName, VIEW_NAME_NAME)
        ViewCompat.setTransitionName(tvYear, VIEW_NAME_YEAR)
        ViewCompat.setTransitionName(tvMonth, VIEW_NAME_MONTH)
        ViewCompat.setTransitionName(tvDay, VIEW_NAME_DAY)

//        val bounds = ChangeBounds()
//        bounds.setDuration(1000)
//        window.sharedElementEnterTransition = bounds

        setDdayInfo()
        setRemainings()
    }

    private fun setDdayInfo() {
        tvDiff.text = DateUtil.getDiffSTring(dday.diffToday)
        tvName.text = dday.name
        tvYear.text = dday.year.toString()
        tvMonth.text = getString(R.string.date_string_month, dday.month)
        tvDay.text = getString(R.string.date_string_day, dday.day)
    }
    private fun setRemainings() {
        lvRemainings.adapter = RemainingListAdapter(this, dday.getRemainings())
    }
}
