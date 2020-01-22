package com.example.dday.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.example.dday.model.Dday

// FIXME: log 삭제
class LoadImageTask(val context: Context, val listener: Listener): AsyncTask<Dday, Void, Bitmap>() {
    init {
        Log.i("LoadImageTask", "init")
    }

    var d: Dday? = null

    override fun doInBackground(vararg dday: Dday?): Bitmap {
        val bitmap = ImageUtil.getDdayThumbnail(context, dday[0]!!)
        d = dday[0]
        Log.i("LoadImageTask", "doInBackground "+dday[0]?.index)
        return bitmap!!
    }

    override fun onPostExecute(result: Bitmap?) {
        Log.i("LoadImageTask", "onPostExecute "+d?.index)
        listener.onSuccess(result)
    }

    interface Listener {
        fun onSuccess(bitmap: Bitmap?)
    }
}