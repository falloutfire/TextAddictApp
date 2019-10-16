package com.textaddict.app.ui.adapter

import androidx.recyclerview.selection.ItemKeyProvider
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.ArticleType

// В конструкторе ItemKeyProvider мы выбираем метод предоставления доступа к данным:
//  SCOPE_MAPPED - ко всем данным. Позволяет реализовать Shift+click выбор данных
//  SCOPE_CACHED - к данным, которые были недавно или сейчас на экране. Экономит память
class ArticleKeyProvider(private val items: List<ArticleType>) :
    ItemKeyProvider<ArticleType>(SCOPE_CACHED) {

    override fun getKey(position: Int): ArticleType? {
        return if (items[position] is Article) {
            items.getOrNull(position)
        } else null
    }

    override fun getPosition(key: ArticleType) = items.indexOf(key)
}