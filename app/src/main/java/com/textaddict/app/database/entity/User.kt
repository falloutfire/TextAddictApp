package com.textaddict.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.textaddict.app.R

@Entity
data class User(
    var username: String,
    var email: String,
    var password: String,
    var refresh_token: String,
    var imageProfile: Int = R.mipmap.ic_profile_label_round
) {

    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0
}

data class UserLogin(var username: String?, var password: String?, var email: String? = null)

data class UserToken(
    var status: String?,
    var access_token: String?,
    var refresh_token: String?,
    var expired_in: Long?
)