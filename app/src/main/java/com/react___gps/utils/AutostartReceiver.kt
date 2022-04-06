package com.react___gps.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.react___gps.app.presentation.activity.MainActivity

class AutostartReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent){
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }
}