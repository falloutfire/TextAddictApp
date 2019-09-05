package com.textaddict.app.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.repository.ArticleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    val title: LiveData<String>
        get() = _title
    private val _title = MutableLiveData<String>()

    val page: LiveData<String>
        get() = _page
    private val _page = MutableLiveData<String>()

    private val repository: ArticleRepository
    val articles: LiveData<List<Article>>

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean>
        get() = _spinner

    init {
        val articleDao = AppDatabase.getDatabase(application, viewModelScope).articleDao()
        repository = ArticleRepository(articleDao)
        articles = repository.articles
    }

    @RequiresApi(Build.VERSION_CODES.N)
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

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Exception) {
                error.message
            } finally {
                _spinner.value = false
            }
        }
    }
}