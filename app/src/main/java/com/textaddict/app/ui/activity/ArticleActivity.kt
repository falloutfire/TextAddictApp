package com.textaddict.app.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import com.google.android.material.appbar.AppBarLayout
import com.textaddict.app.R
import com.textaddict.app.ui.fragment.ArticleFragment
import kotlinx.android.synthetic.main.activity_article.*
import kotlin.math.abs


class ArticleActivity : AppCompatActivity(), ArticleFragment.OnFragmentInteractionListener {

    private lateinit var domain: String
    private var articleId: Long = 0

    private lateinit var shareActionProvider: ShareActionProvider
    private lateinit var actionBar: Toolbar

    private val mHideHandler = Handler()
    private val mHideRunnable = Runnable {

        val uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE and
                View.SYSTEM_UI_FLAG_FULLSCREEN.inv() or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        mRoot.systemUiVisibility = uiOptions
    }

    private val mShowRunnable = Runnable {
        mRoot.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        actionBar = findViewById(R.id.toolbar_article)
        setSupportActionBar(actionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        domain = intent.extras!!.get("DOMAIN") as String
        articleId = intent.extras!!.getLong("ID")

        if (savedInstanceState == null) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = ArticleFragment.newInstance(domain, articleId)
            transaction.replace(R.id.article_fragment, fragment)
            transaction.commit()
        }

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                abs(verticalOffset) == appBarLayout.totalScrollRange -> {
                    // Collapsed
                    mHideHandler.removeCallbacks(mShowRunnable)
                    mHideHandler.postDelayed(mHideRunnable, UI_ANIMATION_DELAY.toLong())
                }
                verticalOffset == 0 -> {
                    // Fully Expanded - show the status bar
                    mHideHandler.removeCallbacks(mHideRunnable)
                    mHideHandler.postDelayed(mShowRunnable, UI_ANIMATION_DELAY.toLong())
                }
                else -> {
                    // Somewhere in between
                    // We could optionally dim icons in this step by adding the flag:
                    // View.SYSTEM_UI_FLAG_LOW_PROFILE
                }
            }
        })

        var uiOptions = mRoot.systemUiVisibility
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        uiOptions = uiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        mRoot.systemUiVisibility = uiOptions
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.e("fragment", "frag")
    }

    /**
     * Метод добавляет элементы действий из файла ресурсов меню на панель приложения.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.article_menu_white, menu)
        val menuItem = menu?.findItem(R.id.action_share_article)
        shareActionProvider = MenuItemCompat.getActionProvider(menuItem) as ShareActionProvider
        setShareActionIntent(domain)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setShareActionIntent(text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        shareActionProvider.setShareIntent(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_archive_article -> {
                Log.e("archive", "article")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }

}
