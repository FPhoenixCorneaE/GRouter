package com.fphoenixcorneae.grouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.grouter.annotation.Router
import com.fphoenixcorneae.grouter.lib.R

@Router(path = "/login", description = "登录")
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}