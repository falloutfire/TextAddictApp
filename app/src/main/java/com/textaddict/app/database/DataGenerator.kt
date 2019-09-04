package com.textaddict.app.database

import com.textaddict.app.database.entity.Article
import java.net.URL

/**
 * Generates data to pre-populate the database
 */
class DataGenerator {

    fun generateArticles(): ArrayList<Article> {
        return arrayListOf(
            Article(
                "Habrahabr DODO",
                getHost("https://habr.com/ru/company/dodopizzaio/blog/462747/"),
                "https://habr.com/ru/company/dodopizzaio/blog/462747/",
                System.currentTimeMillis(),
                null
            ),
            Article(
                "Habrahabr POST",
                getHost("https://habr.com/ru/post/429058/"),
                "https://habr.com/ru/post/429058/",
                System.currentTimeMillis(),
                null
            ),
            Article(
                "Meduza",
                getHost("https://meduza.io/feature/2019/08/17/vrachey-ne-predupredili-o-radiatsii-pri-lechenii-postradavshih-ot-vzryva-pod-severodvinskom-v-organizme-odnogo-medika-nashli-tseziy-137"),
                "https://meduza.io/feature/2019/08/17/vrachey-ne-predupredili-o-radiatsii-pri-lechenii-postradavshih-ot-vzryva-pod-severodvinskom-v-organizme-odnogo-medika-nashli-tseziy-137",
                System.currentTimeMillis(),
                null
            ),
            Article(
                "Baeldung",
                getHost("https://www.baeldung.com/spring-resttemplate-post-json"),
                "https://www.baeldung.com/spring-resttemplate-post-json",
                System.currentTimeMillis(),
                null
            )
        )
    }

    fun getHost(url: String): String {
        return URL(url).host
    }
}