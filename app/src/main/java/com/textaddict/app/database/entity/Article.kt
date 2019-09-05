package com.textaddict.app.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.textaddict.app.database.converter.Converters
import java.util.*

//TODO maybe add key domain
@Entity
data class Article(
    var title: String,
    var domain: String,
    var fullPath: String,
    @TypeConverters(Converters::class)
    var date: Date?,
    var content: String?,
    var archieve: Boolean = false,
    var favorite: Boolean = false
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}