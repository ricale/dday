package com.example.dday

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.dday.model.Dday
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.threeten.bp.LocalDate
import java.io.FileNotFoundException

class AddDdayActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_GET_IMAGE = 101
    }
    private lateinit var imageView: ImageView
    private lateinit var imageButton: FloatingActionButton
    private lateinit var nameEditText: TextInputEditText
    private lateinit var dateEditText: TextInputEditText
    private lateinit var okButton: Button

    lateinit var name: String

    private var year = -1
    private var month = -1
    private var dayOfMonth = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dday)

        imageView = findViewById(R.id.ddayBgImage)
        imageButton = findViewById(R.id.ddayBgImageButton)
        nameEditText = findViewById(R.id.ddayName)
        dateEditText = findViewById(R.id.ddayDate)
        okButton = findViewById(R.id.ddayAddButton)

        initDefaultDate()
        setEventHandlers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_GET_IMAGE) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    try {
                        val imageUri = data.data
                        val imageStream = contentResolver.openInputStream(imageUri!!)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        imageView.setImageBitmap(selectedImage)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        dateEditText.setText(
            getString(
                R.string.date_string_formatted,
                year,
                month,
                dayOfMonth
            )
        )
    }

    private fun initDefaultDate() {
        val today = LocalDate.now()

        setDateText(
            today.year,
            today.monthValue,
            today.dayOfMonth
        )
    }

    private fun setEventHandlers() {
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GET_IMAGE);
        }

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
            val newOne = Dday(
                name,
                getString(R.string.date_string, year, month, dayOfMonth)
            )
            newOne.save()

            val returnIntent = Intent()
            returnIntent.putExtra("index", newOne.index)
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }

    private fun showCalendarDialog() {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener {_, y, m, d ->
            setDateText(y, m + 1, d)
        }, year, month - 1, dayOfMonth).show()
    }
}
