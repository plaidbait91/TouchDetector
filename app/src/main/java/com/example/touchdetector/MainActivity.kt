package com.example.touchdetector

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.installations.FirebaseInstallations


class MainActivity : AppCompatActivity() {
    private val PREF_STR = "preferences"
    private val KEY = "ID"

    private fun isAccessServiceEnabled(context: Context, accessibilityServiceClass: Class<*>): Boolean {
        val prefString = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return prefString != null && prefString.contains(context.packageName + "/" + accessibilityServiceClass.name)
    }

    private lateinit var first: TextView
    private lateinit var setting: TextView
    private lateinit var service: Button
    private lateinit var form: Button
    private lateinit var usage: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        first = findViewById(R.id.firstTime)
        service = findViewById(R.id.service)
        usage = findViewById(R.id.usage)
        setting = findViewById(R.id.settingText)
        form = findViewById(R.id.form)

        val sharedPref = getSharedPreferences(PREF_STR, Context.MODE_PRIVATE)

        if(sharedPref.getString(KEY, "unknown") != "unknown") {
            first.visibility = View.GONE
            form.visibility = View.GONE
        }

        else {
            FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    form.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSf4Aou2yModBQ4QmlWTZ59sLHNE4Gh-LoQQ6nIlpd9DERBkwQ/viewform?entry.86604369=${task.result}"))
                        startActivity(intent)
                    }
                } else {
                    Log.e("Installations", "Unable to get Installation ID")
                }
            }
        }

        if(!isAccessServiceEnabled(this, Detector::class.java)) {

            service.setOnClickListener {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }

            usage.setOnClickListener {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivity(intent)
            }

        }

        else {
            val nav = Intent(this, ThankYouActivity::class.java)
            nav.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(nav)
        }
    }
}