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

    val title: LiveData<String>
        get() = _title
    private val _title = MutableLiveData<String>()

    val page: LiveData<String>
        get() = _page
    private val _page = MutableLiveData<String>()

    init {
        val articleDao = AppDatabase.getDatabase(application, viewModelScope).articleDao()
        repository = ArticleRepository(articleDao)
        articles = repository.articles
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

    fun deleteArticle(id: Long) {
        launchDataLoad {
            repository.removeArticleInDataBase(id)
        }
    }
}