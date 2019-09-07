package com.textaddict.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.textaddict.app.R

@Entity
data class User(var username: String, var userEmail: String, var imageProfile: Int = R.mipmap.ic_profile_label_round) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}