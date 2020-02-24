package kr.ricale.dday.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kr.ricale.dday.DdayDetailActivity
import kr.ricale.dday.R
import kr.ricale.dday.model.Dday
import java.util.*

// FIXME: use strings.xml
object DdayNotification {
    private const val DDAY_CHANNEL_ID = "default_channel"
    private const val DDAY_NOTIFICATION_ID = 131

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager

    fun init(c: Context) {
        context = c
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        createChannel()
    }

    fun show(dday: Dday) {
        updateMessage(dday)
        setUpdater(dday)
    }

    fun hide() {
        NotificationManagerCompat
            .from(context)
            .cancel(DDAY_NOTIFICATION_ID)
        releaseUpdater()
    }

    fun updateMessage() {
        updateMessage(Dday.getNotified()!!)
    }

    fun updateMessage(dday: Dday) {
        val title = dday.name
        val content = DateUtil.getDiffString(dday.diffToday)

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_dday)
        notificationLayout.setTextViewText(R.id.notificationDdayTitle, title)
        notificationLayout.setTextViewText(R.id.notificationDdayContent, content)

        val intent = Intent(context, DdayDetailActivity::class.java).apply {
            putExtra("index", dday.index)
        }

        val pendingIndent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, DDAY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(notificationLayout)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentIntent(pendingIndent)
            .setShowWhen(false)
            .setOngoing(true)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(DDAY_NOTIFICATION_ID, notification)
    }

    private fun setUpdater(dday: Dday) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            getAlarmIntent(dday)
        )
    }

    private fun releaseUpdater() {
        alarmManager.cancel(getAlarmIntent())
    }

    private fun getAlarmIntent(): PendingIntent {
        return getAlarmIntent(Dday.getNotified()!!)
    }

    private fun getAlarmIntent(dday: Dday): PendingIntent {
        val receiverIntent = Intent(context, DdayReceiver::class.java)
        receiverIntent.putExtra("dday.index", dday.index)
        return PendingIntent.getBroadcast(context, 0, receiverIntent, 0)
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