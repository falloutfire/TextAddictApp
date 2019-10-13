package com.textaddict.app.ui.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.textaddict.app.database.entity.ArticleType

class ArticleLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<ArticleType>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<ArticleType>? {

        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            val viewHolder = recyclerView.getChildViewHolder(view)
            if (viewHolder is ArticleViewAdapter.ViewHolderArticle) {
                return viewHolder.getItemDetails()
            }
        }

        return null
    }
}