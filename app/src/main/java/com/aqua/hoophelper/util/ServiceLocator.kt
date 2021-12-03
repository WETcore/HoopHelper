package com.aqua.hoophelper.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.aqua.hoophelper.database.DefaultHoopRepository
import com.aqua.hoophelper.database.HoopDataSource
import com.aqua.hoophelper.database.HoopRepository
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource

object ServiceLocator {

    @Volatile
    var repository: HoopRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): HoopRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createPublisherRepository(context)
        }
    }

    private fun createPublisherRepository(context: Context): HoopRepository {
        return DefaultHoopRepository(
            HoopRemoteDataSource
        )
    }

}