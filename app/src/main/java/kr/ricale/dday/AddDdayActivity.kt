package kr.ricale.dday

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kr.ricale.dday.model.Dday
import kr.ricale.dday.utils.ImageUtil
import kr.ricale.dday.widget.LoadingIndicator
import org.threeten.bp.LocalDate
import java.io.FileNotFoundException

// FIXME: rename activity
class AddDdayActivity : AppCompatActivity() {
    companion object {
        const val TAG = "AddDdayActivity"
        const val REQUEST_GET_IMAGE = 101
        const val IMAGE_SIZE = 1200
    }
    private lateinit var imageView: ImageView
    private lateinit var imageButton: ImageButton
    private lateinit var nameEditText: TextInputEditText
    private lateinit var dateEditText: TextInputEditText
    private lateinit var okButton: Button

    private lateinit var image: Bitmap
    private lateinit var loadingIndicator: LoadingIndicator

    private lateinit var dday: Dday

    private lateinit var name: String

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

        loadingIndicator = LoadingIndicator(this)

        val index = intent.getIntExtra("index", 0)
        if(index != 0) {
            dday = Dday.get(index)
            name = dday.name
            nameEditText.setText(name)
            setDateText(dday.year, dday.month, dday.day)
            imageView.setImageBitmap(dday.getThumbnail())
            enableOkButtonIfNeeded()
        } else {
            initDefaultDate()
        }

        setEventHandlers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_GET_IMAGE) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    try {
                        image = ImageUtil.getImageFromUri(data.data!!, IMAGE_SIZE)
                        imageView.setImageBitmap(image)
                        enableOkButtonIfNeeded()
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GET_IMAGE)
    }

    private fun setEventHandlers() {
        imageView.setOnClickListener {
            openGallery()
        }
        imageButton.setOnClickListener {
            openGallery()
        }

        nameEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
                enableOkButtonIfNeeded()
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
            loadingIndicator.on()

            val dateString = getString(
                R.string.date_string,
                year,
                month,
                dayOfMonth
            )

            val newOrUpdated = if(this::dday.isInitialized) {
                dday.name = name
                dday.date = dateString
                dday
            } else {
                Dday(name, dateString)
            }
            newOrUpdated.save()

            AsyncTask.execute(fun () {
                if(this::image.isInitialized) {
                    newOrUpdated.saveThumbnail(image)
                }

                val returnIntent = Intent()
                returnIntent.putExtra("index", newOrUpdated.index)
                setResult(RESULT_OK, returnIntent)
                loadingIndicator.off()
                finish()
            })
        }
    }

    private fun enableOkButtonIfNeeded() {
        okButton.isEnabled = (
            this::name.isInitialized && !name.isBlank()
//                    && this::image.isInitialized
        )
    }

    private fun showCalendarDialog() {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener {_, y, m, d ->
            setDateText(y, m + 1, d)
        }, year, month - 1, dayOfMonth).show()
    }
}
