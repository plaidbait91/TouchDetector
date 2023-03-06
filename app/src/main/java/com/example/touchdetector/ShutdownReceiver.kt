package com.example.touchdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class ShutdownReceiver: BroadcastReceiver() {
    private val PREF_STR = "preferences"

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("ShutdownReceiver", "onReceive: ")
        val sharedPref = p0!!.getSharedPreferences(PREF_STR, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("shutdown", true)
            commit()
        }

    }
}