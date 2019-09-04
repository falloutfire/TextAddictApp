package com.textaddict.app.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.textaddict.app.R
import kotlinx.android.synthetic.main.activity_start_up.*


class StartUpActivity : AppCompatActivity() {

    val SPLASH_TIME_OUT = 4000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        var uiOptions = start_up_layout.systemUiVisibility
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        start_up_layout.systemUiVisibility = uiOptions

        Handler().postDelayed(
            {
                // This method will be executed once the timer is over
                // Start your app main activity
                val i = Intent(this, MainActivity::class.java)
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

                // close this activity
                finish()
            }, SPLASH_TIME_OUT
        )
    }
}
