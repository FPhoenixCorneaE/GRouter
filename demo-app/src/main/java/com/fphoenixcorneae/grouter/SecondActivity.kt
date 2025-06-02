package com.fphoenixcorneae.grouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.grouter.annotation.Router

@Router(scheme = "deeplink", host = "grouter", path = "/second", description = "SecondActivity")
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }
}