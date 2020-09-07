package net.dejanjokic.reposearch.ui.repo.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.dejanjokic.reposearch.data.GitHubRepository
import net.dejanjokic.reposearch.data.PreferencesManager
import net.dejanjokic.reposearch.data.model.repo.RepoSearchResponse
import timber.log.Timber

@ExperimentalCoroutinesApi
class RepoSearchViewModel @ViewModelInject constructor(
    private val repository: GitHubRepository,
    private val preferences: PreferencesManager
) : ViewModel() {

    private var currentQueryValue: String? = null
    private var currentSearchResult: Flow<PagingData<RepoSearchResponse.Repo>>? = null

    val sortIndex = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            preferences.sortIndexFlow().collect {
                Timber.d("Current sort index: $it")
                sortIndex.postValue(it)
            }
        }
    }

    fun searchRepositories(query: String, sort: String, shouldForceRefresh: Boolean = false): Flow<PagingData<RepoSearchResponse.Repo>> {
        val lastResult = currentSearchResult
        if (query == currentQueryValue && lastResult != null && !shouldForceRefresh) {
            return lastResult
        }
        currentQueryValue = query
        val newResult: Flow<PagingData<RepoSearchResponse.Repo>> =
            repository.getSearchResultsStream(query, sort).cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun saveSortIndexToPreferences(index: Int) {
        preferences.saveSortIndex(index)
    }
}