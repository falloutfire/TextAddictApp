package com.textaddict.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.textaddict.app.R


class StartUpActivity : AppCompatActivity() {

    val SPLASH_TIME_OUT = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        Handler().postDelayed(
            {
                // This method will be executed once the timer is over
                // Start your app main activity
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)

                // close this activity
                finish()
            }, SPLASH_TIME_OUT
        )
    }
}
