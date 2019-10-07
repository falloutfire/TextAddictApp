package com.textaddict.app.viewmodel.impl

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.repository.ArticleRepository
import com.textaddict.app.viewmodel.AppViewModel

class AddActivityViewModel(application: Application) : AppViewModel(application) {

    private val repository: ArticleRepository

    init {
        val articleDao = AppDatabase.getDatabase(application, viewModelScope).articleDao()
        repository = ArticleRepository(articleDao)
    }

    fun addArticleInStorage(url: String, userId: Long) {
        launchDataLoad {
            repository.addArticleInDatabase(url, userId)
        }
    }

    fun addArticleInStorage(url: String, siteName: String, userId: Long) {
        launchDataLoad {
            repository.addArticleInDatabase(url, siteName, userId)
        }
    }
}