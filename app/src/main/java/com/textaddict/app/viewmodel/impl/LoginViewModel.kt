package com.textaddict.app.viewmodel.impl

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.repository.UserRepository
import com.textaddict.app.network.ArticleApiService
import com.textaddict.app.viewmodel.AppViewModel

class LoginViewModel(application: Application) : AppViewModel(application) {

    private val repository: UserRepository
    val login: LiveData<Boolean>
        get() = _login
    private val _login = MutableLiveData<Boolean>()

    val test: LiveData<Boolean>
        get() = _test
    private var _test = MutableLiveData<Boolean>()

    init {
        val userDao = AppDatabase.getDatabase(application, viewModelScope).userDao()
        repository = UserRepository(userDao, ArticleApiService.userService)
    }

    fun checkUser(userName: String, userPassword: String, pref: SharedPreferences) {
        launchDataLoad {
            var user = false
            try {
                user = repository.getUserFromServer(userName, userPassword, pref)
                _login.postValue(user)
            } catch (e: Exception) {
                Log.e("Exception", e.message)
                e.printStackTrace()
            }
        }
    }
}