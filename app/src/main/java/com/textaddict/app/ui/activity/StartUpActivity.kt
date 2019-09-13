package com.textaddict.app.ui.activity

import android.animation.Animator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.textaddict.app.R
import com.textaddict.app.ui.fragment.LoginFragment
import kotlinx.android.synthetic.main.activity_start_up.*

class StartUpActivity : AppCompatActivity() {

    val SPLASH_TIME_OUT = 4000L
    private var fragment: Fragment? = null
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        object : CountDownTimer(SPLASH_TIME_OUT, 1000) {
            override fun onFinish() {
                pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
                val intent = Intent(this@StartUpActivity, MainActivity::class.java)

                if (pref.contains(APP_PREFERENCES_USER_ID)) {
                    // TODO add login screen and save user in pref
                    intent.putExtra(USER_ID, pref.getLong(APP_PREFERENCES_USER_ID, 0))
                    startActivity(
                        intent
                    )
                    finish()
                } else {
                    appNameTextView.visibility = View.GONE
                    loadingProgressBar.visibility = View.GONE
                    start_up_layout.setBackgroundColor(
                        ContextCompat.getColor(
                            this@StartUpActivity,
                            R.color.colorPrimary
                        )
                    )
                    appIconImageView.setImageResource(R.mipmap.ic_launcher)

                    startAnimation()

                    fragment = LoginFragment.newInstance("", "")
                    if (savedInstanceState == null) {
                        supportFragmentManager.beginTransaction()
                            .add(R.id.frame_enter_container, fragment!!, null).commit()
                    }
                    intent.putExtra(USER_ID, pref.getInt(APP_PREFERENCES_USER_ID, 0))
                }
            }

            override fun onTick(p0: Long) {}
        }.start()

        var uiOptions = start_up_layout.systemUiVisibility
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        start_up_layout.systemUiVisibility = uiOptions

    }

    private fun startAnimation() {
        appIconImageView.animate().apply {
            x(50f)
            y(110f)
            duration = 1000
        }.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                frame_enter_container.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })
    }

    private fun startMainActivity(intent: Intent) {
        // TODO create and check db connection
        /*Handler().postDelayed(
            {
                startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
                finish()
            }, SPLASH_TIME_OUT
        )*/
    }

    companion object {
        var USER_ID = "USER_ID"

        // имя файла настроек
        val APP_PREFERENCES = "user_pref"
        val APP_PREFERENCES_USER_ID = "user_id"
    }
}
