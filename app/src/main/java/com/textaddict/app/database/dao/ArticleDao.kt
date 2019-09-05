package com.textaddict.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.textaddict.app.database.entity.Article
import java.util.*

/***
 * Very small database that will hold one title
 */
@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article)

    @Query("select * from Article")
    fun loadAllArticle(): LiveData<List<Article>>

    @Query("select * from Article")
    fun loadAllArticleCheck(): List<Article>

    @Query("select * from Article where fullPath = :fullPath")
    fun getArticleByDomain(fullPath: String): Optional<Article>

    @Query("select * from Article where id = :id")
    fun getArticleById(id: Long): Optional<Article>

    @Query("select * from Article where id = :id and fullPath = :fullPath")
    fun getArticleByIdAndDomain(id: Long, fullPath: String): Optional<Article>

    @Query("DELETE FROM Article where id = :id")
    fun deleteArticle(id: String)

    @Query("DELETE FROM Article")
    suspend fun deleteAll()
}