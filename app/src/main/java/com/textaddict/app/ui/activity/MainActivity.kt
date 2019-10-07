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
import com.textaddict.app.ui.fragment.*
import com.textaddict.app.utils.Constants.ACTION
import com.textaddict.app.utils.Constants.EXTRA_IS_ROOT_FRAGMENT
import com.textaddict.app.utils.Constants.TAB_FAVORITE
import com.textaddict.app.utils.Constants.TAB_HOME
import com.textaddict.app.utils.Constants.TAB_NOTIFICATION
import com.textaddict.app.utils.Constants.TAB_PROFILE
import com.textaddict.app.utils.FragmentUtils.Companion.addInitialTabFragment
import com.textaddict.app.utils.FragmentUtils.Companion.removeFragment
import com.textaddict.app.utils.FragmentUtils.Companion.showHideTabFragment
import com.textaddict.app.utils.StackListManager.Companion.updateStackIndex
import com.textaddict.app.utils.StackListManager.Companion.updateStackToIndexFirst
import com.textaddict.app.utils.StackListManager.Companion.updateTabStackIndex
import java.util.*

class MainActivity : AppCompatActivity(), ArticleListFragment.OnListFragmentInteractionListener,
    ArchiveFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener,
    FragmentInteractionCallback {

    override fun onFragmentInteractionCallback(bundle: Bundle) {
        val action = bundle.getString(ACTION)

        action?.let {
            when (action) {

            }
        }
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.e("fragment", "frag")
    }

    private lateinit var navView: BottomNavigationView
    private var tagStacks: LinkedHashMap<String, Stack<String>>? = null
    private var currentTab: String? = null
    private var stackList: ArrayList<String>? = null
    private var menuStacks: ArrayList<String>? = null
    private var fragmentHome: Fragment? = null
    private var fragmentFavorite: Fragment? = null
    private var fragmentNotification: Fragment? = null
    private var fragmentProfile: Fragment? = null
    private var currentFragment: Fragment? = null
    private var userId: Long = 1
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
        createStacks()

        userId = intent.getLongExtra(StartUpActivity.USER_ID, 1)
        navView.selectedItemId = 0
    }

    override fun onBackPressed() {
        resolveBackPressed()
    }

    override fun onListFragmentInteraction(item: Article?) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("DOMAIN", item?.fullPath)
        intent.putExtra("ID", item?.id)
        startActivity(intent)
    }

    fun logoutFromApp() {
        pref.edit().remove(StartUpActivity.APP_PREFERENCES_USER_ID).apply()
        val intent = Intent(this, StartUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createStacks() {
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        fragmentHome = ArticleListFragment.newInstance(2, userId)
        fragmentFavorite = FavoriteFragment()
        fragmentNotification = NotificationFragment()
        fragmentProfile =
            ProfileFragment.newInstance(
                pref.getLong(
                    StartUpActivity.APP_PREFERENCES_USER_ID,
                    0
                ), ""
            )

        tagStacks = LinkedHashMap()
        tagStacks!![TAB_HOME] = Stack()
        tagStacks!![TAB_PROFILE] = Stack()
        tagStacks!![TAB_FAVORITE] = Stack()
        tagStacks!![TAB_NOTIFICATION] = Stack()

        menuStacks = ArrayList()
        menuStacks!!.add(TAB_HOME)

        stackList = ArrayList()
        stackList!!.add(TAB_HOME)
        stackList!!.add(TAB_PROFILE)
        stackList!!.add(TAB_FAVORITE)
        stackList!!.add(TAB_NOTIFICATION)

        navView.selectedItemId = R.id.navigation_home
        navView.setOnNavigationItemReselectedListener(onNavigationItemReselectedListener)
    }

    private val onNavigationItemReselectedListener =
        BottomNavigationView.OnNavigationItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> popStackExceptFirst()
                R.id.navigation_favorite -> popStackExceptFirst()
                R.id.navigation_person -> popStackExceptFirst()
                R.id.navigation_notifications -> popStackExceptFirst()
            }
        }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    selectedTab(TAB_HOME)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorite -> {
                    selectedTab(TAB_FAVORITE)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    selectedTab(TAB_NOTIFICATION)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_person -> {
                    selectedTab(TAB_PROFILE)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun selectedTab(tabId: String) {
        currentTab = tabId
        BaseFragment.currentTab = currentTab as String

        if (tagStacks!![tabId]!!.size == 0) {
            /*
              First time this tab is selected. So add first fragment of that tab.
              We are adding a new fragment which is not present in stack. So add to stack is true.
             */
            when (tabId) {
                TAB_HOME -> {
                    addInitialTabFragment(
                        supportFragmentManager,
                        tagStacks!!,
                        TAB_HOME,
                        fragmentHome!!,
                        R.id.frame_container,
                        true
                    )
                    resolveStackLists(tabId)
                    assignCurrentFragment(fragmentHome!!)
                }
                TAB_PROFILE -> {
                    addInitialTabFragment(
                        supportFragmentManager,
                        tagStacks!!,
                        TAB_PROFILE,
                        fragmentProfile!!,
                        R.id.frame_container,
                        true
                    )
                    resolveStackLists(tabId)
                    assignCurrentFragment(fragmentProfile!!)
                }
                TAB_FAVORITE -> {
                    addInitialTabFragment(
                        supportFragmentManager,
                        tagStacks!!,
                        TAB_FAVORITE,
                        fragmentFavorite!!,
                        R.id.frame_container,
                        true
                    )
                    resolveStackLists(tabId)
                    assignCurrentFragment(fragmentFavorite!!)
                }
                TAB_NOTIFICATION -> {
                    addInitialTabFragment(
                        supportFragmentManager,
                        tagStacks!!,
                        TAB_NOTIFICATION,
                        fragmentNotification!!,
                        R.id.frame_container,
                        true
                    )
                    resolveStackLists(tabId)
                    assignCurrentFragment(fragmentNotification!!)
                }
            }
        } else {
            /*
             * We are switching tabs, and target tab already has at least one fragment.
             * Show the target fragment
             */
            val targetFragment =
                supportFragmentManager.findFragmentByTag(tagStacks!![tabId]!!.lastElement())
            showHideTabFragment(supportFragmentManager, targetFragment!!, currentFragment!!)
            resolveStackLists(tabId)
            assignCurrentFragment(targetFragment)
        }
    }

    private fun popFragment() {
        /*
         * Select the second last fragment in current tab's stack,
         * which will be shown after the fragment transaction given below
         */
        val fragmentTag = tagStacks!![currentTab]!!.elementAt(tagStacks!![currentTab]!!.size - 2)
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        /*pop current fragment from stack */
        tagStacks!![currentTab]!!.pop()

        removeFragment(supportFragmentManager, fragment!!, currentFragment!!)

        assignCurrentFragment(fragment)
    }

    private fun resolveBackPressed() {
        var stackValue = 0
        if (tagStacks!![currentTab]!!.size == 1) {
            val value = tagStacks!![stackList!![1]]
            if (value!!.size > 1) {
                stackValue = value.size
                popAndNavigateToPreviousMenu()
            }
            if (stackValue <= 1) {
                if (menuStacks!!.size > 1) {
                    navigateToPreviousMenu()
                } else {
                    finish()
                }
            }
        } else {
            popFragment()
        }
    }

    /*Pops the last fragment inside particular tab and goes to the second tab in the stack*/
    private fun popAndNavigateToPreviousMenu() {
        val tempCurrent = stackList!![0]
        currentTab = stackList!![1]
        BaseFragment.currentTab = currentTab as String
        navView.selectedItemId = resolveTabPositions(currentTab!!)
        val targetFragment =
            supportFragmentManager.findFragmentByTag(tagStacks!![currentTab!!]!!.lastElement())
        showHideTabFragment(supportFragmentManager, targetFragment!!, currentFragment!!)
        assignCurrentFragment(targetFragment)
        updateStackToIndexFirst(stackList!!, tempCurrent)
        menuStacks!!.removeAt(0)
    }

    private fun navigateToPreviousMenu() {
        menuStacks!!.removeAt(0)
        currentTab = menuStacks!![0]
        BaseFragment.currentTab = currentTab as String
        navView.selectedItemId = resolveTabPositions(currentTab!!)
        val targetFragment =
            supportFragmentManager.findFragmentByTag(tagStacks!![currentTab!!]!!.lastElement())
        showHideTabFragment(supportFragmentManager, targetFragment!!, currentFragment!!)
        assignCurrentFragment(targetFragment)
    }

    private fun popStackExceptFirst() {
        if (tagStacks!![currentTab]!!.size == 1) {
            return
        }
        while (!tagStacks!![currentTab]!!.empty() && !supportFragmentManager.findFragmentByTag(
                tagStacks!![currentTab]!!.peek()
            )!!.arguments!!.getBoolean(
                EXTRA_IS_ROOT_FRAGMENT
            )
        ) {
            supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentByTag(tagStacks!![currentTab]!!.peek())!!)
            tagStacks!![currentTab]!!.pop()
        }
        val fragment =
            supportFragmentManager.findFragmentByTag(tagStacks!![currentTab]!!.elementAt(0))
        removeFragment(supportFragmentManager, fragment!!, currentFragment!!)
        assignCurrentFragment(fragment)
    }

    /*
     * Add a fragment to the stack of a particular tab
     */
    /*private fun showFragment(bundle: Bundle, fragmentToAdd: Fragment) {
        val tab = bundle.getString(DATA_KEY_1)
        val shouldAdd = bundle.getBoolean(DATA_KEY_2)
        addShowHideFragment(
            supportFragmentManager,
            tagStacks!!,
            tab!!,
            fragmentToAdd,
            getCurrentFragmentFromShownStack()!!,
            R.id.frame_container,
            shouldAdd
        )
        assignCurrentFragment(fragmentToAdd)
    }*/

    private fun resolveTabPositions(currentTab: String): Int {
        var tabIndex = 0
        when (currentTab) {
            TAB_HOME -> tabIndex = R.id.navigation_home
            TAB_PROFILE -> tabIndex = R.id.navigation_person
            TAB_FAVORITE -> tabIndex = R.id.navigation_favorite
            TAB_NOTIFICATION -> tabIndex = R.id.navigation_notifications
        }
        return tabIndex
    }

    private fun resolveStackLists(tabId: String) {
        updateStackIndex(stackList!!, tabId)
        updateTabStackIndex(menuStacks!!, tabId)
    }

    /*private fun getCurrentFragmentFromShownStack(): Fragment? {
        return supportFragmentManager.findFragmentByTag(
            tagStacks!![currentTab]!!.elementAt(
                tagStacks!![currentTab]!!.size - 1
            )
        )
    }*/

    private fun assignCurrentFragment(current: Fragment) {
        currentFragment = current
    }


}
