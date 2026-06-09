package com.dailytask.manager

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class DailyTaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Back-port java.time for API < 26 (safe no-op on API 26+)
        AndroidThreeTen.init(this)
    }
}
