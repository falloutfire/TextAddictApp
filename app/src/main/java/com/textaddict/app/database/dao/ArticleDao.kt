package com.textaddict.app.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.textaddict.app.database.entity.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article)

    @Query("select * from Article")
    fun loadAllArticle(): LiveData<List<Article>>

    @Query("select * from Article where archive = 0")
    fun loadAllArticleWithoutArchive(): LiveData<List<Article>>

    @Query("select * from Article where archive = 1")
    fun loadAllArchivedArticle(): LiveData<List<Article>>

    @Query("UPDATE Article set archive = 1 where id = :id")
    fun setArchiveArticle(id: Long)

    @Query("UPDATE Article set archive = 0 where id = :id")
    fun setUnarchiveArticle(id: Long)

    @Query("select * from Article")
    fun loadAllArticleCheck(): List<Article>

    @Query("select * from Article where fullPath = :fullPath")
    fun getArticleByDomain(fullPath: String): Article?

    @Query("select * from Article where id = :id")
    fun getArticleById(id: Long): Article?

    @Query("select * from Article where id = :id and fullPath = :fullPath")
    fun getArticleByIdAndDomain(id: Long, fullPath: String): Article?

    @Query("DELETE FROM Article where id = :id")
    fun deleteArticle(id: Long)

    @Query("DELETE FROM Article")
    suspend fun deleteAll()
}