package com.example.dday

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.dday.model.Dday
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class AddDdayActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextInputEditText
    private lateinit var dateEditText: TextInputEditText
    private lateinit var okButton: Button

    lateinit var name: String

    private var year: Int = -1
    private var month: Int = -1
    private var dayOfMonth: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dday)

        nameEditText = findViewById(R.id.ddayName)
        dateEditText = findViewById(R.id.ddayDate)
        okButton = findViewById(R.id.ddayAddButton)

        initDefaultDate()

        nameEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })

        dateEditText.setOnClickListener {
            showCalendarDialog()
        }

        okButton.setOnClickListener {
            Dday(name, "${year}-${month}-${dayOfMonth}").save()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev is MotionEvent && ev.action == MotionEvent.ACTION_DOWN) {
            val v: View? = this.currentFocus
            if(v is TextInputEditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                if(!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun setDateText(y: Int, m: Int, d: Int) {
        year = y
        month = m
        dayOfMonth = d
        dateEditText.setText(getString(R.string.date_string, year, month + 1, dayOfMonth))
    }

    private fun initDefaultDate() {
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()

        setDateText(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun showCalendarDialog() {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_dday_date, null)
        val picker: DatePicker = view.findViewById(R.id.ddayDatePicker)

        picker.updateDate(year, month, dayOfMonth)

        MaterialAlertDialogBuilder(this)
            .setPositiveButton("OK", null)
            .setOnDismissListener {
                setDateText(
                    picker.year,
                    picker.month,
                    picker.dayOfMonth
                )
            }
            .setView(view)
            .show()
    }
}
