package com.example.githubsearch.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Matching the Github search data model.
@Parcelize
data class GithubSearchResult(
    val id: String,
    val full_name: String,
    val owner: GithubAvatar,
    val description: String?
) : Parcelable {

    // Nested data class for the "owner" object.
    @Parcelize
    data class GithubAvatar(
        val avatar_url: String?
    ) : Parcelable

}
