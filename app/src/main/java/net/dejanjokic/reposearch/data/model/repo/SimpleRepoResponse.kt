package net.dejanjokic.reposearch.data.model.repo

import com.squareup.moshi.Json

data class SimpleRepoResponse(
    @field:Json(name = "id") val id: Long,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "description") val description: String?,
    @field:Json(name = "full_name") val fullName: String,
    @field:Json(name = "language") val language: String,
    @field:Json(name = "private") val isPrivate: Boolean
)