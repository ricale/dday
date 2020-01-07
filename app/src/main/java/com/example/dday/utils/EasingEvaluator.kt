package com.example.dday.utils

import android.animation.TypeEvaluator

// http://shrikanth.in/android/2015/11/12/easing-animations-android.html
// https://gist.github.com/jacksonej/e635fc497b7627689050
class EasingEvaluator(val duration: Float): TypeEvaluator<Number> {

    override fun evaluate(fraction: Float, startValue: Number, endValue: Number): Float {
        return calculate(
            duration * fraction,
            startValue.toFloat(),
            endValue.toFloat() - startValue.toFloat(),
            duration
        )
    }

    private fun calculate(tOrig: Float, b: Float, c: Float, d: Float): Float{
        val t = tOrig / d
        return c * t * t + b;
    }
}