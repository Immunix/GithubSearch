package com.example.githubsearch.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.githubsearch.R
import com.example.githubsearch.data.GithubSearchResult
import com.example.githubsearch.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    GithubProjectAdapter.OnItemClickListener {

    companion object {
        private var isSomethingSearched = false
    }

    private val viewModel by viewModels<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        val adapter = GithubProjectAdapter(this)

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
                    adapter.itemCount < 1
                ) {
                    recyclerSearch.gone()
                    infoTextView.visible()
                    infoTextView.text = getString(R.string.invalid_query)
                } else if (loadState.source.refresh is LoadState.Error && isSomethingSearched) {
                    infoTextView.visible()
                    infoTextView.text = getString(R.string.connectivity_issues)
                } else if (recyclerSearch.isEmpty() && !isSomethingSearched) {
                    infoTextView.visible()
                    infoTextView.text = getString(R.string.search_suggestion)
                } else {
                    infoTextView.gone()
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onItemClick(project: GithubSearchResult) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(project)
        findNavController().navigate(action)
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
                isSomethingSearched = false
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun View.visible() {
    isVisible = true
}

fun View.gone() {
    isGone = true
}