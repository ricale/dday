package kr.ricale.dday.utils

import android.graphics.Bitmap
import android.os.AsyncTask
import kr.ricale.dday.model.Dday

class LoadImageTask(private val listener: Listener): AsyncTask<Dday, Void, Bitmap>() {

    override fun doInBackground(vararg dday: Dday?): Bitmap {
        val bitmap = dday[0]?.getThumbnail()
        return bitmap!!
    }

    override fun onPostExecute(result: Bitmap?) {
        listener.onSuccess(result)
    }

    interface Listener {
        fun onSuccess(bitmap: Bitmap?)
    }
}