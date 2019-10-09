package com.textaddict.app.database.repository

import android.content.Context
import android.content.SharedPreferences
import com.textaddict.app.database.dao.UserDao
import com.textaddict.app.database.entity.User
import com.textaddict.app.database.entity.UserLogin
import com.textaddict.app.database.entity.UserToken
import com.textaddict.app.network.BaseRepository
import com.textaddict.app.network.Output
import com.textaddict.app.network.service.UserService
import com.textaddict.app.ui.activity.StartUpActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao, private val apiInterface: UserService) :
    BaseRepository() {

    val APP_PREFERENCES_USER_ACCESS_TOKEN = "access_token"

    private suspend fun processLoginUserInServer(
        username: String,
        userPassword: String
    ): Output<UserToken> {
        val login = UserLogin(username, userPassword)

        return safeApiResponse(
            call = {
                apiInterface.loginUserAsync(login)
            },
            error = "Error fetching user"
        )
    }

    private suspend fun processSignUpUserInServer(
        username: String,
        userPassword: String,
        userEmail: String
    ): Output<UserToken> {
        val login = UserLogin(username, userPassword, userEmail)

        return safeApiResponse(
            call = {
                apiInterface.registerUserAsync(login)
            },
            error = "Error fetching user"
        )
    }

    suspend fun loginUserInServer(
        username: String,
        userPassword: String,
        pref: SharedPreferences,
        context: Context
    ): Output<UserToken> =
        withContext(Dispatchers.IO) {

            val response = processLoginUserInServer(username, userPassword)

            if (response is Output.Success<UserToken>) {
                userDao.insertUser(
                    User(
                        username,
                        "email",
                        userPassword,
                        response.output.refresh_token!!
                    )
                )

                val user = userDao.getUserByUsername(username)

                val editor = pref.edit()
                editor.putLong(StartUpActivity.APP_PREFERENCES_USER_ID, user!!.userId)
                editor.putString(APP_PREFERENCES_USER_ACCESS_TOKEN, response.output.access_token)
                editor.apply()

                return@withContext response
            } else {
                return@withContext response
            }
        }

    suspend fun signUpUserFromServer(
        username: String,
        userPassword: String,
        userEmail: String,
        pref: SharedPreferences,
        context: Context
    ): Output<UserToken> =
        withContext(Dispatchers.IO) {

            val response = processSignUpUserInServer(username, userPassword, userEmail)
            if (response is Output.Success<UserToken>) {
                userDao.insertUser(
                    User(
                        username,
                        userEmail,
                        userPassword,
                        response.output.refresh_token!!
                    )
                )

                val user = userDao.getUserByUsername(username)

                val editor = pref.edit()
                editor.putLong(StartUpActivity.APP_PREFERENCES_USER_ID, user!!.userId)
                editor.putString(APP_PREFERENCES_USER_ACCESS_TOKEN, response.output.access_token)
                editor.apply()

                return@withContext response
            } else {
                return@withContext response
            }
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