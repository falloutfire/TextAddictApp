package com.textaddict.app

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.NetworkType.UNMETERED
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via [WorkManager]
 */
class TextAddictApplication : Application() {

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */

    override fun onCreate() {
        super.onCreate()
        setupWorkManagerJob()
    }

    /**
     * Настройте фоновое задание WorkManager, чтобы ежедневно получать новые сетевые данные.
     */
    private fun setupWorkManagerJob() {
        // Используем ограничения, чтобы работа выполнялась только тогда,
        // когда устройство заряжается, а сеть не измерена

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(UNMETERED)
            .build()

        // Specify that the work should attempt to run every day
        val work = PeriodicWorkRequestBuilder<RefreshMainDataWork>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // Enqueue it work WorkManager, keeping any previously scheduled jobs for the same
        // work.
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(RefreshMainDataWork::class.java.name, KEEP, work)
    }

    /*fun getRepository(): AppDatabase {
        return getDatabase(applicationContext)
    }*/
}