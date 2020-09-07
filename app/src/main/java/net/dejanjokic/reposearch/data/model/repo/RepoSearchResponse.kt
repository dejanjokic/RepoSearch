package net.dejanjokic.reposearch.data.model.repo

import com.squareup.moshi.Json

data class RepoSearchResponse(
    @field:Json(name = "total_count") val totalCount: Int = 0,
    @field:Json(name = "items") val items: List<Repo> = emptyList()
) {
    data class Repo(
        @field:Json(name = "id")
        val id: String,
        @field:Json(name = "name")
        val name: String,
        @field:Json(name = "full_name")
        val fullName: String,
        @field:Json(name = "description")
        val description: String,
        @field:Json(name = "language")
        val language: String,
        @field:Json(name = "forks_count")
        val forksCount: Int,
        @field:Json(name = "open_issues_count")
        val openIssuesCount: Int,
        @field:Json(name = "stargazers_count")
        val starsCount: Int,
        @field:Json(name = "watchers_count")
        val watchersCount: Int,
        @field:Json(name = "url")
        val url: String,
        @field:Json(name = "owner")
        val owner: Owner,
        @field:Json(name = "created_at")
        val createdAt: String,
        @field:Json(name = "updated_at")
        val updatedAt: String
    )

    data class Owner(
        @field:Json(name = "avatar_url")
        val avatarUrl: String,
        @field:Json(name = "login")
        val login: String
    )
}