package com.example.workmanagerdemo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class ImageDownloadWorker(private val mContext: Context, workerParameters: WorkerParameters) :
    Worker(mContext, workerParameters) {

    private val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @SuppressLint("RestrictedApi", "CheckResult")
    override fun doWork(): Result {
        Log.d("ayusch", Thread.currentThread().toString())
        displayNotification(ProgressUpdateEvent("Please wait...", 3, 0))
        val imagesJson = inputData.getString("images")
        val gson = Gson()
        val listOfImages = gson.fromJson<List<Image>>(imagesJson, object : TypeToken<List<Image>>() {}.type);

        listOfImages.forEachIndexed { index, image ->
            Thread.sleep(1000)
            downloadImage(image, index)
        }

        notificationManager.cancel(notificationId)
        return Result.Success()
    }

    @SuppressLint("CheckResult")
    private fun downloadImage(image: Image, index: Int) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(image.url)
            .build()

        try {
            val response = client.newCall(request).execute()
            val bitmap = BitmapFactory.decodeStream(response.body()?.byteStream())

            ImageUtil.saveBitmap(mContext, bitmap, image.title).subscribe({ img ->
                displayNotification(ProgressUpdateEvent(image.title, 3, index + 1))
                EventBus.getDefault().post(ImageDownloadedEvent(img, image.title, image.id))
            }, { error ->
                error.printStackTrace()
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val notificationId: Int = 500
    private val notificationChannel: String = "demo"

    private fun displayNotification(prog: ProgressUpdateEvent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannel,
                notificationChannel,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, notificationChannel)

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.custom_notif)
        remoteView.setImageViewResource(R.id.iv_notif, R.drawable.eminem)
        remoteView.setTextViewText(R.id.tv_notif_progress, "${prog.message} (${prog.progress}/${prog.total} complete)")
        remoteView.setTextViewText(R.id.tv_notif_title, "Downloading Images")
        remoteView.setProgressBar(R.id.pb_notif, prog.total, prog.progress, false)

        notificationBuilder
            .setContent(remoteView)
            .setSmallIcon(R.drawable.eminem)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onStopped() {
        super.onStopped()
        notificationManager.cancel(notificationId)
    }

    data class ProgressUpdateEvent(var message: String, var total: Int, var progress: Int)

}