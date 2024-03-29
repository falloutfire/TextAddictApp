package com.textaddict.app.ui.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.textaddict.app.database.entity.ArchiveItem
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.ArticleType
import com.textaddict.app.ui.fragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_archieve_item.view.*
import kotlinx.android.synthetic.main.fragment_article_item.view.*
import kotlinx.android.synthetic.main.fragment_article_item.view.item_back
import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * [RecyclerView.Adapter] that can display a [articles] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class ArticleViewAdapter internal constructor(
    private val recyclerView: RecyclerView,
    private val mListener: OnListFragmentInteractionListener?,
    private val isMainList: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ItemTouchHelperAdapter<ArticleType> {

    private val mOnClickListener: View.OnClickListener
    val list: ArrayList<ArticleType> = arrayListOf()
    private var count: Int = 0
    var selectionTracker: SelectionTracker<ArticleType>? = null
    private val COLLAPSE_INTERPOLATOR: Interpolator = AccelerateInterpolator(1f)
    private val COLLAPSE_ANIM_DURATION = 250L
    private var collapseHeight = 0

    init {
        mOnClickListener = View.OnClickListener { v ->
            if (v.tag is Article) {
                val item = v.tag as Article
                // Notify the active callbacks interface (the activity, if the fragment is attached to
                // one) that an item has been selected.
                mListener?.onListFragmentInteraction(item)
            } else if (v.tag is ArchiveItem) {
                val item = v.tag as ArchiveItem
                mListener?.onListFragmentInteraction(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            list[position] is Article -> ArticleType.ARTICLE_TYPE
            list[position] is ArchiveItem -> ArticleType.ARCHIEVE_TYPE
            else -> -1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderArticle) {
            val item = list[position] as Article
            holder.mTitleView.text = item.title
            holder.mDomainView.text = item.domain

            val df = SimpleDateFormat.getDateInstance(DateFormat.SHORT)
            val now = df.format(item.date!!)

            holder.mDateView.text = now

            selectionTracker?.let {
                if (it.isSelected(list[position])) {
                    holder.mChoose.visibility = View.VISIBLE
                    holder.setActivatedState(true)
                } else {
                    holder.mChoose.visibility = View.GONE
                    holder.setActivatedState(false)
                }
            }

            if (holder.itemView.height < collapseHeight) {
                val set = AnimatorSet()
                val anim = LayoutParamHeightAnimator.uncollapse(holder.itemView, collapseHeight)
                anim.setDuration(COLLAPSE_ANIM_DURATION).interpolator = COLLAPSE_INTERPOLATOR
                set.play(anim)
                set.start()
            }

            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        } else {
            holder as ViewHolderArchive
            val item = list[position] as ArchiveItem
            holder.mCountView.text = item.listTitles

            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ArticleType.ARTICLE_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(com.textaddict.app.R.layout.fragment_article_item, parent, false)
                ViewHolderArticle(view)
            }
            ArticleType.ARCHIEVE_TYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(com.textaddict.app.R.layout.fragment_archieve_item, parent, false)
                ViewHolderArchive(view)
            }
            else -> throw Exception("bad Item")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(articles: List<Article>) {
        if (list.isNotEmpty() && isMainList) {
            list.removeAt(0)
        }
        val diffCallback = ArticleListDiffUtilCallback(this.list, articles)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        list.clear()
        list.addAll(articles)

        diffResult.dispatchUpdatesTo(this)
        if (isMainList) {
            list.add(0, ArchiveItem(count.toString()))
            notifyItemChanged(0)
        }
    }

    fun setArchiveCount(count: Int) {
        this.count = count
        list.forEach {
            if (it is ArchiveItem) {
                it.listTitles = count.toString()
            }
        }
    }

    override fun removeItem(
        viewHolder: RecyclerView.ViewHolder,
        onSwipeTouchListener: OnSwipeTouchListener
    ) {
        val set = AnimatorSet()
        val animHeight = LayoutParamHeightAnimator.collapse(viewHolder.itemView)
        collapseHeight = viewHolder.itemView.height
        animHeight.setDuration(COLLAPSE_ANIM_DURATION).interpolator = COLLAPSE_INTERPOLATOR
        set.play(animHeight)

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                actionArticle = list[viewHolder.adapterPosition] as Article
                actionPosition = viewHolder.adapterPosition
                onSwipeTouchListener.onSwipeLeft(viewHolder)
                list.removeAt(viewHolder.adapterPosition)
                notifyItemRemoved(viewHolder.adapterPosition)
                notifyItemRangeChanged(viewHolder.adapterPosition, list.size)
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })
        set.start()
    }

    override fun restoreItem(
        viewHolder: RecyclerView.ViewHolder,
        onSwipeTouchListener: OnSwipeTouchListener
    ) {
        val set = AnimatorSet()
        val animHeight = LayoutParamHeightAnimator.uncollapse(viewHolder.itemView, collapseHeight)

        onSwipeTouchListener.onSwipeUndo(viewHolder)
        animHeight.setDuration(COLLAPSE_ANIM_DURATION).interpolator = COLLAPSE_INTERPOLATOR
        set.play(animHeight)

        this.list.add(actionPosition!!, actionArticle!!)
        notifyItemInserted(actionPosition!!)
        notifyItemRangeChanged(0, list.size)
        set.start()
    }

    override fun archiveItem(position: Int) {

    }

    fun getItemFromList(position: Int): ArticleType {
        return list[position]
    }

    inner class ViewHolderArticle(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mBackground: RelativeLayout = mView.item_back
        val mItemLayout: CardView? = mView.card_article_item
        val mTitleView: TextView = mView.item_article_title
        val mDomainView: TextView = mView.item_article_domain
        val mDateView: TextView = mView.item_article_date
        val mChoose: ImageView = mView.checkImageView

        override fun toString(): String {
            return super.toString() + " '" + mDomainView.text + "'"
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<ArticleType>? {
            return ArticleItemDetail(adapterPosition, list[adapterPosition])
        }

        fun setActivatedState(isActivated: Boolean) {
            itemView.isActivated = isActivated
        }
    }

    inner class ViewHolderArchive(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mBackground: RelativeLayout = mView.item_back
        val mCountView: TextView = mView.item_archive_count

        override fun toString(): String {
            return super.toString() + " '" + /*mTitleView.text +*/ "'"
        }
    }


    companion object {
        var actionArticle: Article? = null
        var actionPosition: Int? = null
    }
}

class ArticleListDiffUtilCallback(
    private val oldList: List<ArticleType>,
    private val newList: List<ArticleType>
) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition] is Article && newList[newItemPosition] is Article) {
            return (((oldList[oldItemPosition] as Article).fullPath == (newList[newItemPosition] as Article).fullPath)
                    && (oldList[oldItemPosition] as Article).id == (newList[newItemPosition] as Article).id)
        } else if (oldList[oldItemPosition] is ArchiveItem && newList[newItemPosition] is ArchiveItem) {
            return true
        }
        return false
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition] is Article && newList[newItemPosition] is Article) {
            val (title1, _, fullPath1) = oldList[oldItemPosition] as Article
            val (title2, _, fullPath2) = newList[newItemPosition] as Article

            return title1 == title2 && fullPath1 == fullPath2
        } else if (oldList[oldItemPosition] is ArchiveItem && newList[newItemPosition] is ArchiveItem) {
            return true
        }
        return false
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}

interface ItemTouchHelperAdapter<T : Any> {
    fun archiveItem(position: Int)
    fun restoreItem(viewHolder: RecyclerView.ViewHolder, onSwipeTouchListener: OnSwipeTouchListener)
    fun removeItem(viewHolder: RecyclerView.ViewHolder, onSwipeTouchListener: OnSwipeTouchListener)
}
