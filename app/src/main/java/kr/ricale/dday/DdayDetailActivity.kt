package kr.ricale.dday

import android.animation.AnimatorSet
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ricale.dday.model.Dday
import kr.ricale.dday.utils.AnimatorFactory
import kr.ricale.dday.utils.DateUtil

class DdayDetailActivity : AppCompatActivity() {
    companion object {
        const val VIEW_NAME_CONTAINER = "detail:container"
        const val VIEW_NAME_DIFF = "detail:diff"
        const val VIEW_NAME_NAME = "detail:name"
        const val VIEW_NAME_YEAR = "detail:year"
        const val VIEW_NAME_MONTH = "detail:month"
        const val VIEW_NAME_DAY = "detail:day"

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

    private lateinit var rvRemainings: RecyclerView
    private lateinit var guideline: Guideline

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private lateinit var dday: Dday

    private var touchDownY = 0.0f
    private var touchUpY = 0.0f

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
        btnNotification= findViewById(R.id.ddayDetailStatusButton)
        rvRemainings = findViewById(R.id.ddayRemainings)
        guideline = findViewById(R.id.ddayDetailGuideline2)

        dday = Dday.get(
            intent.getIntExtra("index", 0)
        )

        setTransition()
        setDdayInfo()
        setRemainings()
        setDdayNotification()
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

    override fun onBackPressed() {
        if(showRemainingsNow) {
            hideRemainings()
        } else {
            btnRemainings.visibility = View.GONE
            btnNotification.visibility = View.GONE
            super.onBackPressed()
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        btnRemainings.visibility = View.VISIBLE
        btnNotification.visibility = View.VISIBLE
    }

    private fun setTransition() {
        ViewCompat.setTransitionName(ctLayout,
            VIEW_NAME_CONTAINER
        )
        ViewCompat.setTransitionName(tvDiff,
            VIEW_NAME_DIFF
        )
        ViewCompat.setTransitionName(tvName,
            VIEW_NAME_NAME
        )
        ViewCompat.setTransitionName(tvYear,
            VIEW_NAME_YEAR
        )
        ViewCompat.setTransitionName(tvMonth,
            VIEW_NAME_MONTH
        )
        ViewCompat.setTransitionName(tvDay,
            VIEW_NAME_DAY
        )

////        Scene Transition Animation Duration
//        val bounds = ChangeBounds()
//        bounds.duration = 200
//        window.sharedElementEnterTransition = bounds
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

    private fun isNotified(): Boolean {
        return Dday.getNotified()?.index != dday.index
    }

    // FIXME: remove duplicated
    private fun setDdayNotification() {
        val imageResId = if(isNotified()) {
            R.drawable.ic_bookmark_border_white_24dp
        } else {
            R.drawable.ic_bookmark_white_24dp
        }
        btnNotification.setImageResource(imageResId)

        btnNotification.setOnClickListener {
            if(isNotified()) {
                dday.setAsNotification()
                btnNotification.setImageResource(R.drawable.ic_bookmark_white_24dp)

            } else {
                dday.removeFromNotification()
                btnNotification.setImageResource(R.drawable.ic_bookmark_border_white_24dp)
            }
        }
    }
}
