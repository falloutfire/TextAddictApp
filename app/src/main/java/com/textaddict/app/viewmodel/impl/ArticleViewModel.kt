package com.textaddict.app.viewmodel.impl

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.repository.ArticleRepository
import com.textaddict.app.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleViewModel(application: Application) : AppViewModel(application) {

    val title: LiveData<String>
        get() = _title
    private val _title = MutableLiveData<String>()

    val page: LiveData<String>
        get() = _page
    private val _page = MutableLiveData<String>()

    private val repository: ArticleRepository
    val articles: LiveData<List<Article>>

    init {
        val articleDao = AppDatabase.getDatabase(application, viewModelScope).articleDao()
        repository = ArticleRepository(articleDao)
        articles = repository.articles
    }

    fun getPage(articleId: Long?, domain: String?) {
        if (domain != null && articleId != null) {
            launchDataLoad {
                val page: String? = withContext(Dispatchers.IO) {
                    repository.getArticleByIdAndUrl(articleId, domain)
                }
                _title.value = page
                _page.value = page
            }
        }
    }
}