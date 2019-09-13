package com.textaddict.app.viewmodel.impl

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.textaddict.app.database.AppDatabase
import com.textaddict.app.database.repository.UserRepository
import com.textaddict.app.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AppViewModel(application) {

    private val repository: UserRepository
    val login: LiveData<Boolean>
        get() = _login
    private val _login = MutableLiveData<Boolean>()

    init {
        val userDao = AppDatabase.getDatabase(application, viewModelScope).userDao()
        repository = UserRepository(userDao)
    }

    fun checkUser(userName: String, userPassword: String, pref: SharedPreferences) {
        launchDataLoad {
            val data = withContext(Dispatchers.IO) {
                repository.getUserFromServer(userName, userPassword, pref)
            }
            _login.value = data
        }
    }
}