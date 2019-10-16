package com.textaddict.app.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.*
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.ArticleType
import com.textaddict.app.ui.adapter.*
import com.textaddict.app.utils.Constants
import com.textaddict.app.utils.dpToPx
import com.textaddict.app.utils.drawableToBitmap
import com.textaddict.app.viewmodel.impl.ListArticleViewModel


class ArticleListFragment : BaseFragment(), OnActionItemClickListener {

    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var viewModel: ListArticleViewModel
    private lateinit var adapter: ArticleViewAdapter
    private lateinit var spinner: ProgressBar
    private var actionMode: ActionMode? = null
    private lateinit var actionModeController: ActionModeController
    private lateinit var tracker: SelectionTracker<ArticleType>

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
        adapter = ArticleViewAdapter(recyclerView, listener, true)
        recyclerView.adapter = adapter

        val dLeft = resources.getDrawable(R.drawable.ic_delete_white_24dp).mutate()
        val dRight = resources.getDrawable(R.drawable.ic_archive_white_24dp).mutate()
        val iconSizeInDp = 24f

        val iconLeft = drawableToBitmap(dLeft)
        val iconRight = drawableToBitmap(dRight)

        val touchCallback = TouchCallbackBuilder<Article>(adapter)
            .backgroundColor(resources.getColor(R.color.colorBackground))
            .iconSize(dpToPx(iconSizeInDp, context!!))
            .leftBackgroundColor(resources.getColor(R.color.colorLightBlue))
            .leftIcon(iconLeft)
            .leftTextSnackBar("Archive")
            .rightBackgroundColor(resources.getColor(R.color.colorLightBlue))
            .rightIcon(iconRight)
            .rightTextSnackBar("Delete")
            .isMarginAppbar(false)
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

        tracker = SelectionTracker.Builder<ArticleType>(
            // индетифицируем трекер в констексе
            "article-selection-id",
            recyclerView,
            // для Long ItemKeyProvider реализован в виде StableIdKeyProvider
            ArticleKeyProvider(adapter.list),
            ArticleLookup(recyclerView),
            // существуют аналогичные реализации для Long и String
            StorageStrategy.createParcelableStorage(ArticleType::class.java)
        ).build()

        adapter.selectionTracker = tracker

        actionModeController = ActionModeController(tracker, touchCallback).also {
            it.createActionMode(R.menu.action_menu)
            it.onActionItemClickListener = this
        }

        tracker.addObserver(object : SelectionTracker.SelectionObserver<Any>() {
            override fun onItemStateChanged(key: Any, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                if (tracker.hasSelection() && actionMode == null) {
                    actionMode = (activity as AppCompatActivity).startSupportActionMode(
                        actionModeController
                    )
                    setSelectedTitle(tracker.selection.size())
                } else if (!tracker.hasSelection()) {
                    actionMode?.finish()
                    actionMode = null
                } else {
                    setSelectedTitle(tracker.selection.size())
                }
            }
        })

        viewModel.archivedArticles.observe(this, Observer { value ->
            adapter.setArchiveCount(value.size)
        })
        subscribeUi(viewModel.articles)
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

    override fun onActionItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.action_delete -> {
                val selection = adapter.selectionTracker!!.selection.toCollection(arrayListOf())
                val confirm = ConfirmDialogFragment(
                    resources.getString(R.string.confirm_delete),
                    object : OnInteractionDialog {
                        override fun positiveInteraction() {
                            for (i in selection) {
                                val index = adapter.list.indexOf(i)
                                adapter.list.remove(i)
                                adapter.notifyItemRemoved(index)
                                viewModel.deleteArticle((i as Article).id)
                            }
                        }

                        override fun negativeInteraction() {
                            actionModeController.closeActionMode()
                        }

                    })
                confirm.show(activity!!.supportFragmentManager, "ConfirmDialogFragment")
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setSelectedTitle(selected: Int) {
        actionMode?.title = "Selected: $selected"
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
