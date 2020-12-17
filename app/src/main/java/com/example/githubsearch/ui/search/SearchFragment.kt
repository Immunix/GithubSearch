package com.example.githubsearch.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.example.githubsearch.R
import com.example.githubsearch.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel by viewModels<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var isSomethingSearched = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        val adapter = GithubProjectAdapter()

        binding.apply {
            recyclerSearch.setHasFixedSize(true)
            recyclerSearch.itemAnimator = null
            recyclerSearch.adapter = adapter.withLoadStateHeaderAndFooter(
                header = GithubProjectLoadStateAdapter { adapter.retry() },
                footer = GithubProjectLoadStateAdapter { adapter.retry() }
            )
        }

        viewModel.projects.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerSearch.isVisible = loadState.source.refresh is LoadState.NotLoading

                if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        adapter.itemCount < 1) {
                    recyclerSearch.isVisible = false
                    infoTextView.isVisible = true
                    infoTextView.text = resources.getString(R.string.invalid_query)
                } else if (loadState.source.refresh is LoadState.Error && isSomethingSearched) {
                    infoTextView.isVisible = true
                    infoTextView.text = resources.getString(R.string.connectivity_issues)
                } else if (!isSomethingSearched) {
                    infoTextView.isVisible = true
                    infoTextView.text = resources.getString(R.string.search_suggestion)
                } else {
                    infoTextView.isVisible = false
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerSearch.scrollToPosition(0)
                    viewModel.searchProjects(query)
                    isSomethingSearched = true
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}