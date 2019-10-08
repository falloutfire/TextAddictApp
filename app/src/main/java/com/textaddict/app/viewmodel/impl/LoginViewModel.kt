package com.textaddict.app.viewmodel.impl

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.entity.UserToken
import com.textaddict.app.database.repository.UserRepository
import com.textaddict.app.network.ArticleApiService
import com.textaddict.app.network.Output
import com.textaddict.app.network.ResultLogin
import com.textaddict.app.viewmodel.AppViewModel

class LoginViewModel(application: Application) : AppViewModel(application) {

    private val repository: UserRepository

    val resultLogin: LiveData<Output<UserToken>>
        get() = _resultLogin
    private val _resultLogin = MutableLiveData<Output<UserToken>>()

    val resultSignUp: LiveData<ResultLogin>
        get() = _resultSignUp
    private val _resultSignUp = MutableLiveData<ResultLogin>()

    init {
        val userDao = AppDatabase.getDatabase(application, viewModelScope).userDao()
        repository = UserRepository(userDao, ArticleApiService.userService)
    }

    fun loginUserInServer(userName: String, userPassword: String, pref: SharedPreferences) {
        var user: Output<UserToken>?
        launchDataLoad {
            user = repository.loginUserInServer(userName, userPassword, pref, getApplication())
            _resultLogin.postValue(user)
        }
    }

    fun signUpUserInServer(
        userName: String,
        userPassword: String,
        userEmail: String,
        pref: SharedPreferences
    ) {
        var user: ResultLogin? = null
        launchDataLoad {
            try {
                user = repository.signUpUserFromServer(
                    userName,
                    userPassword,
                    userEmail,
                    pref,
                    getApplication()
                )
                //_login.postValue(user)
                _resultSignUp.postValue(user)
            } catch (e: Exception) {
                _resultSignUp.postValue(user)
                Log.e("Exception", e.message)
                e.printStackTrace()
            }
        }
    }
}

