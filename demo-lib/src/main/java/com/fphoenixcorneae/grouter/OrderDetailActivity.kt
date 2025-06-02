package com.fphoenixcorneae.grouter

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.grouter.annotation.Router
import com.fphoenixcorneae.grouter.lib.R

@Router(path = "/order/detail", description = "订单详情")
class OrderDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        findViewById<TextView>(R.id.tvArgument).text = buildString {
            append("order_id:")
            append(intent.getStringExtra("order_id"))
            append("\n")
            append("channel:")
            append(intent.getStringExtra("channel"))
        }
    }
}