package com.textaddict.app

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.textaddict.app.database.AppDatabase.Companion.getDatabase
import com.textaddict.app.database.repository.ArticleRefreshError
import com.textaddict.app.database.repository.ArticleRepository
import kotlinx.coroutines.coroutineScope

/**
 * Рабочее задание для обновления заголовков из сети, пока приложение находится в фоновом режиме.
 *
 * WorkManager - это библиотека, используемая для постановки в очередь работы, которая гарантированно будет выполняться
 * после выполнения ее ограничений. Он может работать, даже если приложение работает в фоновом режиме или не работает.
 */
class RefreshMainDataWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    /**
     * Обновить заголовок из сети, используя [ArticleRepository]
     * WorkManager вызовет этот метод из фонового потока. Он может быть вызван даже после того, как наше приложение
     * было закрыто операционной системой, и в этом случае [WorkManager] запустится достаточно для запуска этого [Worker].
     */
    override suspend fun doWork(): Result = coroutineScope {
        val database = getDatabase(applicationContext, this)
        val repository =
            ArticleRepository(/*MainNetworkImpl,*/ database.articleDao())

        return@coroutineScope try {
            repository.refreshListArticle()
            Result.success()
        } catch (error: ArticleRefreshError) {
            Result.failure()
        }
    }
}