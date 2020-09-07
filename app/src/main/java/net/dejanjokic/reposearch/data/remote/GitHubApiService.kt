package net.dejanjokic.reposearch.data.remote

import net.dejanjokic.reposearch.data.model.repo.RepoDetailsResponse
import net.dejanjokic.reposearch.data.model.repo.RepoSearchResponse
import net.dejanjokic.reposearch.data.model.repo.SimpleRepoResponse
import net.dejanjokic.reposearch.data.model.user.UserDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    @GET("search/repositories")
    suspend fun getRepositorySearchResults(
        @Query("q") searchQuery: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("sort") sortBy: String = "stars",
        @Query("order") orderBy: String = "desc"
    ): RepoSearchResponse

    @GET("repos/{login}/{name}")
    suspend fun getRepositoryDetails(
        @Path("login") login: String,
        @Path("name") name: String
    ): RepoDetailsResponse

    @GET("users/{login}")
    suspend fun getUserDetails(
        @Path("login") login: String
    ): UserDetailsResponse

    @GET("users/{login}/repos")
    suspend fun getUserRepos(
        @Path("login") login: String
    ): List<SimpleRepoResponse>
}