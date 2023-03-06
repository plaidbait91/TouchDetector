package com.example.touchdetector

import android.accessibilityservice.AccessibilityService
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.random.Random


class Detector : AccessibilityService() {

    private lateinit var receiver: BroadcastReceiver
    lateinit var database: FirebaseDatabase
    private var notifClicked = false
    private var launcherClicked = false
    private lateinit var context: Context
    private lateinit var launcher: String
    private val PREF_STR = "preferences"
    private val KEY = "ID"
    private lateinit var id: String
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sharedPref = context.getSharedPreferences(PREF_STR, Context.MODE_PRIVATE)


        if(sharedPref.getInt("DBchosen", -1) == -1 && sharedPref.getString(KEY, "unknown device") == "unknown device") {
            val i = Random(System.nanoTime()).nextInt(2)
            database = if(i == 1) {
                with(sharedPref.edit()) {
                    putInt("DBchosen", 1)
                    commit()
                }
                FirebaseDatabase.getInstance(FirebaseApp.getInstance("deaddict-collect"))
            } else {
                with(sharedPref.edit()) {
                    putInt("DBchosen", 0)
                    commit()
                }
                FirebaseDatabase.getInstance(FirebaseApp.getInstance("deaddict-collect2"))
            }
        }

        else {
            val x = sharedPref.getInt("DBchosen", -1)
            database = if(x == 0 || x == -1) {
                FirebaseDatabase.getInstance(FirebaseApp.getInstance("deaddict-collect"))
            } else {
                FirebaseDatabase.getInstance(FirebaseApp.getInstance("deaddict-collect2"))
            }
        }

        database.setPersistenceEnabled(true)

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(Intent.ACTION_SCREEN_OFF)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let {
                    val child = if(it.action == Intent.ACTION_USER_PRESENT) "unlocks" else "locks"
                    database.getReference(id).child(child).push().setValue(Date().time)
                }
            }
        }

        registerReceiver(receiver, filter)

        if(sharedPref.getString(KEY, "unknown device") == "unknown device") {
            FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sharedPref = getSharedPreferences(PREF_STR, Context.MODE_PRIVATE)
                    id = task.result.toString()
                    init()

                } else {
                    Log.e("Installations", "Unable to get Installation ID")
                    id = "all404ids"
                    init()
                }
            }
        }

        else {
            id = sharedPref.getString(KEY, "unknown device")!!
            init()
        }

    }

    private fun init() {
        val shut = sharedPref.getBoolean("shutdown", false)

        if (!shut) {
            with(sharedPref.edit()) {
                putLong("start", System.currentTimeMillis())
                commit()
            }

            Log.d("ddd", "start: ${sharedPref.getLong("start", 0)}")
        } else {
            with(sharedPref.edit()) {
                putBoolean("shutdown", false)
                commit()
            }
        }

        val localPackageManager = packageManager
        val main = Intent("android.intent.action.MAIN")
        main.addCategory("android.intent.category.HOME")

        launcher = localPackageManager
            .resolveActivity(main, PackageManager.MATCH_DEFAULT_ONLY)!!.activityInfo.packageName

        with(sharedPref.edit()) {
            putString(KEY, id)
            commit()
        }
    }

    override fun onServiceConnected() {
        val intent = Intent(context, ThankYouActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        Log.d("Service", "Started")
        startActivity(intent)
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

        val myRef = database.getReference(id).child("events")
        var pkg = ""
        var event = ""
        var clicked = ""

        p0?.let {

            clicked = if(it.className.isNullOrEmpty()) "idk" else it.className as String
            pkg = if(it.packageName.isNullOrEmpty()) "unknown" else it.packageName as String
            when(it.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> event = "Clicked"
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> event = "Long clicked"
                AccessibilityEvent.TYPE_VIEW_SCROLLED -> event = "Scrolled"
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> event = "Typing"
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> event = "Change window"
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> event = "Notif received"
            }

        }

        Log.d("service", "Log if service still works: $pkg $event at $clicked")

        if(notifClicked && validateEvent(event, pkg) && pkg != "com.android.systemui" && event == "Change window") {
            Log.d("d", "detectyayy $pkg")
//            showToast(pkg)
            myRef.child(pkg.replace('.', ',')).child("Notif clicked").push().setValue(Date().time)
            notifClicked = false
        }

        if(launcherClicked && validateEvent(event, pkg) && pkg != launcher && event == "Change window") {
            Log.d("d", "detectyahoooo $pkg")
            myRef.child(pkg.replace('.', ',')).child("Launched").push().setValue(Date().time)
            launcherClicked = false
        }

        if(pkg == launcher && event == "Clicked") launcherClicked = true

        if(notifBarEvent(pkg, event, clicked)) notifClicked = true

        if(validateEvent(event, pkg)) myRef.child(pkg.replace('.', ',')).child(event).push().setValue(Date().time)
    }

    private fun notifBarEvent(
        pkg: String,
        event: String,
        clicked: String
    ) = pkg == "com.android.systemui" &&
                event == "Clicked" &&
            clicked == "android.widget.FrameLayout"

    private fun validateEvent(event: String, pkg: String) = event.isNotEmpty() && pkg.isNotEmpty()

    override fun onInterrupt() {

    }

    override fun onDestroy() {
        Log.d("okbro", "bye bye")
        unregisterReceiver(receiver)
    }
}