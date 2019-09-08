package com.textaddict.app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.ui.adapter.ArticleViewAdapter
import com.textaddict.app.viewmodel.impl.ListArticleViewModel

class ArticleListFragment : Fragment() {

    private var columnCount = 1
    private var userId: Long = 1
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var viewModel: ListArticleViewModel
    private lateinit var adapter: ArticleViewAdapter
    private lateinit var spinner: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_list, container, false)
        viewModel = ViewModelProvider(this).get(ListArticleViewModel::class.java)
        spinner = view.findViewById(R.id.spinner)

        val recyclerView: RecyclerView = view.findViewById(R.id.list)
        // Set the adapter
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        adapter = ArticleViewAdapter(context!!, listener)
        recyclerView.adapter = adapter

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.spinner.observe(this, Observer { value ->
            value?.let { show ->
                Log.e("test", "spinndeer")
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        })

/*        viewModel.articles.observe(this, Observer { value ->
            value?.let { adapter.setArticles(it) }
        })*/

        subscribeUi(viewModel.articles)
        val fabButton: FloatingActionButton = view!!.findViewById(R.id.floatingActionButton)
        fabButton.setOnClickListener {
            viewModel.addData(userId)
            //viewModel.setData()
            subscribeUi(viewModel.articles)
            Log.e("test", "fab")
            Log.e("userId", viewModel.articles.value?.get(0)?.userId.toString())
        }
    }

    private fun subscribeUi(liveData: LiveData<List<Article>>) {
        // Update the list when the data changes
        liveData.observe(this, Observer<List<Article>> { listArticle ->
            listArticle?.let { adapter.setArticles(it) }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            //mBinding.executePendingBindings()
        })
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
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: Article?)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_USER_ID = "user-id"

        @JvmStatic
        fun newInstance(columnCount: Int, userId: Long) =
            ArticleListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putLong(ARG_USER_ID, userId)
                }
            }
    }
}
