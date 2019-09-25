package com.textaddict.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.textaddict.app.R
import com.textaddict.app.viewmodel.impl.AddActivityViewModel

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        url?.let {
            val viewModel = ViewModelProvider(this).get(AddActivityViewModel::class.java)
            val pref = getSharedPreferences(StartUpActivity.APP_PREFERENCES, MODE_PRIVATE)
            viewModel.addArticleInStorage(url, pref.getLong(StartUpActivity.APP_PREFERENCES_USER_ID, 0))

            val toast = Toast.makeText(
                applicationContext, "Article saved!", Toast.LENGTH_SHORT
            )
            toast.show()
        }

        finish()
    }
}
