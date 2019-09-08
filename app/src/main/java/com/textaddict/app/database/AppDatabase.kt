package com.textaddict.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.textaddict.app.database.converter.Converters
import com.textaddict.app.database.dao.ArticleDao
import com.textaddict.app.database.dao.UserDao
import com.textaddict.app.database.entity.Article
import com.textaddict.app.database.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * AppDatabase provides a reference to the dao to repositories
 */
@Database(entities = [Article::class, User::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "article_db"
                ).addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.userDao(), database.articleDao())
                    }
                }
            }
        }

        fun populateDatabase(userDao: UserDao, articleDao: ArticleDao) {
            if (userDao.getUserByUsername("Man") == null) {
                userDao.insertUser(User("Man", "email@mail.com"))
            }

            if (articleDao.loadAllArticleCheck().isEmpty()) {
                val user = userDao.getUserByUsername("Man")
                val list: List<Article> = DataGenerator().generateArticles(user!!.id)
                for (i in list) {
                    articleDao.insertArticle(i)
                }
            }
        }
    }
}