package com.example.dday.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit

object DateUtil {
    fun getRemaingingDays(date: LocalDate): Long {
        return ChronoUnit.DAYS.between(
            date,
            LocalDate.now()
        )
    }

    fun getDiffSTring(diff: Int): String {
        return if(diff < 0) {
            "D-%d".format(diff*-1)
        } else {
            "D+%d".format(diff)
        }
    }
}