package com.example.dday.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    private lateinit var context: Context

    private fun getFile(dirname: String, filename: String): File {
        val cw = ContextWrapper(context)
        val directory = cw.getDir(dirname, Context.MODE_PRIVATE)
        if(!directory.exists()) {
            directory.mkdir()
        }
        return File(directory, filename)
    }

    fun init(c: Context) {
        context = c
    }

    fun getImage(dirname: String, filename: String): Bitmap? {
        val file = getFile(dirname, filename)

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    fun saveImage(dirname: String, filename: String, image: Bitmap) {
        val file = getFile(dirname, filename)
        val fos = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    }

    fun removeImage(dirname: String, filename: String) {
        val file = getFile(dirname, filename)
        file.delete()
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