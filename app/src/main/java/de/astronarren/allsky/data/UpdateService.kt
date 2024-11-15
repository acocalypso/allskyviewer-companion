package de.astronarren.allsky.data

import retrofit2.http.GET

interface UpdateService {
    @GET("releases/latest")
    suspend fun getLatestRelease(): GithubRelease
}

data class GithubRelease(
    val tag_name: String,
    val html_url: String,
    val assets: List<GithubAsset>,
    val body: String
)

data class GithubAsset(
    val name: String,
    val browser_download_url: String,
    val size: Long
) 