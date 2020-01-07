package com.example.dday.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator

// TODO: make more generic if needed
object AnimatorFactory {
    fun create(
        startValue: Float,
        endValue: Float,
        duration: Long,
        listener: (ValueAnimator) -> Unit
    ): AnimatorSet {
        val animatorSet = AnimatorSet()
        val valueAnimator = ValueAnimator.ofFloat(startValue, endValue)
        valueAnimator.setEvaluator(EasingEvaluator(duration.toFloat()))
        valueAnimator.addUpdateListener(listener)
        animatorSet.playTogether(valueAnimator)
        animatorSet.duration = duration

        return animatorSet
    }
}