package com.textaddict.app.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.textaddict.app.R
import kotlinx.android.synthetic.main.activity_start_up.*


class StartUpActivity : AppCompatActivity() {

    val SPLASH_TIME_OUT = 4000L
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        var uiOptions = start_up_layout.systemUiVisibility
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        start_up_layout.systemUiVisibility = uiOptions

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        val intent = Intent(this, MainActivity::class.java)

        if (pref.contains(APP_PREFERENCES_USER_ID)) {
            // TODO add login screen and save user in pref
            intent.putExtra(USER_ID, pref.getInt(APP_PREFERENCES_USER_ID, 0))
        } else {
            val editor = pref.edit()
            editor.putInt(APP_PREFERENCES_USER_ID, 1)
            editor.apply()
            intent.putExtra(USER_ID, pref.getInt(APP_PREFERENCES_USER_ID, 0))
        }

        // TODO create and check db connection
        Handler().postDelayed(
            {
                startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
                finish()
            }, SPLASH_TIME_OUT
        )
    }

    companion object {
        var USER_ID = "USER_ID"

        // имя файла настроек
        val APP_PREFERENCES = "user_pref"
        val APP_PREFERENCES_USER_ID = "user_id"
    }
}
