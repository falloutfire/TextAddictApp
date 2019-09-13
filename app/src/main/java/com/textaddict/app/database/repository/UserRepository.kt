package com.textaddict.app.database.repository

import android.content.SharedPreferences
import com.textaddict.app.database.dao.UserDao
import com.textaddict.app.database.entity.User
import com.textaddict.app.ui.activity.StartUpActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserFromServer(username: String, userPassword: String, pref: SharedPreferences): Boolean =
        withContext(Dispatchers.IO) {
            //TODO get login from server
            val user = userDao.getUserByUsername(username)

            if (user?.let {
                    return@let it.userPassword == userPassword
                } == true) {

                //Сохраняем id пользователя в Pref
                val editor = pref.edit()
                editor.putLong(StartUpActivity.APP_PREFERENCES_USER_ID, user.id)
                editor.apply()
                // TODO update user in db
                true
            } else {
                false
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