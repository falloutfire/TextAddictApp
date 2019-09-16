package com.textaddict.app.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.textaddict.app.R
import com.textaddict.app.ui.activity.MainActivity
import com.textaddict.app.ui.activity.StartUpActivity
import com.textaddict.app.viewmodel.impl.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*

private const val ARG_PARAM1 = "username"
private const val ARG_PARAM2 = "password"

class LoginFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: LoginViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var loginButton: Button
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        loginButton = view.findViewById(R.id.login_button)
        progressDialog = ProgressDialog(
            activity
        )

        if (savedInstanceState != null) {
            username_editText.text = savedInstanceState.getString(username) as Editable
            password_editText.text = savedInstanceState.getString(password) as Editable
        }

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        pref = activity!!.getSharedPreferences(StartUpActivity.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        viewModel.spinner.observe(this, Observer { value ->
            value.let {
                if (value) {
                    login_button.isEnabled = false
                    progressDialog.isIndeterminate = true
                    progressDialog.setMessage("Checking Account...")
                    progressDialog.show()
                } else {
                    Handler().postDelayed({
                        progressDialog.hide()
                        login_button.isEnabled = true
                    }, 1500)
                }
            }
        })

        viewModel.login.observe(this, Observer { value ->
            value.let {
                if (value) {
                    Log.e("login", "complete")
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra(
                        StartUpActivity.USER_ID,
                        pref.getLong(StartUpActivity.APP_PREFERENCES_USER_ID, 0)
                    )
                    startActivity(intent)
                } else {
                    Log.e("login", "invalid")
                }
            }
        })

        view.findViewById<Button>(R.id.login_button).setOnClickListener(this)
        view.findViewById<TextView>(R.id.signUpButton).setOnClickListener(this)
        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.login_button -> {
                onClickLogin()
            }
            R.id.signUpButton -> {
                onClickSignUp()
            }
        }
    }

    private fun onClickSignUp() {
        (activity as StartUpActivity).openSignUpFragment()
    }

    private fun onClickLogin() {
        viewModel.checkUser(username_editText.text.toString(), password_editText.text.toString(), pref)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(username, username_editText.text.toString())
        outState.putString(password, password_editText.text.toString())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val username = "USERNAME"
        const val password = "PASSWORD"
    }
}
