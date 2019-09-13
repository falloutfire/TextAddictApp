package com.textaddict.app.viewmodel.impl

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.User
import com.textaddict.app.database.repository.ArticleRepository
import com.textaddict.app.database.repository.UserRepository
import com.textaddict.app.viewmodel.AppViewModel

class ProfileViewModel(application: Application) : AppViewModel(application) {

    val user: LiveData<User>
        get() = _user
    private val _user = MutableLiveData<User>()

    val countPages: LiveData<List<Article>>

    private val articleRepository: ArticleRepository
    private val userRepository: UserRepository

    init {
        val articleDao = AppDatabase.getDatabase(application, viewModelScope).articleDao()
        val userDao = AppDatabase.getDatabase(application, viewModelScope).userDao()
        articleRepository = ArticleRepository(articleDao)
        userRepository = UserRepository(userDao)
        countPages = articleRepository.articles
    }

    fun getUser() {
        launchDataLoad {
            val user = userRepository.getUserByUsername("Man")
            _user.value = user
        }
    }


}