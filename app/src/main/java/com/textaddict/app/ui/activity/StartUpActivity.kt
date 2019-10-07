package com.textaddict.app.ui.activity

import android.animation.Animator
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.textaddict.app.R
import com.textaddict.app.ui.fragment.ErrorDialogFragment
import com.textaddict.app.ui.fragment.LoginFragment
import com.textaddict.app.ui.fragment.SignUpFragment
import kotlinx.android.synthetic.main.activity_start_up.*

class StartUpActivity : AppCompatActivity() {

    private var fragment: Fragment? = null
    private var isAnim = false
    private lateinit var pref: SharedPreferences
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)

        progressDialog = ProgressDialog(this@StartUpActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Checking Account...")

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)

        if (!pref.contains(APP_PREFERENCES_USER_ID)) {
            fragment = LoginFragment.newInstance("", "")
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .add(
                        R.id.frame_enter_container,
                        fragment!!,
                        null
                    ).commitAllowingStateLoss()
            }
        }

        countDownTimer = object : CountDownTimer(SPLASH_TIME_OUT, 1000) {
            override fun onFinish() {
                isAnim = true
                if (pref.contains(APP_PREFERENCES_USER_ID)) {
                    startMainActivity()
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
                }
            }

            override fun onTick(p0: Long) {}
        }

        countDownTimer.start()

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
            override fun onAnimationRepeat(animator: Animator?) {
            }

            override fun onAnimationEnd(animator: Animator?) {
                frame_enter_container.visibility = View.VISIBLE
            }

            override fun onAnimationCancel(animator: Animator?) {
                frame_enter_container.visibility = View.VISIBLE
            }

            override fun onAnimationStart(animator: Animator?) {
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus && isAnim) {
            appIconImageView.x = 50f
            appIconImageView.y = 110f
        }
    }

    fun openSignUpFragment() {
        fragment = SignUpFragment.newInstance("", "")
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_enter_container, fragment!!, null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    fun openLoginFragment() {
        fragment = LoginFragment.newInstance("", "")
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_enter_container, fragment!!, null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    fun openProgressDialog() {
        progressDialog.create()
        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.cancel()
    }

    fun startMainActivity() {
        val mainIntent = Intent(this@StartUpActivity, MainActivity::class.java)
        mainIntent.putExtra(
            USER_ID,
            pref.getLong(APP_PREFERENCES_USER_ID, 0)
        )
        startActivity(mainIntent)
        finish()
    }

    fun openErrorFragment(message: String?) {
        val error = ErrorDialogFragment(message)
        error.show(supportFragmentManager, "ErrorDialogFragment")
    }

    companion object {
        val SPLASH_TIME_OUT = 3000L
        var USER_ID = "USER_ID"

        // имя файла настроек
        val APP_PREFERENCES = "user_pref"
        val APP_PREFERENCES_USER_ID = "user_id"

        val fragmentSignUp = "SIGN_UP_FRAGMENT"
        val fragmentLogin = "LOG_IN_FRAGMENT"
    }
}
