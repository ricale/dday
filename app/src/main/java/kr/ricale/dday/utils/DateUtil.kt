package kr.ricale.dday.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

object DateUtil {
    fun getRemainingDays(date: LocalDate): Long {
        return ChronoUnit.DAYS.between(
            date,
            LocalDate.now()
        )
    }

    fun getDiffString(diff: Int): String {
        return if(diff < 0) {
            "D-%d".format(diff*-1)
        } else {
            "D+%d".format(diff)
        }
    }
}