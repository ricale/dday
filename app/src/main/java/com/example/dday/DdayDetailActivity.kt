package com.example.dday

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dday.model.Dday

class DdayDetailActivity : AppCompatActivity() {

    private lateinit var tvIndex: TextView
    private lateinit var tvName: TextView
    private lateinit var tvDate: TextView

    private lateinit var lvRemainings: ListView

    private lateinit var dday: Dday

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_detail)

        tvIndex = findViewById(R.id.ddayDetailIndex)
        tvName = findViewById(R.id.ddayDetailName)
        tvDate = findViewById(R.id.ddayDetailDate)
        lvRemainings = findViewById(R.id.ddayRemainings)

        dday = Dday.get(
            intent.getIntExtra("index", 0)
        ) ?: throw Exception()

        setDdayInfo()
        setRemainings()
    }

    private fun setDdayInfo() {
        tvIndex.text = dday.index.toString()
        tvName.text = dday.name
        tvDate.text = dday.date
    }
    private fun setRemainings() {
        lvRemainings.adapter = RemainingListAdapter(this, dday.getRemainings())
    }
}
