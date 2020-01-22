package com.example.dday

import android.app.Application
import com.example.dday.utils.ImageUtil

class DdayApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        ImageUtil.init(applicationContext)
    }
}