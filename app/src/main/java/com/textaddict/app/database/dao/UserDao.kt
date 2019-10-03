package com.textaddict.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.textaddict.app.database.entity.User

@Dao
interface UserDao {

    @Query("select * from User where userId = :userId")
    fun getUserById(userId: Long): User?

    @Query("select * from User where username = :username")
    fun getUserByUsername(username: String): User?

    @Query("select * from User where username = :username")
    fun getUserByUsernameAsync(username: String): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("DELETE FROM User where userId = :userId")
    fun deleteUser(userId: Long)

}