package com.example.dday.model

import com.example.dday.utils.DateUtil
import com.example.dday.utils.Storage
import org.json.JSONObject
import org.threeten.bp.LocalDate

class Dday(var name: String, var date: String) {
    companion object {
        const val KEY_PREFIX = "dday"
        const val SEPARATOR = "-"
        const val LAST_INDEX_KEY = "lastIndex"

        val DAYS_TO_ADDS: IntArray = intArrayOf(100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)

        fun get(index: Int): Dday? {
            return try {
                Dday(index)
            } catch (e: Exception) {
                null
            }
        }

        fun getAll(): List<Dday> {
            val all = Storage.getAll()
            return all.filterKeys {
                it.contains(KEY_PREFIX) && !it.contains(LAST_INDEX_KEY)
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
            val lastIndex = Storage.get("${KEY_PREFIX}${SEPARATOR}${LAST_INDEX_KEY}", 0)
            return if(lastIndex is Int) {
                lastIndex + 1
            } else {
                0
            }

        }
    }

    var index = 0
    var year = 0
    var month = 0
    var day = 0
    var diffToday = 0

    constructor(index: Int, name: String, date: String): this(name, date) {
        this.index = index
    }

    constructor(index: Int): this("", ""){
        val json: JSONObject = Storage.get("${KEY_PREFIX}${SEPARATOR}${index}") ?: throw Exception()

        this.index = index
        this.name = json.getString("name")
        this.date = json.getString("date")
        val split = this.date.split("-")
        year = split[0].toInt()
        month = split[1].toInt()
        day = split[2].toInt()

        diffToday = DateUtil.getRemaingingDays(LocalDate.of(year, month, day)).toInt()
        if(diffToday >= 0) {
            diffToday += 1
        }
    }

    fun save() {
        index = getIndex()
        val json: JSONObject = JSONObject()
        json.put("index", index.toString())
        json.put("name", name)
        json.put("date", date)

        Storage.set("${KEY_PREFIX}${SEPARATOR}${index}", json)
        Storage.set("${KEY_PREFIX}${SEPARATOR}${LAST_INDEX_KEY}", index)
    }

    fun getRemainings(): List<LocalDate> {
        val split = date.split(SEPARATOR)
        val localDate = LocalDate.of(split[0].toInt(), split[1].toInt(), split[2].toInt())

        return DAYS_TO_ADDS.map {
            localDate.plusDays(it.toLong() - 1)
        }
    }
}