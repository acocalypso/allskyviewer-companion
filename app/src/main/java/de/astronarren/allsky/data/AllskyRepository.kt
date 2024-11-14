package de.astronarren.allsky.data

import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AllskyRepository(private val userPreferences: UserPreferences) {
    suspend fun getAllContent(): AllskyContent {
        return withContext(Dispatchers.IO) {
            try {
                val baseUrl = userPreferences.getAllskyUrl()
                if (baseUrl.isEmpty()) {
                    println("Debug: No Allsky URL configured")
                    return@withContext AllskyContent(emptyList(), emptyList(), emptyList())
                }

                println("Debug: Fetching content from Allsky: $baseUrl")
                
                val timelapseDoc = Jsoup.connect("$baseUrl/videos/").get()
                val keogramDoc = Jsoup.connect("$baseUrl/keograms/").get()
                val startrailDoc = Jsoup.connect("$baseUrl/startrails/").get()

                println("Debug: Successfully fetched HTML documents")

                val keograms = parseKeograms(keogramDoc, baseUrl)
                println("Debug: Found ${keograms.size} keograms")
                
                val startrails = parseStartrails(startrailDoc, baseUrl)
                println("Debug: Found ${startrails.size} startrails")
                
                val timelapses = parseTimelapses(timelapseDoc, baseUrl)
                println("Debug: Found ${timelapses.size} timelapses")

                AllskyContent(
                    timelapses = timelapses,
                    keograms = keograms,
                    startrails = startrails
                )
            } catch (e: Exception) {
                println("Debug: Error fetching allsky content: ${e.message}")
                e.printStackTrace()
                AllskyContent(emptyList(), emptyList(), emptyList())
            }
        }
    }

    private fun parseTimelapses(doc: org.jsoup.nodes.Document, baseUrl: String): List<AllskyMedia> {
        return doc.select("div.archived-files a").mapNotNull { element ->
            try {
                val href = element.attr("href")
                if (href.endsWith(".mp4")) {
                    val dateText = element.select("div.day-text").text()
                    AllskyMedia(
                        date = dateText,
                        url = "$baseUrl/videos/$href"
                    )
                } else null
            } catch (e: Exception) {
                println("Debug: Error parsing timelapse element: ${e.message}")
                null
            }
        }
    }

    private fun parseKeograms(doc: org.jsoup.nodes.Document, baseUrl: String): List<AllskyMedia> {
        return doc.select("div.archived-files a").mapNotNull { element ->
            try {
                val href = element.attr("href")
                if (href.contains("keogram") && href.endsWith(".jpg")) {
                    val dateText = element.select("div.day-text").text()
                    AllskyMedia(
                        date = dateText,
                        url = "$baseUrl/keograms/$href"
                    )
                } else null
            } catch (e: Exception) {
                println("Debug: Error parsing keogram element: ${e.message}")
                null
            }
        }
    }

    private fun parseStartrails(doc: org.jsoup.nodes.Document, baseUrl: String): List<AllskyMedia> {
        return doc.select("div.archived-files a").mapNotNull { element ->
            try {
                val href = element.attr("href")
                if (href.contains("startrail") && href.endsWith(".jpg")) {
                    val dateText = element.select("div.day-text").text()
                    AllskyMedia(
                        date = dateText,
                        url = "$baseUrl/startrails/$href"
                    )
                } else null
            } catch (e: Exception) {
                println("Debug: Error parsing startrail element: ${e.message}")
                null
            }
        }
    }
} 