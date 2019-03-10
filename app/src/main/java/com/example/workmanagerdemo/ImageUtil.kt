package com.example.workmanagerdemo

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtil {

    fun saveBitmap(context: Context, bitmap: Bitmap, filename: String, callback: Callback<String>): Bitmap {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        val mediaByteArray = stream.toByteArray()
        saveFileToStorage(
            context,
            filename, mediaByteArray, callback
        )
        return bitmap
    }

    private fun saveFileToStorage(context: Context, filename: String, data: ByteArray, callback: Callback<String>) {
        val myDir = context.filesDir
        try {
            val path = "$myDir/media/"
            val secondFile = File("$myDir/media/", filename)

            if (!secondFile.parentFile.exists()) {
                secondFile.parentFile.mkdirs()
            }
            secondFile.createNewFile()

            val fos = FileOutputStream(secondFile)

            fos.write(data)
            fos.flush()
            fos.close()
            callback.onSuccess(path)
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError(e)
        }

    }
}