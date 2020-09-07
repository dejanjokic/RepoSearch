package net.dejanjokic.reposearch.ui.repo.details

import net.dejanjokic.reposearch.data.model.repo.RepoDetailsResponse

sealed class RepoDetailsState {
    data class Success(val item: RepoDetailsResponse, val isStarred: Boolean = false) : RepoDetailsState()
    data class Error(val message: String) : RepoDetailsState()
    object Loading : RepoDetailsState()
}