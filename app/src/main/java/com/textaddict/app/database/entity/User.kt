package com.textaddict.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.textaddict.app.R

@Entity
data class User(
    var username: String,
    var userEmail: String,
    var userPassword: String,
    var imageProfile: Int = R.mipmap.ic_profile_label_round//,
    //todo achievements
    //var readingTime: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

class UserLogin(var username: String, var password: String)

class UserToken(var accessToken: String, var refreshToken: String)