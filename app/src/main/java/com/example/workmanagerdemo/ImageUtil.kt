package com.example.workmanagerdemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import io.reactivex.Observable
import io.reactivex.Single
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtil {

    @SuppressLint("CheckResult")
    fun saveBitmap(context: Context, bitmap: Bitmap, filename: String): Single<String> {
        return Single.create<String> { emitter ->
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
            val mediaByteArray = stream.toByteArray()

            try {
                val myDir = context.filesDir

                val path = "$myDir/media/"
                val secondFile = File("$myDir/media/", filename)

                if (!secondFile.parentFile.exists()) {
                    secondFile.parentFile.mkdirs()
                }
                secondFile.createNewFile()

                val fos = FileOutputStream(secondFile)
                fos.write(mediaByteArray)
                fos.flush()
                fos.close()
                emitter.onSuccess(path)
            } catch (exception: IOException) {
                exception.printStackTrace()
                emitter.onError(exception)
            }
        }
    }

}