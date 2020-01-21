package com.example.dday.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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

    fun getImageFromUri(resolver: ContentResolver, imageUri: Uri): Bitmap {
        return getImageFromUri(resolver, imageUri, null)
    }

    fun getImageFromUri(resolver: ContentResolver, imageUri: Uri, maxWidth: Int?): Bitmap {
        val imageStream = resolver.openInputStream(imageUri)
        val image = BitmapFactory.decodeStream(imageStream)

        return if(maxWidth == null) {
            image
        } else {
            resizeBitmap(image, maxWidth)
        }
    }

    fun resizeBitmap(image: Bitmap, maxWidth: Int): Bitmap {
        val width = image.width
        val height = image.height

        val ratio = width.toFloat() / height.toFloat()

        val newWidth = if(ratio > 1) maxWidth else (maxWidth * ratio).toInt()
        val newHeight = if(ratio > 1) (maxWidth / ratio).toInt() else maxWidth

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
    }
}