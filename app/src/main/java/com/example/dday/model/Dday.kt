package com.example.dday.model

import com.example.dday.utils.Storage
import org.json.JSONObject

class Dday(var name: String, var date: String) {
    companion object {
        const val KEY_PREFIX = "dday"
        const val SEPARATOR = "-"
        const val LAST_INDEX = "lastIndex"

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
                it.contains(KEY_PREFIX) && !it.contains(LAST_INDEX)
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
            val lastIndex = Storage.get("${KEY_PREFIX}${SEPARATOR}${LAST_INDEX}", 0)
            return if(lastIndex is Int) {
                lastIndex + 1
            } else {
                0
            }

        }
    }

    var index = 0

    constructor(index: Int, name: String, date: String): this(name, date) {
        this.index = index
    }

    constructor(index: Int): this("", ""){
        val json: JSONObject = Storage.get("${KEY_PREFIX}${SEPARATOR}${index}") ?: throw Exception()

        this.index = index
        this.name = json.getString("name")
        this.date = json.getString("date")
    }

    fun save() {
        index = getIndex()
        val json: JSONObject = JSONObject()
        json.put("index", index.toString())
        json.put("name", name)
        json.put("date", date)

        Storage.set("${KEY_PREFIX}${SEPARATOR}${index}", json)
        Storage.set("${KEY_PREFIX}${SEPARATOR}${LAST_INDEX}", index)
    }
}