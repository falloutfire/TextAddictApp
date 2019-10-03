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

class UserLogin() {

    var username: String? = null
    var password: String? = null
    var email: String? = null

    constructor(username: String, password: String, email: String? = null) : this() {
        this.email = email
        this.username = username
        this.password = password
    }

}

data class UserToken(
    var status: String?,
    var access_token: String?,
    var refresh_token: String?,
    var expired_in: Long?
)

/*
class UserToken() {
    var status: String? = null
    var access_token: String? = null
    var refresh_token: String? = null
    var expired_in: Long? = null

    constructor(status: String, access_token: String, refresh_token: String, expired_in: Long): this() {
        this.status = status
        this.access_token = access_token
        this.refresh_token = refresh_token
        this.expired_in = expired_in
    }
}*/
