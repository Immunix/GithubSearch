package com.example.githubsearch.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubsearch.R
import com.example.githubsearch.data.GithubSearchResult
import com.example.githubsearch.databinding.SearchCellBinding

class GithubProjectAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<GithubSearchResult, GithubProjectAdapter.ProjectViewHolder>(PROJECT_COMPARATOR) {

    inner class ProjectViewHolder(private val binding: SearchCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(project: GithubSearchResult) {
            binding.apply {
                Glide.with(itemView)
                    .load(project.owner.avatar_url)
                    .centerCrop()
                    .override(240, 240)
                    .error(R.drawable.ic_error)
                    .into(userAvatar)

                projectFullName.text = project.full_name
                projectDescription.text = project.description
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(project: GithubSearchResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = SearchCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    companion object {
        private val PROJECT_COMPARATOR = object : DiffUtil.ItemCallback<GithubSearchResult>() {
            override fun areItemsTheSame(
                oldItem: GithubSearchResult,
                newItem: GithubSearchResult
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: GithubSearchResult,
                newItem: GithubSearchResult
            ) = oldItem == newItem
        }
    }
}