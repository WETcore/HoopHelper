package com.aqua.hoophelper.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RestartBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, HoopService::class.java))
    }
}