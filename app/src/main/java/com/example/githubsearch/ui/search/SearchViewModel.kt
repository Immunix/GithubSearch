package com.example.githubsearch.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.githubsearch.data.GithubRepository

class SearchViewModel @ViewModelInject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val projects = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString).cachedIn(viewModelScope)
    }

    fun searchProjects(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val DEFAULT_QUERY = "linux"
    }
}