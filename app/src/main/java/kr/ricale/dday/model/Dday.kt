package kr.ricale.dday.model

import android.graphics.Bitmap
import android.os.AsyncTask
import kr.ricale.dday.utils.DateUtil
import kr.ricale.dday.utils.DdayNotification
import kr.ricale.dday.utils.ImageUtil
import kr.ricale.dday.utils.Storage
import org.json.JSONObject
import org.threeten.bp.LocalDate

class Dday(var name: String, date: String) {
    companion object {
        const val KEY_PREFIX = "dday"
        const val SEPARATOR = "-"
        const val LAST_INDEX_KEY = "lastIndex"
        const val NOTIFICATION_KEY = "notification"
        const val THUMBNAIL_DIR_NAME = "thumbnail"
        const val THUMBNAIL_FILE_PREFIX = "thumbnail"
        const val THUMBNAIL_FILE_EXT = "png"

        val DAYS_TO_ADDS = IntArray(73) { (it + 1) * 100 }

        val YEARS_TO_ADDS = IntArray(20) { it + 1 }

        fun get(index: Int): Dday {
            return Dday(index)
        }

        fun getAll(): List<Dday> {
            val all = Storage.getAll()
            return all.filterKeys {
                it.contains(KEY_PREFIX)
                        && !it.contains(LAST_INDEX_KEY)
                        && !it.contains(NOTIFICATION_KEY)
            }.keys.map {
                val splited = it.split("-")
                val index = splited.get(1).toInt()
                Dday(index)
            }.sortedWith(Comparator { a, b ->
                when {
                    a.index > b.index -> 1
                    a.index < b.index -> -1
                    else -> 0
                }
            })
        }

        fun getIndex(): Int {
            val lastIndex = Storage.get("$KEY_PREFIX$SEPARATOR$LAST_INDEX_KEY", 0)
            return lastIndex + 1

        }

        fun setNotified(index: Int?) {
            if(index is Int) {
                Storage.set("$KEY_PREFIX$SEPARATOR$NOTIFICATION_KEY", index)
            } else {
                Storage.remove("$KEY_PREFIX$SEPARATOR$NOTIFICATION_KEY")
            }
        }

        fun getNotified(): Dday? {
            val index = Storage.get("$KEY_PREFIX$SEPARATOR$NOTIFICATION_KEY", 0)
            return try {
                if (index == 0) null else get(index)
            } catch(e: Exception) {
                null
            }
        }
    }

    var index = 0
    var year = 0
    var month = 0
    var day = 0
    var diffToday = 0
    var date = date
        set(value) {
            if(value != "") {
                val split = value.split("-")
                year = split[0].toInt()
                month = split[1].toInt()
                day = split[2].toInt()

                diffToday = DateUtil.getRemainingDays(LocalDate.of(year, month, day)).toInt()
                if (diffToday >= 0) {
                    diffToday += 1
                }
            }
            field = value
        }

    constructor(index: Int, name: String, date: String): this(name, date) {
        this.index = index
    }

    constructor(index: Int): this("", "") {
        val json: JSONObject = Storage.get("$KEY_PREFIX$SEPARATOR${index}") ?: throw Exception("No record");

        this.index = index
        this.name = json.getString("name")
        this.date = json.getString("date")
    }

    private fun getThumbnailFilename(): String =
        "$THUMBNAIL_FILE_PREFIX${index}.$THUMBNAIL_FILE_EXT"

    private fun getStorageKey(): String =
        "$KEY_PREFIX$SEPARATOR${index}"

    private fun getStorageLastKey(): String =
        "$KEY_PREFIX$SEPARATOR$LAST_INDEX_KEY"

    fun save() {
        val isNewOne = index == 0
        if(isNewOne) {
            index = getIndex()
        }
        val json = JSONObject()
        json.put("index", index.toString())
        json.put("name", name)
        json.put("date", date)

        Storage.set(getStorageKey(), json)
        if(isNewOne) {
            Storage.set(getStorageLastKey(), index)
        }
    }

    fun remove() {
        removeFromNotification()
        Storage.remove(getStorageKey())
        AsyncTask.execute {
            ImageUtil.removeImage(
                THUMBNAIL_DIR_NAME,
                getThumbnailFilename()
            )
        }
    }

    fun getThumbnail(): Bitmap? {
        return ImageUtil.getImage(
            THUMBNAIL_DIR_NAME,
            getThumbnailFilename()
        )
    }

    fun saveThumbnail(image: Bitmap) {
        ImageUtil.saveImage(
            THUMBNAIL_DIR_NAME,
            getThumbnailFilename(),
            image
        )
    }

    fun isInNotification(): Boolean {
        return getNotified()?.index == index
    }

    fun setAsNotification() {
        setNotified(index)
        DdayNotification.show(get(index))
    }

    fun removeFromNotification() {
        DdayNotification.hide()
        setNotified(null)
    }

    fun getRemainings(): List<Remaining> {
        val split = date.split(SEPARATOR)
        val localDate = LocalDate.of(split[0].toInt(), split[1].toInt(), split[2].toInt())

        val addedByDays = DAYS_TO_ADDS.map {
            Remaining(
                "${it}일",
                localDate.plusDays(it.toLong() - 1)
            )
        }
        val addedByYears = YEARS_TO_ADDS.map {
            Remaining(
                "${it}년",
                localDate.plusYears(it.toLong())
            )
        }
        return (addedByDays + addedByYears).sortedWith(Comparator { a, b ->
            a.date.compareTo(b.date)
        })
    }
}