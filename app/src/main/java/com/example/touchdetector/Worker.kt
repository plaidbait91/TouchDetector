package com.example.touchdetector

import android.Manifest
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class Worker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val PREF_STR = "preferences"
    private val KEY = "ID"

    override suspend fun doWork(): Result {
        val database = Firebase.database
        val rn = System.currentTimeMillis()
        val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, rn - 24 * 60 * 60 * 1000, rn)
        val sharedPref = context.getSharedPreferences(PREF_STR, Context.MODE_PRIVATE)
        val id = sharedPref.getString(KEY, "unknown device").toString()

        val requiredPermission = Manifest.permission.PACKAGE_USAGE_STATS
        val checkVal: Int = context.checkCallingOrSelfPermission(requiredPermission)

        if(checkVal != PackageManager.PERMISSION_GRANTED) {
            Log.d("usageTime", "permission denied")
            return Result.failure()
        }

        withContext(Dispatchers.IO) {
            for(entry in stats) {
                val ref = database.getReference(id)
                    .child("events")
                    .child(entry.packageName.replace('.', ','))

                var exist = 0L
                ref.child("usageTime").get().addOnSuccessListener {
                    if(it.exists()) {
                        exist = it.value as Long
                    }

                    else {
                        Log.d("usageTime", "not exist")
                    }
                }

                exist += entry.totalTimeInForeground

                val update = mapOf(
                    "/usageTime" to exist
                )

                ref.updateChildren(update).addOnSuccessListener {
                    Log.d("usageTime", "success")
                }.addOnFailureListener {
                    Log.d("usageTime", "fail")
                }

            }

        }

        Log.d("usageTime", "logggg")
        return Result.success()
    }
}