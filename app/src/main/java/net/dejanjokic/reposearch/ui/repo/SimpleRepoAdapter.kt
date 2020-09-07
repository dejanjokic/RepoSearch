package net.dejanjokic.reposearch.ui.repo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.dejanjokic.reposearch.data.model.repo.SimpleRepoResponse
import net.dejanjokic.reposearch.databinding.ListItemSimpleRepoBinding

class SimpleRepoAdapter : ListAdapter<SimpleRepoResponse, SimpleRepoAdapter.SimpleRepoViewHolder>(SIMPLE_REPO_COMPARATOR) {

    companion object {
        private val SIMPLE_REPO_COMPARATOR = object : DiffUtil.ItemCallback<SimpleRepoResponse>() {
            override fun areItemsTheSame(oldItem: SimpleRepoResponse, newItem: SimpleRepoResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SimpleRepoResponse, newItem: SimpleRepoResponse): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleRepoViewHolder =
        SimpleRepoViewHolder(ListItemSimpleRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SimpleRepoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SimpleRepoViewHolder(private val binding: ListItemSimpleRepoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SimpleRepoResponse) = with(binding) {
            textViewSimpleItemName.text = item.name
            textViewSimpleItemDescription.text = item.description
            textViewSimpleItemLanguage.text = item.language
        }
    }
}