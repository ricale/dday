package kr.ricale.dday.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DdayReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        DdayNotification.updateMessage()
    }
}
