package com.aqua.hoophelper

import android.app.Application
import com.aqua.hoophelper.database.HoopRepository
import com.aqua.hoophelper.util.ServiceLocator
import kotlin.properties.Delegates

class HoopApplication : Application() {

    // Depends on the flavor,
    val hoopRepository: HoopRepository
        get() = ServiceLocator.provideRepository(this)

    companion object {
        var instance: HoopApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}