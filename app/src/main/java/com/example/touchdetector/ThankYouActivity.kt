package com.example.touchdetector

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class ThankYouActivity : AppCompatActivity() {
    private lateinit var phone: ImageView
    private lateinit var email: ImageView
    private lateinit var active: TextView

    private val PREF_STR = "preferences"
    private val KEY = "ID"
    private val monthMillis = 45 * 24 * 60 * 60 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        phone = findViewById(R.id.phone)
        email = findViewById(R.id.email)
        active = findViewById(R.id.active)

        val request =
            PeriodicWorkRequestBuilder<Worker>(1, TimeUnit.DAYS)
                // Additional configuration
                .build()

        WorkManager.getInstance(this).enqueue(request)

        phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:9618555740"))
            startActivity(intent)
        }

        email.setOnClickListener {
            Log.d("email", "clicked")
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, "rkannegu@gmail.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query regarding Social Media Study")
            startActivity(Intent.createChooser(intent, ""))

        }

        val sharedPref = getSharedPreferences(PREF_STR, Context.MODE_PRIVATE)
        val start = sharedPref.getLong("start", System.currentTimeMillis())

        val timer = object: CountDownTimer(start + monthMillis - System.currentTimeMillis(), 1000) {
            override fun onTick(p0: Long) {
                val elapsed = System.currentTimeMillis() - start
                val seconds = elapsed / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24

                active.text = "$days days, ${hours % 24} hours, ${minutes % 60} minutes, ${seconds % 60} seconds"
            }

            override fun onFinish() {
                //pass
            }
        }

        timer.start()


    }
}