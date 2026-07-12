package ru.superstar.rocketalert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Device booted, background receiver registered")
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
