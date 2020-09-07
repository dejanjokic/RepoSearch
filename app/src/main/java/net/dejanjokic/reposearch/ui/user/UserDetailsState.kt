package net.dejanjokic.reposearch.ui.user

import net.dejanjokic.reposearch.data.model.repo.SimpleRepoResponse
import net.dejanjokic.reposearch.data.model.user.UserDetailsResponse

sealed class UserDetailsState {
    data class Success(val user: UserDetailsResponse? = null, val repos: List<SimpleRepoResponse>? = emptyList()) : UserDetailsState()
    data class Error(val message: String) : UserDetailsState()
    object Loading : UserDetailsState()
}