package com.textaddict.app.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.textaddict.app.ui.fragment.FragmentInteractionCallback
import com.textaddict.app.utils.Constants.ACTION
import com.textaddict.app.utils.Constants.DATA_KEY_1
import com.textaddict.app.utils.Constants.DATA_KEY_2
import java.util.*

class FragmentUtils {

    companion object {

        private val TAG_SEPARATOR = ":"

        fun addInitialTabFragment(
            fragmentManager: FragmentManager,
            tagStacks: Map<String, Stack<String>>,
            tag: String,
            fragment: Fragment,
            layoutId: Int,
            shouldAddToStack: Boolean
        ) {
            fragmentManager.beginTransaction().add(
                layoutId,
                fragment,
                fragment.javaClass.name + TAG_SEPARATOR + fragment.hashCode()
            ).commit()
            if (shouldAddToStack)
                tagStacks.getValue(tag).push(fragment.javaClass.name + TAG_SEPARATOR + fragment.hashCode())
        }

        fun addAdditionalTabFragment(
            fragmentManager: FragmentManager,
            tagStacks: Map<String, Stack<String>>,
            tag: String,
            show: Fragment,
            hide: Fragment,
            layoutId: Int,
            shouldAddToStack: Boolean
        ) {
            fragmentManager
                .beginTransaction()
                .add(layoutId, show, show.javaClass.name + TAG_SEPARATOR + show.hashCode())
                .show(show)
                .hide(hide)
                .commit()
            if (shouldAddToStack) tagStacks.getValue(tag).push(show.javaClass.name + TAG_SEPARATOR + show.hashCode())
        }

        fun showHideTabFragment(
            fragmentManager: FragmentManager,
            show: Fragment,
            hide: Fragment
        ) {
            fragmentManager
                .beginTransaction()
                .hide(hide)
                .show(show)
                .commit()
        }

        fun addShowHideFragment(
            fragmentManager: FragmentManager,
            tagStacks: Map<String, Stack<String>>,
            tag: String,
            show: Fragment,
            hide: Fragment,
            layoutId: Int,
            shouldAddToStack: Boolean
        ) {
            fragmentManager
                .beginTransaction()
                .add(layoutId, show, show.javaClass.name + TAG_SEPARATOR + show.hashCode())
                .show(show)
                .hide(hide)
                .commit()
            if (shouldAddToStack) tagStacks.getValue(tag).push(show.javaClass.name + TAG_SEPARATOR + show.hashCode())
        }

        fun removeFragment(fragmentManager: FragmentManager, show: Fragment, remove: Fragment) {
            fragmentManager
                .beginTransaction()
                .remove(remove)
                .show(show)
                .commit()
        }

        fun sendActionToActivity(
            action: String,
            tab: String,
            shouldAdd: Boolean,
            fragmentInteractionCallback: FragmentInteractionCallback
        ) {
            val bundle = Bundle()
            bundle.putString(ACTION, action)
            bundle.putString(DATA_KEY_1, tab)
            bundle.putBoolean(DATA_KEY_2, shouldAdd)
            fragmentInteractionCallback.onFragmentInteractionCallback(bundle)
        }
    }
}