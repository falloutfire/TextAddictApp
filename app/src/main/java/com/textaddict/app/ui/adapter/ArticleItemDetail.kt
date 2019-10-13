package com.textaddict.app.ui.adapter

import androidx.recyclerview.selection.ItemDetailsLookup
import com.textaddict.app.database.entity.ArticleType

class ArticleItemDetail(private val adapterPosition: Int, private val selectedKey: ArticleType?) :
    ItemDetailsLookup.ItemDetails<ArticleType>() {
    override fun getSelectionKey() = selectedKey

    override fun getPosition() = adapterPosition
}