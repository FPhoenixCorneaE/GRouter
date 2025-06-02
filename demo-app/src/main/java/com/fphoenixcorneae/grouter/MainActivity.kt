package com.fphoenixcorneae.grouter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun jump2Second(view: View) {
        GRouter.with(view.context)
            .url("deeplink://grouter/second")
            .start()
    }

    fun jump2OrderDetail(view: View) {
        GRouter.with(view.context)
            .url("android://com.google.router/order/detail?channel=online")
            .extras(Intent().apply {
                putExtra("order_id", "12345678")
            })
            .start()
    }
}