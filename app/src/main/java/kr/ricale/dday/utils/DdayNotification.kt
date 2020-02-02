package kr.ricale.dday.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.ricale.dday.R

// FIXME: use strings.xml
object DdayNotification {
    private const val DDAY_CHANNEL_ID = "default_channel"
    private const val DDAY_NOTIFICATION_ID = 131

    private lateinit var context: Context
//    private var

    fun init(c: Context) {
        context = c
        createChannel()
    }

    fun show(title: String, content: String) {
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_dday)
        notificationLayout.setTextViewText(R.id.notificationDdayTitle, title)
        notificationLayout.setTextViewText(R.id.notificationDdayContent, content)

        val notification = NotificationCompat.Builder(context, DDAY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(notificationLayout)
            .setShowWhen(false)
            .setOngoing(true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(DDAY_NOTIFICATION_ID, notification)
    }

    fun hide() {
        NotificationManagerCompat
            .from(context)
            .cancel(DDAY_NOTIFICATION_ID)
    }

    private fun createChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DDAY_CHANNEL_ID,
                "default",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}