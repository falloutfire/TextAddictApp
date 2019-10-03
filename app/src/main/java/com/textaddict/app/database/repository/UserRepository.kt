package com.textaddict.app.database.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.textaddict.app.R
import com.textaddict.app.database.dao.UserDao
import com.textaddict.app.database.entity.User
import com.textaddict.app.database.entity.UserLogin
import com.textaddict.app.database.entity.UserToken
import com.textaddict.app.network.BaseRepository
import com.textaddict.app.network.ResultLogin
import com.textaddict.app.network.service.UserService
import com.textaddict.app.ui.activity.StartUpActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao, private val apiInterface: UserService) :
    BaseRepository() {

    val APP_PREFERENCES_USER_ACCESS_TOKEN = "access_token"

    private suspend fun loginUserInServer(username: String, userPassword: String): UserToken? {
        val token = MutableLiveData<Boolean>()

        val login = UserLogin(username, userPassword)

        val call = safeApiCall(
            call = {
                apiInterface.loginUserAsync(login)
            },
            error = "Error fetching user"
        )

        return call
    }

    suspend fun getUserFromServer(
        username: String,
        userPassword: String,
        pref: SharedPreferences,
        context: Context
    ): ResultLogin =
        withContext(Dispatchers.IO) {

            /* val response = loginUserInServer(username, userPassword)
             if (response) {
                 // todo get userdetails from server
                 userDao.insertUser(User(username, "email", userPassword))

                 val user = userDao.getUserByUsername(username)
                 val editor = pref.edit()
                 editor.putLong(StartUpActivity.APP_PREFERENCES_USER_ID, user!!.userId)
                 editor.apply()
                 return@withContext true*/

            val response = loginUserInServer(username, userPassword)
            if (response != null) {
                userDao.insertUser(User(username, "email", userPassword, response.refresh_token!!))

                val user = userDao.getUserByUsername(username)

                val editor = pref.edit()
                editor.putLong(StartUpActivity.APP_PREFERENCES_USER_ID, user!!.userId)
                editor.putString(APP_PREFERENCES_USER_ACCESS_TOKEN, response.access_token)
                editor.apply()

                return@withContext ResultLogin.Success
                //return@withContext true
            } else {
                val e = Exception()
                return@withContext ResultLogin.Error(
                    message = context.resources.getString(R.string.bad_credentials),
                    exception = e
                )
            }

            //val response = loginUserInServer(username, userPassword)
            //if (response?.)

            /*val user = userDao.getUserByUsername(username)
            // todo get userdetails from server

            if (user?.let
                {
                    return@let it.userPassword == userPassword
                } == true
            ) {

                //Сохраняем userId пользователя в Pref
                val editor = pref.edit()
                editor.putLong(StartUpActivity.APP_PREFERENCES_USER_ID, user.userId)
                editor.apply()
                // TODO update user in db
                return@withContext ResultLogin.Success
            } else {
                val e = Exception()
                return@withContext ResultLogin.Error(
                    message = context.resources.getString(R.string.bad_credentials),
                    exception = e
                )
            }*/
            /* } else {
                 return@withContext false
             }*/
        }

    suspend fun getUserById(id: Long): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(id)
        }
    }

    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUsername(username)
        }
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

    suspend fun deleteUser(id: Long) {
        withContext(Dispatchers.IO) {
            userDao.deleteUser(id)
        }
    }
}