package com.textaddict.app.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
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

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                if (fragmentArticles == null) {
                    fragmentArticles = ArticleListFragment.newInstance(1)
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
                    fragmentProfile = ProfileFragment.newInstance("", "")
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

        navView.selectedItemId = 0
        fragmentArticles = ArticleListFragment.newInstance(1)
        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_container, fragmentArticles!!, null).addToBackStack(null).commit()
        }
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

}
