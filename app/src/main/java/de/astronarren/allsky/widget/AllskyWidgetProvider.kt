package de.astronarren.allsky.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import de.astronarren.allsky.R
import de.astronarren.allsky.data.UserPreferences
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class AllskyWidgetProvider : AppWidgetProvider() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == REFRESH_ACTION) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_allsky)
        
        // Set up refresh button click
        val refreshIntent = Intent(context, AllskyWidgetProvider::class.java).apply {
            action = REFRESH_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)

        views.setTextViewText(
            R.id.widget_last_update,
            context.getString(R.string.widget_refreshing)
        )
        appWidgetManager.updateAppWidget(appWidgetId, views)

        scope.launch(Dispatchers.IO) {
            try {
                val userPrefs = UserPreferences(context)
                val allskyUrl = userPrefs.getAllskyUrl()

                if (allskyUrl.isNotEmpty()) {
                    val imageUrl = "$allskyUrl/image.jpg?t=${System.currentTimeMillis()}"
                    val url = URL(imageUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()

                    val inputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    
                    withContext(Dispatchers.Main) {
                        views.setImageViewBitmap(R.id.widget_image, bitmap)
                        views.setTextViewText(
                            R.id.widget_last_update,
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        )
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                    
                    inputStream.close()
                    connection.disconnect()
                } else {
                    views.setTextViewText(
                        R.id.widget_last_update,
                        context.getString(R.string.widget_no_url)
                    )
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    views.setTextViewText(
                        R.id.widget_last_update,
                        context.getString(R.string.widget_error)
                    )
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    companion object {
        const val REFRESH_ACTION = "de.astronarren.allsky.widget.REFRESH"
    }
} 