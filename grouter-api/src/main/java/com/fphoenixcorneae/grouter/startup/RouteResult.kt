package com.fphoenixcorneae.grouter.startup

import android.app.Activity
import android.content.Intent

data class RouteResult(
    val resultCode: Int,
    val data: Intent?
) {

    fun isOK(): Boolean {
        return resultCode == Activity.RESULT_OK
    }
}
