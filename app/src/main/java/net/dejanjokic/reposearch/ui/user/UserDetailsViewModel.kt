package net.dejanjokic.reposearch.ui.user

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import net.dejanjokic.reposearch.data.GitHubRepository
import net.dejanjokic.reposearch.data.model.repo.SimpleRepoResponse
import net.dejanjokic.reposearch.data.model.user.UserDetailsResponse

@ExperimentalCoroutinesApi
class UserDetailsViewModel @ViewModelInject constructor(private val repository: GitHubRepository) : ViewModel() {

    private val _state = MutableLiveData<UserDetailsState>()
    val state: LiveData<UserDetailsState>
        get() = _state

    @ExperimentalCoroutinesApi
    fun getUserDetails(login: String) {
        _state.postValue(UserDetailsState.Loading)
        viewModelScope.launch {
            merge(repository.getUserDetails(login), repository.getUserRepos(login)).collect { result ->
                result.fold(
                    success = {
                        when (it) {
                            is List<*> -> {
                                @Suppress("UNCHECKED_CAST")
                                val repos = it as List<SimpleRepoResponse>
                                _state.postValue(UserDetailsState.Success(repos = repos))
                            }
                            is UserDetailsResponse -> {
                                _state.postValue(UserDetailsState.Success(user = it))
                            }
                        }
                    },
                    failure = { _state.postValue(UserDetailsState.Error(it.localizedMessage ?: "Unknown error")) }
                )
            }
        }
    }
}