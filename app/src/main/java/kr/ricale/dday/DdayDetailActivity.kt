package kr.ricale.dday

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ricale.dday.model.Dday
import kr.ricale.dday.utils.AnimatorFactory
import kr.ricale.dday.utils.DateUtil

class DdayDetailActivity : AppCompatActivity() {
    companion object {
        const val TAG = "DdayDetailActivity"
        const val VIEW_NAME_CONTAINER = "detail:container"
        const val VIEW_NAME_DIFF = "detail:diff"
        const val VIEW_NAME_NAME = "detail:name"
        const val VIEW_NAME_YEAR = "detail:year"
        const val VIEW_NAME_MONTH = "detail:month"
        const val VIEW_NAME_DAY = "detail:day"

        const val REQUEST_EDIT_DDAY = 0

        const val SWIPE_ANIM_DURATION = 500L
        const val MIN_DISTANCE_SWIPE = 100

        private const val SWIPE_ANIM_TOP_RATE = 0.0f
        private const val SWIPE_ANIM_BOTTOM_RATE = 1.0f
    }

    private lateinit var ctLayout: FrameLayout
    private lateinit var ivBackground: ImageView
    private lateinit var tvDiff: TextView
    private lateinit var tvName: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvMonth: TextView
    private lateinit var tvDay: TextView
    private lateinit var btnRemainings: ImageButton
    private lateinit var btnNotification: ImageButton
    private lateinit var btnEdit: ImageButton

    private lateinit var rvRemainings: RecyclerView
    private lateinit var guideline: Guideline

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private lateinit var dday: Dday

    private var touchDownY = 0.0f
    private var touchUpY = 0.0f

    private var edited = false
    private var showRemainingsNow = false
    private lateinit var startAnimatorSet: AnimatorSet
    private lateinit var endAnimatorSet: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dday_detail)

        ctLayout = findViewById(R.id.ddayDetailContainer)
        ivBackground = findViewById(R.id.ddayDetailImage)
        tvDiff = findViewById(R.id.ddayDetailDiff)
        tvName = findViewById(R.id.ddayDetailName)
        tvYear = findViewById(R.id.ddayDetailYear)
        tvMonth = findViewById(R.id.ddayDetailMonth)
        tvDay = findViewById(R.id.ddayDetailDay)
        btnRemainings = findViewById(R.id.ddayDetailRemainingButton)
        btnNotification = findViewById(R.id.ddayDetailStatusButton)
        btnEdit = findViewById(R.id.ddayDetailEditButton)
        rvRemainings = findViewById(R.id.ddayRemainings)
        guideline = findViewById(R.id.ddayDetailGuideline2)

        dday = Dday.get(
            intent.getIntExtra("index", 0)
        )

        btnEdit.setOnClickListener {
            if(dday.index != 0) {
                val intent = Intent(this, AddDdayActivity::class.java)
                intent.putExtra("index", dday.index)
                ActivityCompat.startActivityForResult(this, intent, REQUEST_EDIT_DDAY, null)
            }
        }

        setTransition()
        setDdayInfo()
        setRemainings()
        setDdayNotificationButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_EDIT_DDAY) {
            if(resultCode == RESULT_OK) {
                if(data != null) {
                    val index = data.getIntExtra("index", 0)
                    if(index != 0) {
                        edited = true
                        dday = Dday.get(index)
                        setDdayInfo()
                        setRemainings()
                        if(dday.isInNotification()) {
                            setDdayAsNotification()
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownY = event.y
            }
            MotionEvent.ACTION_UP -> {
                touchUpY = event.y
                if(MIN_DISTANCE_SWIPE < touchDownY - touchUpY) {
                    showRemainings()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    // FIXME: 버튼을 하나의 컨테이널 묶어서 visibility 컨트롤
    override fun onBackPressed() {
        if(showRemainingsNow) {
            hideRemainings()
        } else {
            btnRemainings.visibility = View.GONE
            btnNotification.visibility = View.GONE
            btnEdit.visibility = View.GONE

            if(edited) {
                clearTransition()
                val returnIntent = Intent()
                returnIntent.putExtra("edited", dday.index)
                setResult(RESULT_OK, returnIntent)
            }

            super.onBackPressed()
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        btnRemainings.visibility = View.VISIBLE
        btnNotification.visibility = View.VISIBLE
        btnEdit.visibility = View.VISIBLE
    }

    private fun setTransition() {
        ViewCompat.setTransitionName(ctLayout, VIEW_NAME_CONTAINER)
        ViewCompat.setTransitionName(tvDiff,   VIEW_NAME_DIFF)
        ViewCompat.setTransitionName(tvName,   VIEW_NAME_NAME)
        ViewCompat.setTransitionName(tvYear,   VIEW_NAME_YEAR)
        ViewCompat.setTransitionName(tvMonth,  VIEW_NAME_MONTH)
        ViewCompat.setTransitionName(tvDay,    VIEW_NAME_DAY)
    }

    private fun clearTransition() {
        ctLayout.transitionName = null
        tvDiff.transitionName = null
        tvName.transitionName = null
        tvYear.transitionName = null
        tvMonth.transitionName = null
        tvDay.transitionName = null
    }

    private fun setDdayInfo() {
        tvDiff.text = DateUtil.getDiffString(dday.diffToday)
        tvName.text = dday.name
        tvYear.text = dday.year.toString()
        tvMonth.text = getString(R.string.date_string_month, dday.month)
        tvDay.text = getString(R.string.date_string_day, dday.day)

        val bitmap = dday.getThumbnail()
        if(bitmap != null) {
            ivBackground.setImageBitmap(bitmap)
        }
    }


    private fun showRemainings() {
        showRemainingsNow = true
        startAnimatorSet.start()
    }

    private fun hideRemainings() {
        showRemainingsNow = false
        endAnimatorSet.start()
    }

    private fun setRemainings() {
        viewManager = LinearLayoutManager(this)
        viewAdapter =
            RemainingRecyclerAdapter(dday.getRemainings().toTypedArray())

        rvRemainings.layoutManager = viewManager
        rvRemainings.adapter = viewAdapter

        btnRemainings.setOnClickListener {
            showRemainings()
        }

        startAnimatorSet = AnimatorFactory.create(
            SWIPE_ANIM_BOTTOM_RATE,
            SWIPE_ANIM_TOP_RATE,
            SWIPE_ANIM_DURATION
        ) {
            guideline.setGuidelinePercent(it.animatedValue as Float)
        }

        endAnimatorSet = AnimatorFactory.create(
            SWIPE_ANIM_TOP_RATE,
            SWIPE_ANIM_BOTTOM_RATE,
            SWIPE_ANIM_DURATION
        ) {
            guideline.setGuidelinePercent(it.animatedValue as Float)
        }
    }

    private fun setDdayAsNotification() {
        dday.setAsNotification()
        btnNotification.setImageResource(R.drawable.ic_bookmark_white_24dp)
    }

    private fun removeDdayFromNotification() {
        dday.removeFromNotification()
        btnNotification.setImageResource(R.drawable.ic_bookmark_border_white_24dp)
    }

    // FIXME: remove duplicated
    private fun setDdayNotificationButton() {
        val imageResId = if(dday.isInNotification()) {
            R.drawable.ic_bookmark_border_white_24dp
        } else {
            R.drawable.ic_bookmark_white_24dp
        }
        btnNotification.setImageResource(imageResId)

        btnNotification.setOnClickListener {
            if(dday.isInNotification()) {
                removeDdayFromNotification()
            } else {
                setDdayAsNotification()
            }
        }
    }
}
