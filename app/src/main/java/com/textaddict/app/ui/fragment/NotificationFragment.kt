package com.textaddict.app.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.textaddict.app.R
import com.textaddict.app.utils.Constants

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    companion object {
        fun newInstance(isRoot: Boolean): NotificationFragment {
            val args = Bundle()
            args.putBoolean(Constants.EXTRA_IS_ROOT_FRAGMENT, isRoot)
            val fragment = NotificationFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
