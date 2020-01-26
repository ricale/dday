package kr.ricale.dday

import android.app.Application
import kr.ricale.dday.utils.ImageUtil

class DdayApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        ImageUtil.init(applicationContext)
    }
}