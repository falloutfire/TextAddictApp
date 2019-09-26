package com.textaddict.app.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.ui.activity.StartUpActivity.Companion.APP_PREFERENCES
import com.textaddict.app.ui.fragment.ArchiveFragment
import com.textaddict.app.ui.fragment.ArticleListFragment
import com.textaddict.app.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity(), ArticleListFragment.OnListFragmentInteractionListener,
    ArchiveFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        Log.e("fragment", "frag")
    }

    private lateinit var navView: BottomNavigationView
    private var fragmentArticles: Fragment? = null
    private var fragmentArchive: Fragment? = null
    private var fragmentProfile: Fragment? = null
    private var fragmentArticleList: Fragment? = null
    private var userId: Long = 1
    private lateinit var pref: SharedPreferences

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                if (fragmentArticles == null) {
                    fragmentArticles = ArticleListFragment.newInstance(2, userId)
                }
                loadFragment(fragmentArticles!!)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorite -> {
                if (fragmentArchive == null) {
                    fragmentArchive = ArchiveFragment.newInstance("", "")
                }
                loadFragment(fragmentArchive!!)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_person -> {
                if (fragmentProfile == null) {
                    fragmentProfile =
                        ProfileFragment.newInstance(pref.getLong(StartUpActivity.APP_PREFERENCES_USER_ID, 0), "")
                }
                loadFragment(fragmentProfile!!)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        userId = intent.getLongExtra(StartUpActivity.USER_ID, 1)
        navView.selectedItemId = 0
        fragmentArticles = ArticleListFragment.newInstance(1, userId)
        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_container, fragmentArticles!!, null).commit()
        }

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
    }

    override fun onListFragmentInteraction(item: Article?) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("DOMAIN", item?.fullPath)
        intent.putExtra("ID", item?.id)
        startActivity(intent)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment, null).addToBackStack(null).commit()
    }

    fun logoutFromApp() {
        pref.edit().remove(StartUpActivity.APP_PREFERENCES_USER_ID).apply()
        val intent = Intent(this, StartUpActivity::class.java)
        startActivity(intent)
        finish()
    }

}
