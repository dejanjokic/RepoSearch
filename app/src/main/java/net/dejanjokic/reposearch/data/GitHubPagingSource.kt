package net.dejanjokic.reposearch.data

import androidx.paging.PagingSource
import net.dejanjokic.reposearch.core.Constants.API.GITHUB_SEARCH_STARTING_PAGE_INDEX
import net.dejanjokic.reposearch.data.model.repo.RepoSearchResponse
import net.dejanjokic.reposearch.data.remote.GitHubApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GitHubPagingSource @Inject constructor(
    private val api: GitHubApiService,
    private val query: String,
    private val sort: String
) : PagingSource<Int, RepoSearchResponse.Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepoSearchResponse.Repo> {
        val position = params.key ?: GITHUB_SEARCH_STARTING_PAGE_INDEX
        return try {
            val response = api.getRepositorySearchResults(query, position, params.loadSize, sort)
            val repos = response.items
            LoadResult.Page(
                data = repos,
                prevKey = if (position == GITHUB_SEARCH_STARTING_PAGE_INDEX) null else position -1,
                nextKey = if (repos.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}