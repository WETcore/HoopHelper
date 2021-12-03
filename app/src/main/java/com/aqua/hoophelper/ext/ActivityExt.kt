package com.aqua.hoophelper.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import app.appworks.school.publisher.factory.ViewModelFactory
import com.aqua.hoophelper.HoopApplication

/**
 * Created by Wayne Chen in Jul. 2019.
 *
 * Extension functions for Activity.
 */
fun Activity.getVmFactory(): ViewModelFactory {
    val repository = (applicationContext as HoopApplication).hoopRepository
    return ViewModelFactory(repository)
}