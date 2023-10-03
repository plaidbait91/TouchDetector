package com.example.touchdetector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class IdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id)

        val id = findViewById<TextView>(R.id.id)
        val sharedPref = getSharedPreferences("preferences", MODE_PRIVATE)

        val paidID = arrayListOf("126", "153", "169", "176", "177", "189", "e074LK27ROWvSkhuhYDsQG", "fMUzGM-hTKmCnQJECqwGt-",
        "e-kV1kAES8e62JuQv1oS3w", "evyLGHuCR0C6RYWNG70JAR", "eW3YCScMRUSABdMwvf3aqf", "dp1zErk6QAK7tVl2FwMpKj", "eR8YD1lZTrywe2pJM4pqTY")

        val idString = sharedPref.getString("ID", "unknown")

        if(paidID.indexOf(idString) != -1) {
            id.text = "You're eligible for the voucher!"
        }
        else {
            id.text = "You're not eligible, sorry."
        }
    }
}