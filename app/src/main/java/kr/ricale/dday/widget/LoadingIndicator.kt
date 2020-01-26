package kr.ricale.dday.widget

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatDialog
import kr.ricale.dday.R

class LoadingIndicator(activity: Activity) {
    private val dialog = AppCompatDialog(activity)
    private var loading = 0

    init {
        dialog.setCancelable(false)
        dialog.window
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.widget_loading_indicator)
    }

    fun on() {
        loading += 1

        if(!isShowing()) {
            dialog.show()
        }
    }

    fun off() {
        if(loading > 0) {
            loading -= 1
        }

        if(isShowing() && loading <= 0) {
            dialog.dismiss()
        }
    }

    private fun isShowing() = dialog.isShowing
}