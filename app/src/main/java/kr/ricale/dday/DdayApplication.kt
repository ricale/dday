package kr.ricale.dday

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import kr.ricale.dday.utils.DdayNotification
import kr.ricale.dday.utils.ImageUtil
import kr.ricale.dday.utils.Storage

class DdayApplication: Application() {
//    private var isDarkUi = true

    override fun onCreate() {
        super.onCreate()

//        determineCurrentSystemTheme()

        ImageUtil.init(applicationContext)
        DdayNotification.init(applicationContext)
        Storage.init(applicationContext)
        AndroidThreeTen.init(this)
    }

//    fun isDarkSystemUi(): Boolean {
//        return isDarkUi
//    }
//
//    private fun determineCurrentSystemTheme() {
//        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//            Configuration.UI_MODE_NIGHT_YES -> isDarkUi = true
//            Configuration.UI_MODE_NIGHT_NO -> isDarkUi = false
//        }
//    }
}