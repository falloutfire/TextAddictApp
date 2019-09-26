package com.textaddict.app.viewmodel.impl

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.repository.UserRepository
import com.textaddict.app.network.ArticleApiService
import com.textaddict.app.network.ResultLogin
import com.textaddict.app.viewmodel.AppViewModel

class LoginViewModel(application: Application) : AppViewModel(application) {

    private val repository: UserRepository
    val login: LiveData<Boolean>
        get() = _login
    private val _login = MutableLiveData<Boolean>()

    val resultLogin: LiveData<ResultLogin>
        get() = _resultLogin
    private val _resultLogin = MutableLiveData<ResultLogin>()

    init {
        val userDao = AppDatabase.getDatabase(application, viewModelScope).userDao()
        repository = UserRepository(userDao, ArticleApiService.userService)
    }

    fun checkUser(userName: String, userPassword: String, pref: SharedPreferences) {
        var user: ResultLogin
        launchDataLoad {
            //try {
            user = repository.getUserFromServer(userName, userPassword, pref, getApplication())
            //_login.postValue(user)
            _resultLogin.postValue(user)
            /*} catch (e: Exception) {
                _resultLogin.postValue(user)
                Log.e("Exception", e.message)
                e.printStackTrace()
            }*/
        }
    }
}

