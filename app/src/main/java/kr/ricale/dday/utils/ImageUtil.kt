package kr.ricale.dday.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    private lateinit var context: Context

    fun init(c: Context) {
        context = c
    }

    private fun getFile(dirname: String, filename: String): File {
        val cw = ContextWrapper(context)
        val directory = cw.getDir(dirname, Context.MODE_PRIVATE)
        if(!directory.exists()) {
            directory.mkdir()
        }
        return File(directory, filename)
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratio = width.toFloat() / height.toFloat()

        val newWidth = if(ratio > 1) maxWidth else (maxWidth * ratio).toInt()
        val newHeight = if(ratio > 1) (maxWidth / ratio).toInt() else maxWidth

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val imageStream = context.contentResolver.openInputStream(uri)
        val exif = ExifInterface(imageStream!!)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees = when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        val matrix = Matrix()
        matrix.preRotate(rotationInDegrees.toFloat())

        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun getImage(dirname: String, filename: String): Bitmap? {
        val file = getFile(dirname, filename)
        if(!file.isFile) {
            return null
        }

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
        if(file.isFile) {
            file.delete()
        }
    }

    fun getImageFromUri(imageUri: Uri): Bitmap {
        return getImageFromUri(imageUri, null)
    }

    fun getImageFromUri(imageUri: Uri, maxWidth: Int?): Bitmap {
        val bitmap = getBitmapFromUri(imageUri)

        return if(maxWidth == null) {
            bitmap
        } else {
            resizeBitmap(bitmap, maxWidth)
        }
    }
}