package com.example.dday

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.ViewCompat
import com.example.dday.model.Dday
import com.example.dday.utils.DateUtil
import com.example.dday.utils.EasingEvaluator

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
    }

    private lateinit var ctLayout: FrameLayout
    private lateinit var tvDiff: TextView
    private lateinit var tvName: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvMonth: TextView
    private lateinit var tvDay: TextView
    private lateinit var btnRemainings: ImageButton
    private lateinit var lvRemainings: ListView
    private lateinit var guideline: Guideline

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
        tvDiff = findViewById(R.id.ddayDetailDiff)
        tvName = findViewById(R.id.ddayDetailName)
        tvYear = findViewById(R.id.ddayDetailYear)
        tvMonth = findViewById(R.id.ddayDetailMonth)
        tvDay = findViewById(R.id.ddayDetailDay)
        btnRemainings = findViewById(R.id.ddayDetailRemainingButton)
        lvRemainings = findViewById(R.id.ddayRemainings)
        guideline = findViewById(R.id.ddayDetailGuideline2)

        dday = Dday.get(
            intent.getIntExtra("index", 0)
        ) ?: throw Exception()

        setTransition()
        setDdayInfo()
        setRemainings()
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
            super.onBackPressed()
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        btnRemainings.visibility = View.VISIBLE
    }

    private fun setTransition() {
        ViewCompat.setTransitionName(ctLayout, VIEW_NAME_CONTAINER)
        ViewCompat.setTransitionName(tvDiff, VIEW_NAME_DIFF)
        ViewCompat.setTransitionName(tvName, VIEW_NAME_NAME)
        ViewCompat.setTransitionName(tvYear, VIEW_NAME_YEAR)
        ViewCompat.setTransitionName(tvMonth, VIEW_NAME_MONTH)
        ViewCompat.setTransitionName(tvDay, VIEW_NAME_DAY)

////        Scene Transition Animation Duration
//        val bounds = ChangeBounds()
//        bounds.setDuration(1000)
//        window.sharedElementEnterTransition = bounds
    }

    private fun setDdayInfo() {
        tvDiff.text = DateUtil.getDiffString(dday.diffToday)
        tvName.text = dday.name
        tvYear.text = dday.year.toString()
        tvMonth.text = getString(R.string.date_string_month, dday.month)
        tvDay.text = getString(R.string.date_string_day, dday.day)
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
        lvRemainings.adapter = RemainingListAdapter(this, dday.getRemainings())
        btnRemainings.setOnClickListener {
            showRemainings()
        }

        startAnimatorSet = AnimatorSet()
        val valueAnimator1 = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator1.setEvaluator(EasingEvaluator(SWIPE_ANIM_DURATION.toFloat()))
        valueAnimator1.addUpdateListener {
            guideline.setGuidelinePercent(it.animatedValue as Float)
        }
        startAnimatorSet.playTogether(valueAnimator1)
        startAnimatorSet.duration = SWIPE_ANIM_DURATION

        endAnimatorSet = AnimatorSet()
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.setEvaluator(EasingEvaluator(SWIPE_ANIM_DURATION.toFloat()))
        valueAnimator.addUpdateListener {
            guideline.setGuidelinePercent(it.animatedValue as Float)
        }
        endAnimatorSet.playTogether(valueAnimator)
        endAnimatorSet.duration = SWIPE_ANIM_DURATION
    }
}
