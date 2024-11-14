package de.astronarren.allsky.data

import retrofit2.http.GET

interface AllskyService {
    @GET("videos/")
    suspend fun getTimelapses(): String

    @GET("keograms/")
    suspend fun getKeograms(): String

    @GET("startrails/")
    suspend fun getStartrails(): String
}

data class AllskyContent(
    val timelapses: List<AllskyMedia>,
    val keograms: List<AllskyMedia>,
    val startrails: List<AllskyMedia>
)

data class AllskyMedia(
    val date: String,
    val url: String
) 