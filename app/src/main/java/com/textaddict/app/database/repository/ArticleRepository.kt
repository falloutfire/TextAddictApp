package com.textaddict.app.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.github.falloutfire.htmlparser.Entity.Page
import com.github.falloutfire.htmlparser.JHtmlParser
import com.textaddict.app.database.DataGenerator
import com.textaddict.app.database.dao.ArticleDao
import com.textaddict.app.database.entity.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ArticleRepository(/*val network: MainNetwork,*/ private val articleDao: ArticleDao) {

    val articles: LiveData<List<Article>> by lazy(LazyThreadSafetyMode.NONE) {
        Transformations.map(articleDao.loadAllArticle()) { it }
    }

    suspend fun refreshListArticle() {
        return withContext(Dispatchers.IO) {

        }
    }

    suspend fun addArticleInDatabase(userId: Long) {
        return withContext(Dispatchers.IO) {
            val article = Article(
                "Baeldung",
                DataGenerator().getHost("https://www.baeldung.com/spring-resttemplate-post-json"),
                "https://www.baeldung.com/spring-resttemplate-post-json",
                Date(System.currentTimeMillis()),
                null, archieve = false, favorite = false, userId = userId
            )
            articleDao.insertArticle(article)
        }
    }

    suspend fun getArticleByIdAndUrl(id: Long, url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val article = articleDao.getArticleByIdAndDomain(id, url)
                if (article != null) {
                    if (article.content != null) {
                        return@withContext article.content
                    } else {
                        val page = getPageFromHtmlParser(url)
                        article.content = page.article.outerHtml()
                        articleDao.insertArticle(article)
                        return@withContext article.content
                    }
                } else {
                    val page = getPageFromHtmlParser(url)
                    articleDao.insertArticle(
                        Article(
                            "Test",
                            page.domain,
                            page.uri.path,
                            Date(System.currentTimeMillis()),
                            page.article.outerHtml(),
                            archieve = false, favorite = false, userId = 0
                        )
                    )
                    return@withContext page.article.outerHtml()
                }
            } catch (error: Exception) {
                throw error
            }
        }
    }

    private suspend fun getPageFromHtmlParser(url: String): Page = withContext(Dispatchers.Default) {
        val jHtmlParser =
            JHtmlParser(
                url,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36"
                /*"Mozilla/5.0 (Linux; Android 9; Mi A2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36"*/
            )
        jHtmlParser.init()
        return@withContext jHtmlParser.page
    }
}

/**
 * Thrown when there was a error fetching a new title
 *
 * @property message user ready error message
 * @property cause the original cause of this exception
 */
class ArticleRefreshError(cause: Throwable) : Throwable(cause.message, cause)