package com.textaddict.app.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.ArticleType
import com.textaddict.app.ui.adapter.ArticleViewAdapter
import com.textaddict.app.ui.adapter.OnSwipeTouchListener
import com.textaddict.app.ui.adapter.TouchCallbackBuilder
import com.textaddict.app.utils.Constants
import com.textaddict.app.utils.dpToPx
import com.textaddict.app.utils.drawableToBitmap
import com.textaddict.app.viewmodel.impl.ListArticleViewModel


class ArticleListFragment : BaseFragment() {

    private var columnCount = 1
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

        setupRecyclerView(view)

        return view
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.list)
        // Set the adapter
        recyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        adapter = ArticleViewAdapter(context!!, listener, true)
        recyclerView.adapter = adapter

        val dLeft = resources.getDrawable(R.drawable.ic_delete_white_24dp).mutate()
        val dRight = resources.getDrawable(R.drawable.ic_archive_white_24dp).mutate()
        val iconSizeInDp = 24f

        val iconLeft = drawableToBitmap(dLeft)
        val iconRight = drawableToBitmap(dRight)

        val touchCallback = TouchCallbackBuilder<Article>(adapter)
            .iconSize(dpToPx(iconSizeInDp, context!!))
            .leftBackgroundColor(Color.parseColor("#D32F2F"))
            .leftIcon(iconLeft)
            .leftTextSnackBar("archive")
            .rightBackgroundColor(Color.parseColor("#388E3C"))
            .rightIcon(iconRight)
            .rightTextSnackBar("delete")
            .isMarginAppbar(true)
            .view(view.rootView)
            .onSwipeListener(object : OnSwipeTouchListener {
                override fun onSwipeUndo(vh: RecyclerView.ViewHolder) {
                    viewModel.unarchiveArticle(ArticleViewAdapter.actionArticle!!.id)
                }

                override fun onSwipeRight(vh: RecyclerView.ViewHolder) {
                    viewModel.deleteArticle(ArticleViewAdapter.actionArticle!!.id)
                    Log.e("archive", ArticleViewAdapter.actionArticle!!.id.toString())
                }

                override fun onSwipeLeft(vh: RecyclerView.ViewHolder) {
                    viewModel.archiveArticle(ArticleViewAdapter.actionArticle!!.id)

                }
            })
            .build()

        val helper = ItemTouchHelper(touchCallback)
        helper.attachToRecyclerView(recyclerView)
        recyclerView.itemAnimator = null
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
        subscribeUi(viewModel.articles)
        viewModel.archivedArticles.observe(this, Observer { value ->
            adapter.setArchiveCount(value.size)
        })
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
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    private fun subscribeUi(liveData: LiveData<List<Article>>) {
        // Update the list when the data changes
        liveData.observe(this, Observer { listArticle ->
            adapter.setList(listArticle)
        })


        /*liveData.observe(this, Observer<List<Article>> { listArticle ->
            listArticle?.let { adapter.submitList(it) }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            //mBinding.executePendingBindings()
        })*/
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_USER_ID = "user-userId"

        @JvmStatic
        fun newInstance(columnCount: Int, userId: Long, isRoot: Boolean) =
            ArticleListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putLong(ARG_USER_ID, userId)
                    putBoolean(Constants.EXTRA_IS_ROOT_FRAGMENT, isRoot)
                }
            }
    }
}

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 *
 */
interface OnListFragmentInteractionListener {
    fun onListFragmentInteraction(item: ArticleType?)
}
