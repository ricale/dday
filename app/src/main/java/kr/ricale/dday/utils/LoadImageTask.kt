package kr.ricale.dday.utils

import android.graphics.Bitmap
import android.os.AsyncTask
import kr.ricale.dday.model.Dday

class LoadImageTask(private val listener: Listener): AsyncTask<Dday, Void, Bitmap>() {

    override fun doInBackground(vararg dday: Dday?): Bitmap? {
        return dday[0]?.getThumbnail()
    }

    override fun onPostExecute(result: Bitmap?) {
        listener.onSuccess(result)
    }

    interface Listener {
        fun onSuccess(bitmap: Bitmap?)
    }
}