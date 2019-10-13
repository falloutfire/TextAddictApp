package com.textaddict.app.viewmodel.impl

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.repository.ArticleRepository
import com.textaddict.app.viewmodel.AppViewModel

class ListArticleViewModel(application: Application) : AppViewModel(application) {

    private val repository: ArticleRepository
    val articles: LiveData<List<Article>>
    val archivedArticles: LiveData<List<Article>>

    val title: LiveData<String>
        get() = _title
    private val _title = MutableLiveData<String>()

    init {
        val articleDao = AppDatabase.getDatabase(application, viewModelScope).articleDao()
        repository = ArticleRepository(articleDao)
        articles = repository.articles
        archivedArticles = repository.archivedArticles
    }

    fun addData(userId: Long) {
        launchDataLoad {
            repository.addArticleInDatabase(userId)
        }
    }

    fun restoreArticle(article: Article) {
        launchDataLoad {
            repository.restoreArticleInDataBase(article)
        }
    }

    fun archiveArticle(id: Long) {
        launchDataLoad {
            repository.archiveArticleInDataBase(id)
        }
    }

    fun unarchiveArticle(id: Long) {
        launchDataLoad {
            repository.unarchiveArticleInDataBase(id)
        }
    }

    fun deleteArticle(id: Long) {
        launchDataLoad {
            repository.removeArticleInDataBase(id)
        }
    }
}