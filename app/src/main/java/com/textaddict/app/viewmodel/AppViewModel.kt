package com.textaddict.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

abstract class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _spinner = MutableLiveData<Boolean>()
    val spinner: LiveData<Boolean>
        get() = _spinner

    private var job = SupervisorJob()
    private var scope: CoroutineScope = viewModelScope + job

    fun launchDataLoad(block: suspend () -> Unit): Job {
        return scope.launch {
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