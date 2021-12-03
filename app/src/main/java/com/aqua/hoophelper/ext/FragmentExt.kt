package com.aqua.hoophelper.ext

import androidx.fragment.app.Fragment
import app.appworks.school.publisher.factory.ViewModelFactory
import com.aqua.hoophelper.HoopApplication

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as HoopApplication).hoopRepository
    return ViewModelFactory(repository)
}