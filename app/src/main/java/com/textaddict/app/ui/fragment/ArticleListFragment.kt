package com.textaddict.app.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.textaddict.app.R
import com.textaddict.app.database.entity.Article
import com.textaddict.app.ui.adapter.*
import com.textaddict.app.ui.drawableToBitmap
import com.textaddict.app.viewmodel.impl.ListArticleViewModel


class ArticleListFragment : Fragment() {

    private var columnCount = 1
    private var userId: Long = 1
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var viewModel: ListArticleViewModel
    private lateinit var adapter: ArticleViewAdapter
    private lateinit var spinner: ProgressBar
    private lateinit var swipeControllerCallback: ListAdapterItemTouchHelperCallback<Article>
    private lateinit var callback: ListItemTouchHelperCallback<Article>
    private lateinit var uiRecyclerTouchListener: UiRecyclerTouchCallback<Article>

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
        adapter = ArticleViewAdapter(context!!, listener)
        recyclerView.adapter = adapter

        val dLeft = resources.getDrawable(R.drawable.ic_archive_white_24dp).mutate()
        val dRight = resources.getDrawable(R.drawable.ic_delete_white_24dp).mutate()
        val iconSizeInDp = 24f

        val iconLeft = drawableToBitmap(dLeft)
        val iconRight = drawableToBitmap(dRight)

        val touchCallback = TouchCallbackBuilder<Article>()
            .iconSize(dpToPx(iconSizeInDp))
            .leftBackgroundColor(Color.parseColor("#388E3C"))
            .leftIcon(iconLeft)
            .leftTextSnackBar("archive")
            .rightBackgroundColor(Color.parseColor("#D32F2F"))
            .rightIcon(iconRight)
            .rightTextSnackBar("delete")
            .isMarginAppbar(true)
            .view(view.findViewById(R.id.placeSnackBar))
            .onSwipeListener(object : OnSwipeTouchListener {
                override fun onSwipeRight(vh: RecyclerView.ViewHolder) {
                    //adapter.removeItem(vh.adapterPosition)
                    //adapter.getItem(vh.adapterPosition).status = 0
                    ArticleViewAdapter.actionArticle = adapter.getItemFromList(vh.adapterPosition)
                    ArticleViewAdapter.actionPosition = vh.adapterPosition
                    //viewModel.deleteArticle(adapter.getItemFromList(vh.adapterPosition).id)
                    //adapter.submitList(viewModel.articles.value)
                    viewModel.deleteArticle(adapter.getItemFromList(vh.adapterPosition).id)
                    adapter.submitList(viewModel.articles.value)

                    //adapter.notifyItemChanged(vh.adapterPosition)
                }

                override fun onSwipeLeft(vh: RecyclerView.ViewHolder) {
                    //(adapter as ItemTouchHelperAdapter<Article>).archiveItem(vh.adapterPosition)
                    //adapter.removeItem(vh.adapterPosition)
                    //adapter.getItem(vh.adapterPosition).status = 2
                    ArticleViewAdapter.actionArticle = adapter.getItemFromList(vh.adapterPosition)
                    ArticleViewAdapter.actionPosition = vh.adapterPosition
                    viewModel.deleteArticle(adapter.getItemFromList(vh.adapterPosition).id)
                    //adapter.notifyItemChanged(vh.adapterPosition)
                    adapter.submitList(viewModel.articles.value)
                }

                override fun onSwipeUndo(vh: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        //adapter.restoreItem()
                        val article = Article(
                            ArticleViewAdapter.actionArticle!!.title,
                            ArticleViewAdapter.actionArticle!!.domain,
                            ArticleViewAdapter.actionArticle!!.fullPath,
                            ArticleViewAdapter.actionArticle!!.date,
                            ArticleViewAdapter.actionArticle!!.content,
                            ArticleViewAdapter.actionArticle!!.archieve,
                            ArticleViewAdapter.actionArticle!!.favorite,
                            ArticleViewAdapter.actionArticle!!.userId
                        )

                        /*adapter.notifyItemChanged(vh.adapterPosition)
                        adapter.notifyDataSetChanged()*/
                        adapter.restoreItem()
                        viewModel.restoreArticle(article)
                        /*adapter.submitList(null)
                        adapter.submitList(viewModel.articles.value)*/
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        //adapter.restoreItem()
                        val article = Article(
                            ArticleViewAdapter.actionArticle!!.title,
                            ArticleViewAdapter.actionArticle!!.domain,
                            ArticleViewAdapter.actionArticle!!.fullPath,
                            ArticleViewAdapter.actionArticle!!.date,
                            ArticleViewAdapter.actionArticle!!.content,
                            ArticleViewAdapter.actionArticle!!.archieve,
                            ArticleViewAdapter.actionArticle!!.favorite,
                            ArticleViewAdapter.actionArticle!!.userId
                        )


                        adapter.restoreItem()
                        viewModel.restoreArticle(article)
                        /*adapter.submitList(null)
                        adapter.submitList(viewModel.articles.value)*/
                    }
                }

            })
            .build()

        val helper = ItemTouchHelper(touchCallback)
        helper.attachToRecyclerView(recyclerView)

        // Add animation
        val animator = RecyclerItemAnimator(object : RecyclerItemAnimator.OnAnimationEndListener {
            override fun onChangeEnd(newHolder: RecyclerView.ViewHolder) {
                viewModel.deleteArticle(adapter.getItemFromList(newHolder.adapterPosition).id)
                //adapter.notifyItemChanged(newHolder.adapterPosition)
                //adapter.removeItem(newHolder.adapterPosition)
            }
        })
        //recyclerView.itemAnimator = animator
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
                Log.e("test", "spinndeer")
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        })


    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }


    private fun subscribeUi(liveData: LiveData<List<Article>>) {
        // Update the list when the data changes
        liveData.observe(this, Observer { listArticle ->
            listArticle?.let {
                Log.e("update", it.size.toString())
                adapter.submitList(it.toMutableList())
            }
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
