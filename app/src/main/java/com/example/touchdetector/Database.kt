package com.example.touchdetector

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random


class Database: Application() {

    override fun onCreate() {
        super.onCreate()
        var options = FirebaseOptions.Builder()
            .setProjectId("deaddict-collect")
            .setApplicationId("1:453822197018:android:962f607adaf47df67f10a3")
            .setApiKey("AIzaSyCtHMP8EPBoG-wgbojxwyQQeaQeOJHmKjA")
            .setDatabaseUrl("https://deaddict-collect-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .build()
        var app = FirebaseApp.initializeApp(this, options, "deaddict-collect")

        FirebaseDatabase.getInstance(app).setPersistenceEnabled(true)

        options = FirebaseOptions.Builder()
            .setProjectId("deaddict-collect2")
            .setApplicationId("1:133705878828:android:8d92b119e434373f8fb089")
            .setApiKey("AIzaSyAyy5J84RLVPVyea91ggTnzs9b-AKh8vxg")
            .setDatabaseUrl("https://deaddict-collect2-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .build()
        app = FirebaseApp.initializeApp(this, options, "deaddict-collect2")

        FirebaseDatabase.getInstance(app).setPersistenceEnabled(true)

    }
}