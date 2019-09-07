package com.textaddict.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.repository.ArticleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/*class ListArticleViewModel(app: Application) : AndroidViewModel(app) {

    val database = (app as TextAddictApplication).getRepository()

    val title: LiveData<String>
        get() = _title
    private val _title = MutableLiveData<String>()

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val articleRepository = ArticleRepository(database.articleDao)

    val articles: MediatorLiveData<List<Article>> = MediatorLiveData()

    init {
        // set by default null, until we get data from the database.
        articles.value = articleRepository.articles.value
        //val list = articleRepository.loadAll()
        articles.addSource(articleRepository.articles) { articles.setValue(it) }
    }

    fun initDatabase() {
        launchDataLoad {
            if (articles.value!!.isEmpty()) {
                articleRepository.initDatabase()
            }
        }
    }

    fun addData() {
        launchDataLoad {
            articleRepository.addArticleInDatabase()
        }
    }

    fun getPage(domain: String?) {
        if (domain != null) {
            launchDataLoad {
                val page: String? = withContext(Dispatchers.IO) {
                    articleRepository.refreshTitle(domain)
                }
                _title.value = page
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
}*/


class ListArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArticleRepository
    val articles: LiveData<List<Article>>

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean>
        get() = _spinner

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

    fun addData() {
        launchDataLoad {
            repository.addArticleInDatabase()
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