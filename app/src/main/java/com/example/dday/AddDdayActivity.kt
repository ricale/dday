package com.example.dday

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class AddDdayActivity : AppCompatActivity() {
    lateinit var dateEditText: TextInputEditText
    lateinit var calendarDialog: MaterialAlertDialogBuilder

    lateinit var name: String

    var year: Int = -1
    var month: Int = -1
    var dayOfMonth: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dday)

        dateEditText = findViewById(R.id.ddayDate)

        initDefaultDate()

        dateEditText.setOnClickListener {
            showCalendarDialog()
        }
    }

    private fun initDefaultDate() {
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    }

    fun showCalendarDialog() {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_dday_date, null)
        val picker: DatePicker = view.findViewById(R.id.ddayDatePicker)

        picker.updateDate(year, month, dayOfMonth)

        MaterialAlertDialogBuilder(this)
            .setPositiveButton("OK", null)
            .setOnDismissListener {
                dayOfMonth = picker.dayOfMonth
                month = picker.month + 1
                year = picker.year
                dateEditText.setText("${year}년 ${month}월 ${dayOfMonth}일")
            }
            .setView(view)
            .show()
    }
}
