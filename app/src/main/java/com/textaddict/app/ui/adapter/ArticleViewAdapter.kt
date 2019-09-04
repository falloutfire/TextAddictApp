package com.textaddict.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
) : RecyclerView.Adapter<ArticleViewAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var articles = emptyList<Article>()
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Article
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles[position]
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

    override fun getItemCount(): Int = articles.size

    internal fun setArticles(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView = mView.item_article_title
        val mDomainView: TextView = mView.item_article_domain
        val mDateView: TextView = mView.item_article_date

        override fun toString(): String {
            return super.toString() + " '" + mDomainView.text + "'"
        }
    }
}
