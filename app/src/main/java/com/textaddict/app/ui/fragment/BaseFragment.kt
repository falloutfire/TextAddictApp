package com.textaddict.app.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    protected var fragmentInteractionCallback: FragmentInteractionCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentInteractionCallback = context as FragmentInteractionCallback
        } catch (e: ClassCastException) {
            throw RuntimeException(context.toString() + " must implement " + FragmentInteractionCallback::class.java.name)
        }
    }

    override fun onDetach() {
        fragmentInteractionCallback = null
        super.onDetach()
    }

    companion object {
        var currentTab: String = ""
    }


}


interface FragmentInteractionCallback {
    fun onFragmentInteractionCallback(bundle: Bundle)
}