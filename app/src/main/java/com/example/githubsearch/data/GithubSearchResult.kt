package com.example.githubsearch.data

// Matching the Github search data model.

data class GithubSearchResult(
    val id: String,
    val full_name: String,
    val owner: GithubAvatar,
    val description: String?
) {

    // Nested data class for the "owner" object.
    data class GithubAvatar(
        val avatar_url: String?
    )

}
