package com.example.dday.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.dday.model.Dday
import java.io.File

object ImageUtil {
    fun getDdayThumbnail(context: Context, dday: Dday): Bitmap? {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("thumbnail", Context.MODE_PRIVATE)
        if(!directory.exists()) {
            directory.mkdir()
        }
        val path = File(directory, "thumbnail${dday.index}.png")

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(path.absolutePath, options)
    }
}