package com.textaddict.app.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.textaddict.app.R
import com.textaddict.app.ui.activity.MainActivity
import com.textaddict.app.utils.Constants
import com.textaddict.app.viewmodel.impl.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProfileFragment : BaseFragment(), View.OnClickListener {

    private var param1: Long? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getLong(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel.user.observe(this, Observer { value ->
            value.let {
                username_textview.text = value.username
            }
        })

        viewModel.countPages.observe(this, Observer { value ->
            value?.let {
                count_pages_textview.text = it.size.toString()
            }
        })

        viewModel.getUser(param1!!)

        view.findViewById<Button>(R.id.log_out_button).setOnClickListener(this)

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.log_out_button -> {
                onClickLogout()
            }
        }
    }

    private fun onClickLogout() {
        // todo delete articles and users
        (activity as MainActivity).logoutFromApp()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Long, param2: String, isRoot: Boolean) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putBoolean(Constants.EXTRA_IS_ROOT_FRAGMENT, isRoot)
                }
            }
    }
}
