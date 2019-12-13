package com.example.dday.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.lang.Exception

object Storage {
    private lateinit var sharedPref: SharedPreferences

    fun init(context: Context) {
        if(!this::sharedPref.isInitialized) {
            sharedPref = context.getSharedPreferences("temp", Context.MODE_PRIVATE)
        }
    }

    fun set(key: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun set(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun set(key: String, value: JSONObject) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value.toString())
        editor.apply()
    }

    fun getAll(): Map<String, *> {
        return sharedPref.all
    }

    fun get(key: String, defValue: String): String? {
        return sharedPref.getString(key, defValue)
    }

    fun get(key: String, defValue: Int): Int? {
        return sharedPref.getInt(key, defValue)
    }

    fun get(key: String): JSONObject? {
        val jsonString: String? = Storage.sharedPref.getString(key, null)

        return try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            null
        }
    }
}