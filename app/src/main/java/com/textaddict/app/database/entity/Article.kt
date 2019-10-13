package com.textaddict.app.database.entity

import androidx.room.*
import com.textaddict.app.database.converter.Converters
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("userId"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Article(
    var title: String,
    var domain: String,
    var fullPath: String,
    @TypeConverters(Converters::class)
    var date: Date?,
    var content: String?,
    var archive: Boolean = false,
    @ColumnInfo(name = "user_id")
    var userId: Long
) : ArticleType {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class ArchiveItem(var listTitles: String) : ArticleType

interface ArticleType {
    companion object {
        val ARTICLE_TYPE = 0
        val ARCHIEVE_TYPE = 1
    }
}