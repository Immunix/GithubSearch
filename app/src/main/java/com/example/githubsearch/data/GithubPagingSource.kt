package com.example.githubsearch.data

import androidx.paging.PagingSource
import com.example.githubsearch.api.GithubApi
import retrofit2.HttpException
import java.io.IOException

private const val GITHUB_STARTING_PAGE_INDEX = 1

class GithubPagingSource(
    private val githubApi: GithubApi,
    private val query: String
) : PagingSource<Int, GithubSearchResult>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubSearchResult> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX

        return try {
            val response = githubApi.searchProjects(query, position, params.loadSize)
            val projects = response.items

            LoadResult.Page(
                data = projects,
                prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (projects.isEmpty()) null else position + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}