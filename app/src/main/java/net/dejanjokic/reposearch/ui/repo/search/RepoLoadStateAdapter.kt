package net.dejanjokic.reposearch.ui.repo.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import net.dejanjokic.reposearch.R
import net.dejanjokic.reposearch.databinding.ListItemRepoLoadStateBinding
import net.dejanjokic.reposearch.ui.repo.search.RepoLoadStateAdapter.RepoLoadStateViewHolder

class RepoLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<RepoLoadStateViewHolder>()   {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): RepoLoadStateViewHolder =
        RepoLoadStateViewHolder(
            ListItemRepoLoadStateBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_repo_load_state, parent, false)), retry)

    override fun onBindViewHolder(holder: RepoLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class RepoLoadStateViewHolder(
        private val binding: ListItemRepoLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRepoItemLoadStateRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) = with(binding) {
            if (loadState is LoadState.Error) {
                textViewRepoItemLoadStateMessage.text = loadState.error.localizedMessage
            }
            progressBarRepoItemLoadState.isVisible = loadState is LoadState.Loading
            buttonRepoItemLoadStateRetry.isVisible = loadState !is LoadState.Loading
            textViewRepoItemLoadStateMessage.isVisible = loadState !is LoadState.Loading
        }
    }
}