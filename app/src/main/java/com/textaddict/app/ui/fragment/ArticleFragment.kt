package com.textaddict.app.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.textaddict.app.R
import com.textaddict.app.viewmodel.impl.ArticleViewModel

private const val ARG_PARAM1 = "DOMAIN"
private const val ARG_PARAM2 = "ARTICLE_ID"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ArticleFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ArticleFragment : Fragment() {
    private var domain: String? = null
    private var articleId: Long? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var viewModel: ArticleViewModel
    private lateinit var content_view: TextView
    private val mimeType = "text/html"
    private val encoding = "UTF-8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            domain = it.getString(ARG_PARAM1)
            articleId = it.getLong(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article, container, false)

        //content_view = view.findViewById(R.id.fullscreen_content_web_view)
        //content_view.setOnClickListener { toggle() }

        val webView: WebView = view.findViewById(R.id.fullscreen_content_web_view)
        viewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)

        // update the title when the [ListArticleViewModel.title] changes
        viewModel.title.observe(this, Observer { value ->
            value.let {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    content_view.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    content_view.text = Html.fromHtml(it)
                }*/
                webView.loadDataWithBaseURL("", it, mimeType, encoding, "")
            }
        })

        viewModel.getPage(articleId, domain)

        return view
    }


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param domain Parameter 1.
         * @return A new instance of fragment ArticleFragment.
         */
        @JvmStatic
        fun newInstance(domain: String, articleId: Long) =
            ArticleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, domain)
                    putLong(ARG_PARAM2, articleId)
                }
            }


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
