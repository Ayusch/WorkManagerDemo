package com.example.workmanagerdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.jakewharton.rxbinding3.view.clicks
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class MainActivity : AppCompatActivity() {

    val jsonString: String = "[\n" +
            "  {\n" +
            "    \"albumId\": 1,\n" +
            "    \"id\": 1,\n" +
            "    \"title\": \"Eminem\",\n" +
            "    \"url\": \"https://i.pinimg.com/originals/c4/14/4f/c4144fba258c2f0b4735180fe5d9a03b.jpg\",\n" +
            "    \"thumbnailUrl\": \"https://via.placeholder.com/150/92c952\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"albumId\": 1,\n" +
            "    \"id\": 2,\n" +
            "    \"title\": \"MEME\",\n" +
            "    \"url\": \"https://pics.me.me/eminems-emotions-suprised-sad-happy-curious-annoyed-excited-shocked-bored-13877943.png\",\n" +
            "    \"thumbnailUrl\": \"https://via.placeholder.com/150/771796\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"albumId\": 1,\n" +
            "    \"id\": 3,\n" +
            "    \"title\": \"Eminem News\",\n" +
            "    \"url\": \"https://www.sohh.com/wp-content/uploads/Eminem.jpg\",\n" +
            "    \"thumbnailUrl\": \"https://via.placeholder.com/150/24f355\"\n" +
            "  }]"


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_download.setOnClickListener {
            startWorker()
        }
    }

    private fun startWorker() {
        val data = Data.Builder()
            .putString("images", jsonString)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)

        val oneTimeRequest = OneTimeWorkRequest.Builder(ImageDownloadWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints.build())
            .addTag("demo")
            .build()

        Toast.makeText(this, "Starting worker", Toast.LENGTH_SHORT).show()

        WorkManager.getInstance(this)
            .enqueueUniqueWork("AndroidVille", ExistingWorkPolicy.KEEP, oneTimeRequest)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(imageDownloadedEvent: ImageDownloadedEvent) {
        val file = File("${imageDownloadedEvent.path}/${imageDownloadedEvent.name}")
        when (imageDownloadedEvent.id) {
            "1" -> Picasso.get().load(file).into(iv_1)
            "2" -> Picasso.get().load(file).into(iv_2)
            "3" -> Picasso.get().load(file).into(iv_3)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

}
