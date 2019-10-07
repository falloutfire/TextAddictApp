package com.textaddict.app.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.textaddict.app.network.ResultLogin
import com.textaddict.app.ui.activity.MainActivity
import com.textaddict.app.ui.activity.StartUpActivity
import com.textaddict.app.viewmodel.impl.LoginViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpFragment : Fragment(), View.OnClickListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: LoginViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var signUpButton: Button

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
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        signUpButton = view.findViewById<Button>(R.id.signUp_button)
        val textView = view.findViewById<TextView>(R.id.loginBack_button)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        pref = activity!!.getSharedPreferences(
            StartUpActivity.APP_PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )

        viewModel.spinner.observe(this, Observer { value ->
            value.let {
                if (value) {
                    signUpButton.isEnabled = false
                    (activity as StartUpActivity).openProgressDialog()
                } else {
                    (activity as StartUpActivity).hideProgressDialog()
                    signUpButton.isEnabled = true
                }
            }
        })

        viewModel.resultSignUp.observe(this, Observer { value ->
            value.let {
                if (value == ResultLogin.Success) {
                    Log.e("login", "complete")
                    viewModel.spinner.removeObservers(this)
                    //(activity as StartUpActivity).startMainActivity()
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.putExtra(
                        StartUpActivity.USER_ID,
                        pref.getLong(StartUpActivity.APP_PREFERENCES_USER_ID, 0)
                    )
                    startActivity(intent)
                    activity?.finish()
                } else {
                    (activity as StartUpActivity).openErrorFragment((value as ResultLogin.Error).message)
                    Log.e("login", "invalid")
                }
            }
        })

        if (savedInstanceState != null) {
            username_editText.text = savedInstanceState.getString(username) as Editable
            password_editText.text = savedInstanceState.getString(password) as Editable
            email_editText.text = savedInstanceState.getString(email) as Editable
        }

        signUpButton.setOnClickListener(this)
        textView.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.signUp_button -> {
                onClickSignUp()
            }
            R.id.loginBack_button -> {
                onClickBackLogin()
            }
        }
    }

    private fun onClickBackLogin() {
        (activity as StartUpActivity).openLoginFragment()
    }

    private fun onClickSignUp() {
        viewModel.signUpUserInServer(
            username_editText.text.toString(),
            password_editText.text.toString(),
            email_editText.text.toString(),
            pref
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(username, username_editText.text.toString())
        outState.putString(password, password_editText.text.toString())
        outState.putString(email, email_editText.text.toString())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val username = "USERNAME"
        const val password = "PASSWORD"
        const val email = "EMAIL"
    }
}
