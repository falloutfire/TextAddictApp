package com.textaddict.app.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.ui.fragment.ArticleListFragment

class MainActivity : AppCompatActivity(), ArticleListFragment.OnListFragmentInteractionListener {

    private lateinit var navView: BottomNavigationView
    private lateinit var fragment: Fragment

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                fragment = ArticleListFragment.newInstance(1)
                loadFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorite -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_person -> {
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
        fragment = ArticleListFragment.newInstance(1)
        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_container, fragment, null).commit()
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
            .add(R.id.frame_container, fragment, null).commit()
    }

}
