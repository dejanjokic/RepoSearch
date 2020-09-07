package net.dejanjokic.reposearch.ui.repo.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import net.dejanjokic.reposearch.R
import net.dejanjokic.reposearch.data.model.repo.RepoSearchResponse
import net.dejanjokic.reposearch.data.model.repo.RepoSearchResponse.Repo
import net.dejanjokic.reposearch.databinding.ListItemRepoBinding

class RepoAdapter(
    private val userClickListener: (String) -> Unit,
    private val repoClickListener: (String) -> Unit
) : PagingDataAdapter<Repo, RepoAdapter.SearchRepoViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRepoViewHolder =
        SearchRepoViewHolder(ListItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SearchRepoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindRepo(it)
        }
    }

    inner class SearchRepoViewHolder(private val binding: ListItemRepoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindRepo(repo: Repo) = with(binding) {
            imageViewRepoItemAvatar.load(repo.owner.avatarUrl) {
                placeholder(R.drawable.ic_github)
            }
            textViewRepoItemOwnerTitle.text = repo.fullName
            textViewRepoItemDescription.text = repo.description
            textViewRepoItemForks.text = repo.forksCount.toString()
            textViewRepoItemIssues.text = repo.openIssuesCount.toString()
            textViewRepoItemStars.text = repo.starsCount.toString()
            textViewRepoItemWatchers.text = repo.watchersCount.toString()

            imageViewRepoItemAvatar.setOnClickListener { userClickListener(repo.owner.login) }
            root.setOnClickListener { repoClickListener(repo.fullName) }
        }
    }
}