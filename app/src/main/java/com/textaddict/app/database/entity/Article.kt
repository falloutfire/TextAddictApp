package com.textaddict.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.textaddict.app.database.converter.DateConverter

@Entity
data class Article(
    var title: String,
    var domain: String,
    var fullPath: String,
    @TypeConverters(DateConverter::class)
    var date: Long,
    var content: String?
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}