package net.dejanjokic.reposearch.ui.repo.details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.dejanjokic.reposearch.data.GitHubRepository

@ExperimentalCoroutinesApi
class RepoDetailsViewModel @ViewModelInject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _state = MutableLiveData<RepoDetailsState>()
    val state: LiveData<RepoDetailsState>
        get() = _state

    fun getRepoDetails(login: String, name: String) {
        _state.postValue(RepoDetailsState.Loading)
        viewModelScope.launch {
            repository.getRepoDetails(login, name).collect { result ->
                result.fold(
                    success = { item ->_state.postValue(RepoDetailsState.Success(item)) },
                    failure = { t -> _state.postValue(RepoDetailsState.Error(t.localizedMessage ?: "Error")) }
                )
            }
        }
    }
}