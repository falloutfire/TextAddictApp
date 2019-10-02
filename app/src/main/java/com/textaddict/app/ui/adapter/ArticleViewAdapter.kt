package com.textaddict.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.textaddict.app.database.entity.Article
import com.textaddict.app.ui.fragment.ArticleListFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_article_item.view.*
import java.text.SimpleDateFormat


/**
 * [RecyclerView.Adapter] that can display a [articles] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class ArticleViewAdapter internal constructor(
    context: Context,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ArticleViewAdapter.ViewHolder>(),
    ItemTouchHelperAdapter<Article> {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mOnClickListener: View.OnClickListener
    private val list: ArrayList<Article> = arrayListOf()

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Article
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.mTitleView.text = item.title
        holder.mDomainView.text = item.domain

        val df = SimpleDateFormat.getDateInstance()
        val now = df.format(item.date)

        holder.mDateView.text = now

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.textaddict.app.R.layout.fragment_article_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(articles: List<Article>) {
        val diffCallback = ArticleListDiffUtilCallback(this.list, articles)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        list.clear()
        list.addAll(articles)
        //notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun removeItem(position: Int) {
        actionArticle = list[position]
        actionPosition = position

        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    override fun restoreItem() {
        /*val diffCallback = ArticleListDiffUtilCallback(this.articles, viewModel.articles.value!!)
        val diffResult = DiffUtil.calculateDiff(diffCallback)*/

        //viewModel.restoreArticle(actionArticle!!)
        this.list.add(actionPosition!!, actionArticle!!)
        notifyItemInserted(actionPosition!!)
        notifyItemRangeChanged(actionPosition!!, list.size)

        //diffResult.dispatchUpdatesTo(this)
    }

    override fun archiveItem(position: Int) {

    }

    fun getItemFromList(position: Int): Article {
        return list[position]
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mBackground: FrameLayout = mView.item_back
        val mItemLayout: CardView? = mView.card_article_item
        val mTitleView: TextView = mView.item_article_title
        val mDomainView: TextView = mView.item_article_domain
        val mDateView: TextView = mView.item_article_date

        override fun toString(): String {
            return super.toString() + " '" + mDomainView.text + "'"
        }
    }


    companion object {
        var actionArticle: Article? = null
        var actionPosition: Int? = null

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.fullPath == newItem.fullPath
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                val (title1, _, fullPath1) = oldItem
                val (title2, _, fullPath2) = newItem

                return title1 == title2
            }
        }
    }
}

class ArticleListDiffUtilCallback(
    private val oldList: List<Article>,
    private val newList: List<Article>
) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].fullPath == newList[newItemPosition].fullPath
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (title1, _, fullPath1) = oldList[oldItemPosition]
        val (title2, _, fullPath2) = newList[newItemPosition]

        return title1 == title2 && fullPath1 == fullPath2
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}

interface ItemTouchHelperAdapter<T : Any> {
    fun removeItem(position: Int)
    fun archiveItem(position: Int)
    fun restoreItem()
    //fun getItem(position: Int): T
}
