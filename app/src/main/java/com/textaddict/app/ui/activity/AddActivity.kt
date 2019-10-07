package com.textaddict.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.textaddict.app.R
import com.textaddict.app.viewmodel.impl.AddActivityViewModel
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val url = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (url != null) {
            val viewModel = ViewModelProvider(this).get(AddActivityViewModel::class.java)
            val pref = getSharedPreferences(StartUpActivity.APP_PREFERENCES, MODE_PRIVATE)

            path_editText.setText(url)

            saveArticle_button.setOnClickListener {
                if (siteName_editText.text != null) {
                    viewModel.addArticleInStorage(
                        url,
                        siteName_editText.text.toString(),
                        pref.getLong(StartUpActivity.APP_PREFERENCES_USER_ID, 0)
                    )
                    val toast = Toast.makeText(
                        applicationContext, "Article saved!", Toast.LENGTH_SHORT
                    )
                    toast.show()
                    finish()
                } else {
                    val toast = Toast.makeText(
                        applicationContext, "Please, set article name.", Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }
        } else {
            val toast = Toast.makeText(
                applicationContext, "Uncorrect url", Toast.LENGTH_SHORT
            )
            toast.show()
            finish()
        }
    }
}
