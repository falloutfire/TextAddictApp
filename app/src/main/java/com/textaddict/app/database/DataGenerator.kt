package com.textaddict.app.database

import com.textaddict.app.database.entity.Article
import java.net.URL
import java.util.*

/**
 * Generates data to pre-populate the database
 */
class DataGenerator {

    fun generateArticles(userId: Long): ArrayList<Article> {
        return arrayListOf(
            Article(
                "Habrahabr DODO",
                getHost("https://habr.com/ru/company/dodopizzaio/blog/462747/"),
                "https://habr.com/ru/company/dodopizzaio/blog/462747/",
                Date(System.currentTimeMillis()),
                null, archieve = false, favorite = false, userId = userId
            ),
            Article(
                "Habrahabr POST",
                getHost("https://habr.com/ru/post/429058/"),
                "https://habr.com/ru/post/429058/",
                Date(System.currentTimeMillis()),
                null, archieve = false, favorite = false, userId = userId
            ),
            Article(
                "Meduza",
                getHost("https://meduza.io/feature/2019/08/17/vrachey-ne-predupredili-o-radiatsii-pri-lechenii-postradavshih-ot-vzryva-pod-severodvinskom-v-organizme-odnogo-medika-nashli-tseziy-137"),
                "https://meduza.io/feature/2019/08/17/vrachey-ne-predupredili-o-radiatsii-pri-lechenii-postradavshih-ot-vzryva-pod-severodvinskom-v-organizme-odnogo-medika-nashli-tseziy-137",
                Date(System.currentTimeMillis()),
                null, archieve = false, favorite = false, userId = userId
            ),
            Article(
                "Baeldung",
                getHost("https://www.baeldung.com/spring-resttemplate-post-json"),
                "https://www.baeldung.com/spring-resttemplate-post-json",
                Date(System.currentTimeMillis()),
                null, archieve = false, favorite = false, userId = userId
            )
        )
    }

    fun getHost(url: String): String {
        return URL(url).host
    }
}