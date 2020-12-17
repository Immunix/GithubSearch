package com.example.githubsearch.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubsearch.databinding.ProjectLoadStateFooterBinding

class GithubProjectLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<GithubProjectLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(private val binding: ProjectLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading
                textViewError.isVisible = loadState !is LoadState.Loading
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ProjectLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}