package com.textaddict.app.database.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.github.falloutfire.htmlparser.JHtmlParser
import com.textaddict.app.database.DataGenerator
import com.textaddict.app.database.dao.ArticleDao
import com.textaddict.app.database.entity.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepository(/*val network: MainNetwork,*/ private val articleDao: ArticleDao) {

    val articles: LiveData<List<Article>> = articleDao.loadAllArticle()

    /*val articles: LiveData<List<Article>> by lazy(LazyThreadSafetyMode.NONE) {
        Transformations.map(articleDao.loadAllArticle()) { it }
    }*/

    suspend fun refreshListArticle() {
        return withContext(Dispatchers.IO) {
            // TODO set request to server
            //val result = network.fetchNewWelcome().await()
            //articleDao.insertTitle(Title(result))
        }
    }

    //@WorkerThread
    suspend fun initDatabase() {
        return withContext(Dispatchers.IO) {
            val list: List<Article> = DataGenerator().generateArticles()
            for (i in list) {
                articleDao.insertArticle(i)
            }
        }
    }

    //@WorkerThread
    suspend fun addArticle() {
        return withContext(Dispatchers.IO) {
            val article = Article(
                "Baeldung",
                DataGenerator().getHost("https://www.baeldung.com/spring-resttemplate-post-json"),
                "https://www.baeldung.com/spring-resttemplate-post-json",
                System.currentTimeMillis(),
                null
            )
            articleDao.insertArticle(article)
        }
    }

    //@WorkerThread
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun refreshTitle(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val article = articleDao.getArticleByDomain(url)
                if (article.isPresent) {
                    if (article.get().content != null) {
                        return@withContext article.get().content
                    } else {
                        val jHtmlParser =
                            JHtmlParser(
                                url,
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36"
                            )
                        jHtmlParser.init()
                        val update = article.get()
                        update.content = jHtmlParser.outerHtml()
                        articleDao.insertArticle(update)
                        return@withContext update.content
                    }
                } else {
                    val jHtmlParser =
                        JHtmlParser(
                            url,
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36"
                            /*"Mozilla/5.0 (Linux; Android 9; Mi A2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36"*/
                        )
                    jHtmlParser.init()
                    articleDao.insertArticle(
                        Article(
                            "Test",
                            jHtmlParser.page.domain,
                            jHtmlParser.page.uri.path,
                            System.currentTimeMillis(),
                            jHtmlParser.outerHtml()
                        )
                    )
                    return@withContext jHtmlParser.outerHtml()
                }
            } catch (error: Exception) {
                throw error
            }
        }
    }
}

/**
 * Thrown when there was a error fetching a new title
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class ArticleRefreshError(cause: Throwable) : Throwable(cause.message, cause)