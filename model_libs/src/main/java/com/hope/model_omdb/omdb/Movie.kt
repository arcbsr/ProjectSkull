package com.hope.models.models

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: String,
    @SerializedName("imdbID") val imdbID: String,
    @SerializedName("Type") val type: String,
    @SerializedName("Poster") val poster: String,
    var pageNumber: Int = 0
) {
    override fun toString(): String {
        return "ID: $imdbID\nTitle: $title\nYear: $year\nType: $type"
    }
}

data class SearchResponseData(
    @SerializedName("Search") val search: List<Movie>,
    @SerializedName("totalResults") val totalResults: String,
    @SerializedName("Response") val response: String
)
data class MovieDetailsResponse(
    val Title: String,
    val Year: String,
    val Plot: String,
    val PosterUrl: String?

)
