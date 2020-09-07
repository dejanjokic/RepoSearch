package net.dejanjokic.reposearch.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.dejanjokic.reposearch.core.Constants.API.GITHUB_SEARCH_PAGE_SIZE
import net.dejanjokic.reposearch.data.model.repo.RepoDetailsResponse
import net.dejanjokic.reposearch.data.model.repo.RepoSearchResponse
import net.dejanjokic.reposearch.data.model.repo.SimpleRepoResponse
import net.dejanjokic.reposearch.data.model.user.UserDetailsResponse
import net.dejanjokic.reposearch.data.remote.GitHubApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class GitHubRepository @Inject constructor(private val api: GitHubApiService) {

    fun getSearchResultsStream(query: String, sort: String): Flow<PagingData<RepoSearchResponse.Repo>> {
        return Pager(
            config = PagingConfig(
                pageSize = GITHUB_SEARCH_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GitHubPagingSource(api, query, sort) }
        ).flow
    }

    fun getRepoDetails(login: String, name: String): Flow<SimpleResult<RepoDetailsResponse>> = flow {
        try {
            val response = api.getRepositoryDetails(login, name)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun getUserDetails(login: String): Flow<SimpleResult<UserDetailsResponse>> = flow {
        try {
            val response = api.getUserDetails(login)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    fun getUserRepos(login: String): Flow<SimpleResult<List<SimpleRepoResponse>>> = flow {
        try {
            val response = api.getUserRepos(login)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}